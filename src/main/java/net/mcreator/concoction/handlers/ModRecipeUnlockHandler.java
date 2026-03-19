package net.mcreator.concoction.handlers;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.recipe.SoapCleaningRecipe;
import net.mcreator.concoction.recipe.SoapShieldCleaningRecipe;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
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
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        INVENTORY_SIGNATURES.remove(event.getEntity().getUUID());
    }

    private static void awardMatchingModRecipes(ServerPlayer player) {
        UnlockIndex unlockIndex = getUnlockIndex(player.server.getRecipeManager());
        if (unlockIndex.isEmpty()) {
            return;
        }

        List<ItemStack> inventoryStacks = collectInventoryStacks(player);
        Set<Item> inventoryItems = collectInventoryItems(inventoryStacks);
        if (inventoryItems.isEmpty()) {
            return;
        }

        Set<RecipeHolder<?>> recipesToAward = new LinkedHashSet<>();
        for (Item item : inventoryItems) {
            for (RecipeHolder<?> holder : unlockIndex.byIngredient().getOrDefault(item, List.of())) {
                if (hasMatchingInventoryIngredient(unlockIndex, holder, inventoryStacks)) {
                    recipesToAward.add(holder);
                }
            }
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
            INVENTORY_SIGNATURES.clear();
        }

        return cachedUnlockIndex;
    }

    private static UnlockIndex buildUnlockIndex(RecipeManager recipeManager) {
        Map<Item, Set<RecipeHolder<?>>> mutableIndex = new HashMap<>();
        Map<RecipeHolder<?>, List<Ingredient>> ingredientsByRecipe = new HashMap<>();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            ResourceLocation recipeId = holder.id();
            if (!ConcoctionMod.MODID.equals(recipeId.getNamespace())) {
                continue;
            }

            List<Ingredient> ingredients = extractIngredients(holder.value()).stream()
                    .filter(ingredient -> ingredient != null && !ingredient.isEmpty())
                    .toList();
            if (ingredients.isEmpty()) {
                continue;
            }

            ingredientsByRecipe.put(holder, ingredients);

            for (Ingredient ingredient : ingredients) {
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

        return new UnlockIndex(
                Collections.unmodifiableMap(immutableIndex),
                Collections.unmodifiableMap(new HashMap<>(ingredientsByRecipe))
        );
    }

    private static Collection<Ingredient> extractIngredients(Recipe<?> recipe) {
        if (recipe instanceof OvenRecipe ovenRecipe) {
            List<Ingredient> ingredients = new ArrayList<>(ovenRecipe.getCraftingIngredients());
            if (!ovenRecipe.getBottleIngredient().isEmpty()) {
                ingredients.add(ovenRecipe.getBottleIngredient());
            }
            return ingredients;
        }

        if (recipe instanceof ButterChurnRecipe butterChurnRecipe) {
            return butterChurnRecipe.getInputItems();
        }

        if (recipe instanceof SoapCleaningRecipe || recipe instanceof SoapShieldCleaningRecipe) {
            return List.of(Ingredient.of(ConcoctionModItems.SOAP.get()));
        }

        return recipe.getIngredients();
    }

    private static boolean hasMatchingInventoryIngredient(UnlockIndex unlockIndex,
                                                          RecipeHolder<?> holder,
                                                          List<ItemStack> inventoryStacks) {
        for (Ingredient ingredient : unlockIndex.ingredientsByRecipe().getOrDefault(holder, List.of())) {
            for (ItemStack stack : inventoryStacks) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Set<Item> collectInventoryItems(List<ItemStack> inventoryStacks) {
        Set<Item> items = new LinkedHashSet<>();

        for (ItemStack stack : inventoryStacks) {
            if (!stack.isEmpty()) {
                items.add(stack.getItem());
            }
        }

        return items;
    }

    private static List<ItemStack> collectInventoryStacks(ServerPlayer player) {
        List<ItemStack> stacks = new ArrayList<>(
                player.getInventory().items.size()
                        + player.getInventory().offhand.size()
                        + player.getInventory().armor.size()
        );
        stacks.addAll(player.getInventory().items);
        stacks.addAll(player.getInventory().offhand);
        stacks.addAll(player.getInventory().armor);
        return stacks;
    }

    private static long inventorySignature(ServerPlayer player) {
        long hash = 1469598103934665603L;

        for (ItemStack stack : player.getInventory().items) {
            hash = mixStack(hash, stack);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            hash = mixStack(hash, stack);
        }

        for (ItemStack stack : player.getInventory().armor) {
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

    private record UnlockIndex(Map<Item, List<RecipeHolder<?>>> byIngredient,
                               Map<RecipeHolder<?>, List<Ingredient>> ingredientsByRecipe) {
        private static final UnlockIndex EMPTY = new UnlockIndex(Map.of(), Map.of());

        private boolean isEmpty() {
            return this.byIngredient.isEmpty();
        }
    }
}
