package net.mcreator.concoction.custom;

import net.mcreator.concoction.entity.SunstruckEntity;
import net.mcreator.concoction.init.ConcoctionModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.mcreator.concoction.block.SunflowerBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.server.level.ServerLevel;

public class CustomAttackSunflowerGoal extends Goal {

    private final Zombie zombie;
    private BlockPos targetBlockPos;
    private int tickCounter = 0;
    private int transformCounter = 0;

    public CustomAttackSunflowerGoal (Zombie zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean canUse() {
        Level level = this.zombie.level();
        BlockPos zombiePos = this.zombie.blockPosition();

        for (int x = -8; x <= 8; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos pos = zombiePos.offset(x, y, z);
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() instanceof SunflowerBlock && blockState.getValue(SunflowerBlock.HALF) == DoubleBlockHalf.UPPER && this.zombie.getType().equals(EntityType.ZOMBIE)) {
                        this.targetBlockPos = pos;
                        this.zombie.targetSelector.removeAllGoals(goal -> true);
                        return true;
                    }
                }
            }
        }
        if (this.zombie.getType().equals(EntityType.ZOMBIE)) {
            this.zombie.targetSelector.removeAllGoals(goal -> true);
            this.zombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this.zombie, Player.class, true));
            this.zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this.zombie, Villager.class, false));
            this.zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this.zombie, WanderingTrader.class, false));
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.targetBlockPos == null) return;

        if (++tickCounter % 20 != 0) return;

        this.zombie.getLookControl().setLookAt(
                this.targetBlockPos.getX(),
                this.targetBlockPos.getY(),
                this.targetBlockPos.getZ()
        );

        if (this.zombie.distanceToSqr(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) > 8 && this.zombie.getType().equals(EntityType.ZOMBIE)) {
            this.transformCounter = 0;
            this.zombie.getNavigation().moveTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 1.0D);
        } else {
            ++this.transformCounter;
            
            // Добавляем частицы во время трансформации
            if (this.transformCounter > 0 && this.transformCounter < 10) {
                Level level = this.zombie.level();
                if (level instanceof ServerLevel serverLevel) {
                    // Зеленые частицы (эффект удобрения)
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.zombie.getX(),
                            this.zombie.getY() + 1.0D,
                            this.zombie.getZ(),
                            8,
                            0.3D, 0.3D, 0.3D,
                            0.1D);
                }
            }

            if (this.transformCounter >= 10 && this.zombie.getType().equals(EntityType.ZOMBIE)) {
                EntityType<?> type = ConcoctionModEntities.SUNSTRUCK.get();
                var sunstruck = type.create(this.zombie.level());
                sunstruck.moveTo(this.zombie.getX(), this.zombie.getY(), this.zombie.getZ(), this.zombie.getYRot(), this.zombie.getXRot());
                
                // Финальные эффекты трансформации
                Level level = this.zombie.level();
                if (level instanceof ServerLevel serverLevel) {
                    // Взрыв зеленых частиц при завершении трансформации
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.zombie.getX(),
                            this.zombie.getY() + 1.0D,
                            this.zombie.getZ(),
                            30,
                            0.5D, 0.5D, 0.5D,
                            0.2D);
                }

                if (this.zombie.isBaby() && sunstruck instanceof SunstruckEntity sunstruckEntity){
                    sunstruckEntity.setBaby(true);
                }

                // Звуки трансформации
                this.zombie.playSound(SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, 1.0f, 1.0f);
                this.zombie.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 0.8f);
                
                this.zombie.remove(Entity.RemovalReason.DISCARDED);
                this.zombie.level().addFreshEntity(sunstruck);
                this.transformCounter = 0;
            }
        }
    }

}
