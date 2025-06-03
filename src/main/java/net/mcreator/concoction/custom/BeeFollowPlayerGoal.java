package net.mcreator.concoction.custom;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BeeFollowPlayerGoal extends Goal {

    private Bee bee;
    private Optional<LivingEntity> target;

    public BeeFollowPlayerGoal (Bee bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        Level level = bee.level();
        if (level.isClientSide()) return false;

        AABB aabb = bee.getBoundingBox();

        target = level.getEntitiesOfClass(
                LivingEntity.class, aabb.inflate(8),
                livingEntity -> livingEntity.hasEffect(ConcoctionModMobEffects.SWEETNESS)
        ).stream().min(Comparator.comparingDouble(entity -> entity.distanceToSqr(bee)));

        if (target.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        target.ifPresent(livingEntity -> bee.getNavigation().moveTo(livingEntity.getX(), livingEntity.getY()+1.4, livingEntity.getZ(), 1.0));
    }
}
