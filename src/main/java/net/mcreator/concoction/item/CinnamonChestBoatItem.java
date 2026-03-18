package net.mcreator.concoction.item;

import java.util.List;

import net.mcreator.concoction.entity.CinnamonChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CinnamonChestBoatItem extends Item {
	private static final java.util.function.Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

	public CinnamonChestBoatItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemStack);
		}

		Vec3 viewVector = player.getViewVector(1.0F);
		List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(viewVector.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
		if (!entities.isEmpty()) {
			Vec3 eyePosition = player.getEyePosition();
			for (Entity entity : entities) {
				AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
				if (aabb.contains(eyePosition)) {
					return InteractionResultHolder.pass(itemStack);
				}
			}
		}

		if (hitResult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemStack);
		}

		CinnamonChestBoatEntity boat = new CinnamonChestBoatEntity(level, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
		boat.setYRot(player.getYRot());
		if (!level.noCollision(boat, boat.getBoundingBox())) {
			return InteractionResultHolder.fail(itemStack);
		}

		if (!level.isClientSide()) {
			level.addFreshEntity(boat);
			level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());
			if (!player.getAbilities().instabuild) {
				itemStack.shrink(1);
			}
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
	}
}
