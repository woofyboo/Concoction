package net.mcreator.concoction.handlers;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.recipe.SoapCleaningRecipe;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber
public final class ModRecipeUnlockHandler {
    private static final int UNLOCK_CHECK_INTERVAL = 20;

    private static final Map<UUID, Long> INVENTORY_SIGNATURES = new HashMap<>();

    private static RecipeManager cachedRecipeManager;
    private static UnlockIndex cachedUnlockIndex = UnlockIndex.EMPTY;

    private ModRecipeUnlockHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % UNLOCK_CHECK_INTERVAL != 0) {
            return;
        }

        long currentSignature = inventorySignature(player);
        Long previousSignature = INVENTORY_SIGNATURES.put(player.getUUID(), currentSignature);
        if (previousSignature != null && previousSignature == currentSignature) {
            return;
        }

        awardMatchingModRecipes(player);
    }

    private static void awardMatchingModRecipes(ServerPlayer player) {
        UnlockIndex unlockIndex = getUnlockIndex(player.server.getRecipeManager());
        if (unlockIndex.isEmpty()) {
            return;
        }

        Set<Item> inventoryItems = collectInventoryItems(player);
        if (inventoryItems.isEmpty()) {
            return;
        }

        Set<RecipeHolder<?>> recipesToAward = new LinkedHashSet<>();
        for (Item item : inventoryItems) {
            recipesToAward.addAll(unlockIndex.byIngredient().getOrDefault(item, List.of()));
        }

        if (recipesToAward.isEmpty()) {
            return;
        }

        player.awardRecipes(new ArrayList<>(recipesToAward));
    }

    private static UnlockIndex getUnlockIndex(RecipeManager recipeManager) {
        if (recipeManager != cachedRecipeManager) {
            cachedRecipeManager = recipeManager;
            cachedUnlockIndex = buildUnlockIndex(recipeManager);
        }

        return cachedUnlockIndex;
    }

    private static UnlockIndex buildUnlockIndex(RecipeManager recipeManager) {
        Map<Item, Set<RecipeHolder<?>>> mutableIndex = new HashMap<>();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            ResourceLocation recipeId = holder.id();
            if (!ConcoctionMod.MODID.equals(recipeId.getNamespace())) {
                continue;
            }

            for (Ingredient ingredient : extractIngredients(holder.value())) {
                if (ingredient == null || ingredient.isEmpty()) {
                    continue;
                }

                for (ItemStack stack : ingredient.getItems()) {
                    if (stack.isEmpty()) {
                        continue;
                    }

                    mutableIndex
                            .computeIfAbsent(stack.getItem(), ignored -> new LinkedHashSet<>())
                            .add(holder);
                }
            }
        }

        if (mutableIndex.isEmpty()) {
            return UnlockIndex.EMPTY;
        }

        Map<Item, List<RecipeHolder<?>>> immutableIndex = new HashMap<>();
        for (Map.Entry<Item, Set<RecipeHolder<?>>> entry : mutableIndex.entrySet()) {
            immutableIndex.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return new UnlockIndex(Collections.unmodifiableMap(immutableIndex));
    }

    private static Collection<Ingredient> extractIngredients(Recipe<?> recipe) {
        if (recipe instanceof OvenRecipe ovenRecipe) {
            List<Ingredient> ingredients = new ArrayList<>(ovenRecipe.getCraftingIngredients());
            if (!ovenRecipe.getBottleIngredient().isEmpty()) {
                ingredients.add(ovenRecipe.getBottleIngredient());
            }
            if (!ovenRecipe.getBowlIngredient().isEmpty()) {
                ingredients.add(ovenRecipe.getBowlIngredient());
            }
            return ingredients;
        }

        if (recipe instanceof CauldronBrewingRecipe cauldronRecipe) {
            return cauldronRecipe.getInputItems();
        }

        if (recipe instanceof ButterChurnRecipe butterChurnRecipe) {
            return butterChurnRecipe.getInputItems();
        }

        if (recipe instanceof SoapCleaningRecipe) {
            return List.of(Ingredient.of(ConcoctionModItems.SOAP.get()));
        }

        return recipe.getIngredients();
    }

    private static Set<Item> collectInventoryItems(ServerPlayer player) {
        Set<Item> items = new LinkedHashSet<>();

        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()) {
                items.add(stack.getItem());
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (!stack.isEmpty()) {
                items.add(stack.getItem());
            }
        }

        return items;
    }

    private static long inventorySignature(ServerPlayer player) {
        long hash = 1469598103934665603L;

        for (ItemStack stack : player.getInventory().items) {
            hash = mixStack(hash, stack);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            hash = mixStack(hash, stack);
        }

        return hash;
    }

    private static long mixStack(long hash, ItemStack stack) {
        if (stack.isEmpty()) {
            return mixLong(hash, 0L);
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        hash = mixLong(hash, itemId.hashCode());
        hash = mixLong(hash, stack.getCount());
        hash = mixLong(hash, stack.getDamageValue());
        return hash;
    }

    private static long mixLong(long hash, long value) {
        hash ^= value;
        hash *= 1099511628211L;
        return hash;
    }

    private record UnlockIndex(Map<Item, List<RecipeHolder<?>>> byIngredient) {
        private static final UnlockIndex EMPTY = new UnlockIndex(Map.of());

        private boolean isEmpty() {
            return this.byIngredient.isEmpty();
        }
    }
}
