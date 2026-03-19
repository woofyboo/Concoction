package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.OvergrownAxeItem;
import net.mcreator.concoction.item.OvergrownHoeItem;
import net.mcreator.concoction.item.OvergrownPickaxeItem;
import net.mcreator.concoction.item.OvergrownShovelItem;
import net.mcreator.concoction.item.OvergrownSwordItem;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public final class OvergrownToolHandler {
    private static final TagKey<Block> WILDLIFE_PLANTS =
            TagKey.create(Registries.BLOCK, ResourceLocation.parse("concoction:wildlife_plants"));

    private static final int UPDATE_INTERVAL = 200;

    private OvergrownToolHandler() {
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack selectedItem = player.getInventory().getSelected();
        if (isNearlyBrokenOvergrownHoe(selectedItem)) {
            event.setCanceled(true);
        }

        if (event.getState().is(WILDLIFE_PLANTS)) {
            Utils.grantAdvancement(player, "concoction:new_crops");
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (shouldBlockLeftClick(event.getEntity().getInventory().getSelected())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (shouldBlockRightClick(event.getEntity().getInventory().getSelected())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (shouldBlockEntityInteraction(event.getEntity().getInventory().getSelected())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer)) {
            return;
        }

        if (player.tickCount % UPDATE_INTERVAL == 0 && Utils.isPlayerSunPowered(player)) {
            int multiplier = player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS) ? 2 : 1;
            repairInventoryIfPossible(player, multiplier, player.level());
        }
    }

    public static boolean isOvergrownTool(ItemStack stack) {
        return stack.getItem() instanceof OvergrownHoeItem
                || stack.getItem() instanceof OvergrownAxeItem
                || stack.getItem() instanceof OvergrownPickaxeItem
                || stack.getItem() instanceof OvergrownShovelItem
                || stack.getItem() instanceof OvergrownSwordItem;
    }

    public static boolean canChargeFromSun(Player player, ItemStack stack) {
        if (stack.isEmpty() || !isOvergrownTool(stack) || stack.getDamageValue() <= 0 || !Utils.isPlayerSunPowered(player)) {
            return false;
        }

        var enchantments = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return stack.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.MENDING)) == 0
                && stack.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.UNBREAKING)) == 0;
    }

    public static boolean shouldCancelAttack(ItemStack stack) {
        return isNearlyBroken(stack) && isOvergrownTool(stack);
    }

    private static boolean shouldBlockLeftClick(ItemStack stack) {
        return isNearlyBroken(stack) && isOvergrownTool(stack);
    }

    private static boolean shouldBlockRightClick(ItemStack stack) {
        return isNearlyBroken(stack) && (stack.getItem() instanceof OvergrownHoeItem
                || stack.getItem() instanceof OvergrownAxeItem
                || stack.getItem() instanceof OvergrownShovelItem);
    }

    private static boolean shouldBlockEntityInteraction(ItemStack stack) {
        return isNearlyBroken(stack) && (stack.getItem() instanceof OvergrownHoeItem
                || stack.getItem() instanceof OvergrownAxeItem
                || stack.getItem() instanceof OvergrownPickaxeItem
                || stack.getItem() instanceof OvergrownShovelItem);
    }

    private static boolean isNearlyBrokenOvergrownHoe(ItemStack stack) {
        return stack.getItem() instanceof OvergrownHoeItem && isNearlyBroken(stack);
    }

    private static boolean isNearlyBroken(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue() <= 1;
    }

    private static void repairInventoryIfPossible(Player player, int multiplier, Level level) {
        Inventory inventory = player.getInventory();

        for (ItemStack stack : inventory.items) {
            repairIfPossible(stack, multiplier, level);
        }

        for (ItemStack stack : inventory.offhand) {
            repairIfPossible(stack, multiplier, level);
        }
    }

    private static void repairIfPossible(ItemStack stack, int multiplier, Level level) {
        if (!isOvergrownTool(stack) || stack.isEmpty()) {
            return;
        }

        int currentDamage = stack.getDamageValue();
        if (currentDamage <= 0) {
            return;
        }

        if (stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING)) != 0
                || stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)) != 0) {
            return;
        }

        stack.setDamageValue(Math.max(0, currentDamage - multiplier));
    }
}
