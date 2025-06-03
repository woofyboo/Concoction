package net.mcreator.concoction.custom;

import net.mcreator.concoction.entity.SunstruckEntity;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModEntities;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class CustomAttackSunflowerGoal extends Goal {

    private final Zombie zombie;
    private Optional<BlockPos> target;
    private int tickCounter = 0;
    private int transformCounter = 0;

    public CustomAttackSunflowerGoal (Zombie zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean canUse() {
        Level level = this.zombie.level();
        if (level.isClientSide()) return false;

        target = BlockPos.findClosestMatch(this.zombie.blockPosition(), 8, 8, blockPos -> level.getBlockState(blockPos).is(ConcoctionModBlocks.SUNFLOWER));

        if (target.isPresent()) {
            if (this.zombie.getType().equals(EntityType.ZOMBIE)) {
                this.zombie.targetSelector.removeAllGoals(goal -> true);
                this.zombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this.zombie, Player.class, true));
                this.zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this.zombie, Villager.class, false));
                this.zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this.zombie, WanderingTrader.class, false));
            }
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        target.ifPresent(targetBlockPos -> {

            if (this.zombie.getType().equals(EntityType.ZOMBIE)) {
                this.transformCounter = 0;
                this.zombie.getNavigation().moveTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1.0D);
                this.zombie.getLookControl().setLookAt(
                        targetBlockPos.getX(),
                        targetBlockPos.getY()+1,
                        targetBlockPos.getZ()
                );


            } //else {
//                ++this.transformCounter;
//
//                if (transformCounter > 0 && transformCounter < 5) {
//                    Level level = this.zombie.level();
//                    if (level instanceof ServerLevel serverLevel) {
//                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
//                                this.zombie.getX(),
//                                this.zombie.getY() + 1.0D,
//                                this.zombie.getZ(),
//                                8,
//                                0.3D, 0.3D, 0.3D,
//                                0.1D);
//                    }
//                }
//
//                if (this.transformCounter >= 5 && this.zombie.getType().equals(EntityType.ZOMBIE)) {
//                    EntityType<?> type = ConcoctionModEntities.SUNSTRUCK.get();
//                    var sunstruck = type.create(this.zombie.level());
//                    assert sunstruck != null;
//                    sunstruck.moveTo(this.zombie.getX(), this.zombie.getY(), this.zombie.getZ(), this.zombie.getYRot(), this.zombie.getXRot());
//
//
//                    Level level = this.zombie.level();
//                    if (level instanceof ServerLevel serverLevel) {
//                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
//                                this.zombie.getX(),
//                                this.zombie.getY() + 1.0D,
//                                this.zombie.getZ(),
//                                30,
//                                0.5D, 0.5D, 0.5D,
//                                0.2D);
//
//                        BlockPos zombiePos = this.zombie.blockPosition();
//                        serverLevel.getPlayers(player ->
//                                player.distanceToSqr(zombiePos.getX(), zombiePos.getY(), zombiePos.getZ()) <= 64
//                        ).forEach(player -> {
//                            Utils.addAchievement(player, "concoction:sunstruck_transformed");
//                        });
//                    }
//
//                    if (this.zombie.isBaby() && sunstruck instanceof SunstruckEntity sunstruckEntity){
//                        sunstruckEntity.setBaby(true);
//                    }
//
//                    // Копируем инвентарь зомби в Sunstruck
//                    if (sunstruck instanceof SunstruckEntity sunstruckEntity) {
//                        sunstruckEntity.setItemInHand(this.zombie.getUsedItemHand(), this.zombie.getItemInHand(this.zombie.getUsedItemHand()).copy());
//                        sunstruckEntity.setItemInHand(this.zombie.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ?
//                                        net.minecraft.world.InteractionHand.OFF_HAND : net.minecraft.world.InteractionHand.MAIN_HAND,
//                                this.zombie.getItemInHand(this.zombie.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ?
//                                        net.minecraft.world.InteractionHand.OFF_HAND : net.minecraft.world.InteractionHand.MAIN_HAND).copy());
//
//                        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
//                            sunstruckEntity.setItemSlot(slot, this.zombie.getItemBySlot(slot).copy());
//                        }
//                    }
//
//                    this.zombie.playSound(SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, 1.0f, 1.0f);
//                    this.zombie.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 0.8f);
//
//                    this.zombie.remove(Entity.RemovalReason.DISCARDED);
//                    this.zombie.level().addFreshEntity(sunstruck);
//                    this.transformCounter = 0;
//                }
//            }
        });

    }

}
