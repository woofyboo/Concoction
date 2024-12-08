package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.concoction.init.ConcoctionModMobEffects;

import javax.annotation.Nullable;

@EventBusSubscriber
public class SweetFoodListProcedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity(), event.getItem());
		}
	}

	public static void execute(Entity entity, ItemStack itemstack) {
		execute(null, entity, itemstack);
	}

	private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:sweetness/sweetened")))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, 240, 0, false, true));
		} else if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:sweetness/sweet")))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, 240, 1, false, true));
		} else if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:sweetness/sugary")))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, 600, 0, false, true));
		} else if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:sweetness/candy")))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, 600, 1, false, true));
		}
	}
}
