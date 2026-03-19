package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        // Overgrown tools keep the last durability point reserved and stop before fully breaking.
        if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
            return false;
        }

        if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 2) {
            itemstack.setDamageValue(itemstack.getMaxDamage() - 1);
            return false;
        }

        boolean result = super.hurtEnemy(itemstack, target, attacker);
        if (result && !attacker.level().isClientSide && attacker.getRandom().nextFloat() < 0.5f) {
            applySunstruck(target);
        }

        return result;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
        if (itemstack.getMaxDamage() - itemstack.getDamageValue() <= 1) {
            return false;
        }

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

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged || oldStack.getItem() != newStack.getItem()) {
            return true;
        }

        return newStack.getDamageValue() > oldStack.getDamageValue();
    }

    private static void applySunstruck(LivingEntity target) {
        if (target == null) {
            return;
        }

        final int durationTicks = 12 * 20;
        final int maxAmplifier = 5;

        MobEffectInstance current = target.getEffect(ConcoctionModMobEffects.SUNSTRUCK_EFFECT);
        int newAmplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, maxAmplifier);

        target.addEffect(new MobEffectInstance(
                ConcoctionModMobEffects.SUNSTRUCK_EFFECT,
                durationTicks,
                newAmplifier,
                false,
                true,
                true
        ));
    }
}
