package net.mcreator.concoction.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class OvergrownHoeItem extends HoeItem {
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


		public boolean canBeDepleted(){
			return false;
		}
	};

	public OvergrownHoeItem() {
		super(TOOL_TIER, new Item.Properties().attributes(DiggerItem.createAttributes(TOOL_TIER, 0f, -3f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		// Если прочность равна 1, инструмент не работает
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
			return false;
		}
		
		boolean result = super.hurtEnemy(itemstack, entity, sourceentity);
		
		// Проверяем прочность после действия и никогда не даём ей упасть до 0
		if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
			itemstack.setDamageValue(itemstack.getMaxDamage() - 1);
			return false;
		}
		
		return result;
	}

	@Override
	public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
		// Если прочность равна 1, инструмент не работает
		if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
			return false;
		}
		
		boolean result = super.mineBlock(itemstack, world, blockstate, pos, entity);
		
		// Проверяем прочность после действия и никогда не даём ей упасть до 0
		if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
			itemstack.setDamageValue(itemstack.getMaxDamage() - 1);
			return false;
		}
		
		return result;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		// Светло-желтый (бело-желтый) цвет для полоски прочности
		return 0xFFFFC0; // RGB-код для светло-желтого цвета
	}

}
