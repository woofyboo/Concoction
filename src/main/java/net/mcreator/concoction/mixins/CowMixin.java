package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModGameRules;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.interfaces.ICowMilkLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public class CowMixin implements ICowMilkLevel {
    @Unique
    private int concoction$milkLevel = 3;

    @Unique
    private long concoction$lastMilkedTime = 0L;

    @Override
    public int concoction$getMilkLevel() {
        return concoction$milkLevel;
    }

    @Override
    public void concoction$setMilkLevel(int level) {
        concoction$milkLevel = Math.max(0, Math.min(3, level));
    }

    @Override
    public void concoction$incrementMilkLevel() {
        concoction$setMilkLevel(concoction$milkLevel + 1);
    }

    @Override
    public void concoction$decrementMilkLevel() {
        concoction$setMilkLevel(concoction$milkLevel - 1);
    }

    @Override
    public long concoction$getLastMilkedTime() {
        return concoction$lastMilkedTime;
    }

    @Override
    public void concoction$setLastMilkedTime(long time) {
        concoction$lastMilkedTime = time;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void concoction$onMilk(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Cow cow = (Cow) (Object) this;
        Level level = cow.level();
        long now = level.getGameTime();

        if (!level.isClientSide) {
            int milkingInterval = 2400;
            if (level instanceof ServerLevel serverLevel) {
                int rule = serverLevel.getGameRules().getInt(ConcoctionModGameRules.MILKING_INTERVAL);
                milkingInterval = (rule == 0 ? 2400 : rule);
            }

            if (cow.isBaby()) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }

            // апдейтим уровень молока по времени
            if (concoction$milkLevel < 3) {
                long intervalsPassed = (now - concoction$lastMilkedTime) / milkingInterval;
                if (intervalsPassed > 0) {
                    concoction$setMilkLevel(Math.min(3, concoction$milkLevel + (int) intervalsPassed));
                    long remainder = (now - concoction$lastMilkedTime) % milkingInterval;
                    concoction$setLastMilkedTime(now - remainder);
                }
            }

            ItemStack stack = player.getItemInHand(hand);

            // ведро
            if (stack.getItem() == Items.BUCKET) {
                if (concoction$milkLevel == 3) {
                    concoction$setMilkLevel(0);
                    concoction$setLastMilkedTime(now);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    player.addItem(new ItemStack(Items.MILK_BUCKET));

                    level.playSound(null, cow, SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0F,
                            1.0F + (level.random.nextFloat() - 0.5F) * 0.2F);
                    level.playSound(null, cow, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F);

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

            // бутылка
            if (stack.getItem() == Items.GLASS_BOTTLE) {
                if (concoction$milkLevel > 0) {
                    concoction$decrementMilkLevel();
                    concoction$setLastMilkedTime(now);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    player.addItem(new ItemStack(ConcoctionModItems.MILK_BOTTLE.asItem()));

                    level.playSound(null, cow, SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0F,
                            1.0F + (level.random.nextFloat() - 0.5F) * 0.2F);
                    level.playSound(null, cow, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F);

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
        } else {
            // на клиенте просто говорим "успех", чтобы не было десинков по руке/анимации
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
