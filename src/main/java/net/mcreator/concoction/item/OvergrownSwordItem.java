package net.mcreator.concoction.item;

import net.mcreator.concoction.utils.Utils;
import net.mcreator.concoction.init.ConcoctionModMobEffects; // === Sunstruck ===

import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;          // === Sunstruck ===

public class OvergrownSwordItem extends SwordItem {
	private static final Tier TOOL_TIER = new Tier() {
		@Override
		public int getUses() {
			return 131;
		}
		@Override
		public float getSpeed() {
			return 6f;
		}

		@Override
		public float getAttackDamageBonus() {
			return 0;
		}

		@Override
		public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
		}

		@Override
		public int getEnchantmentValue() {
			return 15;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of();
		}
	};

	public OvergrownSwordItem() {
		super(TOOL_TIER, new Item.Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 4f, -2.2f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity target, LivingEntity attacker) {
		// Если прочность равна 1, инструмент не работает
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
			return false;
		}

		// Если прочность снизится до 1, устанавливаем её и не даём опуститься ниже
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 2) {
			itemstack.setDamageValue(itemstack.getMaxDamage() - 1);
			return false;
		}

		boolean result = super.hurtEnemy(itemstack, target, attacker);

		// === Sunstruck ===
		// Накладываем эффект только при успешном ударе, на сервере, с шансом 50%
		if (result && !attacker.level().isClientSide && attacker.getRandom().nextFloat() < 0.5f) {
			applySunstruck(target);
		}
		// === /Sunstruck ===

		return result;
	}

	@Override
	public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
		// Если прочность равна 1, инструмент не работает
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
			return false;
		}

		// Если прочность снизится до 1, устанавливаем её и не даём опуститься ниже
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 2) {
			itemstack.setDamageValue(itemstack.getMaxDamage() - 1);
			return false;
		}
		return super.mineBlock(itemstack, world, blockstate, pos, entity);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return Utils.getColor(stack);
	}

	// === Sunstruck ===
	private static void applySunstruck(LivingEntity target) {
		if (target == null) return;

		// 12 секунд = 240 тиков
		final int DURATION_TICKS = 12 * 20;
		final int MAX_AMP = 5;

		MobEffectInstance current = target.getEffect(ConcoctionModMobEffects.SUNSTRUCK_EFFECT);
		int newAmp = 0;

		if (current != null) {
			newAmp = Math.min(current.getAmplifier() + 1, MAX_AMP);
		}

		// Создаём новую инстанцию с обновлённой длительностью и амплифаером.
		// Флаги можно настроить по вкусу; здесь: не ambient, частицы включены, иконка включена.
		MobEffectInstance updated = new MobEffectInstance(
				ConcoctionModMobEffects.SUNSTRUCK_EFFECT,
				DURATION_TICKS,
				newAmp,
				false,  // ambient
				true,   // showParticles
				true    // showIcon
		);

		target.addEffect(updated);
	}
	// === /Sunstruck ===
}
