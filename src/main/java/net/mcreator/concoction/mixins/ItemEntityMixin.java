package net.mcreator.concoction.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getItem();

    @Unique
    private int concoction$tickCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void concoction$tick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;

        // только сервер, чтобы не плодить рассинхрон
        if (itemEntity.level().isClientSide()) {
            return;
        }

        ItemStack stackInWorld = itemEntity.getItem();
        // интересуют только предметы из тега c:tableware
        if (stackInWorld.isEmpty() ||
                !stackInWorld.is(ItemTags.create(ResourceLocation.parse("c:tableware")))) {
            return;
        }

        BlockPos itemPos = itemEntity.blockPosition();

        Optional<BlockPos> nearestChest = BlockPos.findClosestMatch(
                itemPos,
                10, 10,
                pos -> itemEntity.level()
                        .getBlockState(pos)
                        .is(BlockTags.create(ResourceLocation.parse("concoction:kitchen_cabinets")))
        );

        if (nearestChest.isEmpty()) {
            // рядом нет шкафчиков – просто тикаем дальше
            return;
        }

        if (!(itemEntity.level().getBlockEntity(nearestChest.get()) instanceof Container container)) {
            return;
        }

        // ждём 10+ секунд рядом с шкафчиком
        concoction$tickCounter++;
        if (concoction$tickCounter <= 200) {
            return;
        }

        // пытаемся целиком утащить стак в шкафчик
        int toMove = stackInWorld.getCount();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slotStack = container.getItem(i);

            // слота пустой — просто кладём туда весь стак
            if (slotStack.isEmpty()) {
                if (!container.canPlaceItem(i, stackInWorld)) {
                    continue;
                }

                ItemStack copy = stackInWorld.copy();
                copy.setCount(toMove);
                container.setItem(i, copy);

                itemEntity.discard();
                concoction$tickCounter = 0;
                return;
            }

            // слот занят — складываем ТОЛЬКО если это тот же предмет + те же компоненты
            if (!ItemStack.isSameItemSameComponents(slotStack, stackInWorld)) {
                continue;
            }
            if (!container.canPlaceItem(i, stackInWorld)) {
                continue;
            }

            int max = slotStack.getMaxStackSize();
            int freeSpace = max - slotStack.getCount();
            if (freeSpace < toMove) {
                // этот слот не может вместить весь стак — идём дальше
                continue;
            }

            slotStack.grow(toMove);
            container.setItem(i, slotStack);

            itemEntity.discard();
            concoction$tickCounter = 0;
            return;
        }

        // если до сюда дошли — ни один слот не смог принять ПОЛНЫЙ стак,
        // поэтому ничего не двигаем.
    }
}
