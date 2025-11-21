package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.interfaces.ICowMilkLevel;
import net.mcreator.concoction.init.ConcoctionModGameRules;

import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Goat.class)
public class GoatMixin implements ICowMilkLevel {

    @Unique private int  concoction$milkLevel = 3;
    @Unique private long concoction$lastMilkedTime = 0L;

    @Override public int  concoction$getMilkLevel() { return concoction$milkLevel; }
    @Override public void concoction$setMilkLevel(int level) { concoction$milkLevel = Math.max(0, Math.min(3, level)); }
    @Override public void concoction$incrementMilkLevel() { concoction$setMilkLevel(concoction$milkLevel + 1); }
    @Override public void concoction$decrementMilkLevel() { concoction$setMilkLevel(concoction$milkLevel - 1); }

    @Override public long concoction$getLastMilkedTime() { return concoction$lastMilkedTime; }
    @Override public void concoction$setLastMilkedTime(long t) { concoction$lastMilkedTime = t; }

    // -------------------  MAIN MILKING LOGIC  -------------------

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void concoction$onMilk(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Goat goat = (Goat)(Object)this;
        Level level = goat.level();
        long now = level.getGameTime();

        if (!level.isClientSide) {
            int milkingInterval = 2400;

            if (level instanceof ServerLevel serverLevel) {
                int rule = serverLevel.getGameRules().getInt(ConcoctionModGameRules.MILKING_INTERVAL);
                milkingInterval = rule == 0 ? 2400 : rule;
            }

            if (goat.isBaby()) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }

            // восстановление молока по времени
            if (concoction$milkLevel < 3) {
                long intervals = (now - concoction$lastMilkedTime) / milkingInterval;
                if (intervals > 0) {
                    concoction$setMilkLevel(Math.min(3, concoction$milkLevel + (int)intervals));
                    concoction$setLastMilkedTime(now - (now - concoction$lastMilkedTime) % milkingInterval);
                }
            }

            ItemStack stack = player.getItemInHand(hand);

            // ВЕДРО
            if (stack.is(Items.BUCKET)) {
                if (concoction$milkLevel == 3) {
                    concoction$setMilkLevel(0);
                    concoction$setLastMilkedTime(now);

                    if (!player.isCreative()) stack.shrink(1);
                    player.addItem(new ItemStack(Items.MILK_BUCKET));

                    level.playSound(null, goat, SoundEvents.COW_MILK, SoundSource.PLAYERS, 1F, 1F);
                    level.playSound(null, goat, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1F, 1F);

                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    player.displayClientMessage(
                            Component.translatable("message.concoction.milk_not_ready_bucket"),
                            true
                    );
                    cir.setReturnValue(InteractionResult.FAIL);
                }
                return;
            }

            // БУТЫЛКА
            if (stack.is(Items.GLASS_BOTTLE)) {
                if (concoction$milkLevel > 0) {
                    concoction$decrementMilkLevel();
                    concoction$setLastMilkedTime(now);

                    if (!player.isCreative()) stack.shrink(1);
                    player.addItem(new ItemStack(ConcoctionModItems.MILK_BOTTLE.get()));

                    level.playSound(null, goat, SoundEvents.COW_MILK, SoundSource.PLAYERS, 1F, 1F);
                    level.playSound(null, goat, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1F, 1F);

                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    player.displayClientMessage(
                            Component.translatable("message.concoction.milk_not_ready_bottle"),
                            true
                    );
                    cir.setReturnValue(InteractionResult.FAIL);
                }
                return;
            }
        }

        // на клиенте просто подтверждаем, чтобы не ломать анимацию
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    // -------------------  SAVE / LOAD NBT  -------------------

}
