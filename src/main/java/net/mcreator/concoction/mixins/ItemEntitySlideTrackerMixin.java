package net.mcreator.concoction.mixins;

import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mcreator.concoction.block.SoapLayerBlock;
import net.mcreator.concoction.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntitySlideTrackerMixin {

    // Порог «реального скольжения» и длительность (3 сек = 60 тиков)
    private static final double SPEED_EPS = 0.010; // было 0.02 — чуть смягчили
    private static final int REQUIRED_TICKS = 60;

    @Inject(method = "tick", at = @At("TAIL"))
    private void concoction$trackSoapSliding(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!(self.level() instanceof ServerLevel level)) return;

        var nbt = self.getPersistentData();
        if (!nbt.hasUUID("concoction_owner")) return; // не наш «брошенный» предмет

        // Нужен контакт с землёй (на мыльном слое без коллизии "земля" — это блок под ним)
        if (!self.onGround()) {
            // можно сбрасывать прогресс, если оторвался:
            // nbt.putInt("concoction_slide_ticks", 0);
            return;
        }

        // Проверка «бутерброда»: над опорой находится мыльный слой
        BlockPos below = BlockPos.containing(self.getX(), self.getY() - 0.2D, self.getZ());
        BlockPos above = below.above();
        BlockState aboveState = level.getBlockState(above);
        if (!(aboveState.getBlock() instanceof SoapLayerBlock)) {
            // вне мыла прогресс не идёт
            return;
        }

        // Горизонтальная скорость предмета
        var mot = self.getDeltaMovement();
        double speed = Math.hypot(mot.x, mot.z);
        if (speed < SPEED_EPS) return;

        // Копим тики скольжения
        int ticks = nbt.getInt("concoction_slide_ticks") + 1;
        nbt.putInt("concoction_slide_ticks", ticks);

        if (ticks >= REQUIRED_TICKS) {
            UUID ownerId = nbt.getUUID("concoction_owner");
            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(ownerId);
            if (owner != null) {
                Utils.addAchievement(owner, "concoction:slippery_slope");
            }
            // очищаем, чтобы не спамить повторно
            nbt.remove("concoction_owner");
            nbt.remove("concoction_slide_ticks");
        }
    }
}
