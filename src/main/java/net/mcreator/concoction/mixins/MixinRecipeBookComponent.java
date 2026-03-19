package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.world.inventory.OvenMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class MixinRecipeBookComponent {

    @Shadow protected RecipeBookMenu<?, ?> menu;
    @Shadow protected Minecraft minecraft;
    @Shadow protected GhostRecipe ghostRecipe;

    // ------------------ ФИЛЬТР КОЛЛЕКЦИЙ ------------------

    @Redirect(
            method = "updateCollections",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"
            )
    )
    private void concoction$filterOvenCollections(RecipeBookPage page,
                                                  List<RecipeCollection> collections,
                                                  boolean resetPage) {

        if (this.menu instanceof OvenMenu
                && this.menu.getRecipeBookType() == RecipeBookType.FURNACE) {

            RecipeType<?> ovenType = ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
            if (ovenType != null) {
                Iterator<RecipeCollection> it = collections.iterator();
                while (it.hasNext()) {
                    RecipeCollection coll = it.next();

                    boolean hasOven = coll.getRecipes().stream()
                            .anyMatch(h -> h.value().getType() == ovenType);

                    if (!hasOven) {
                        it.remove();
                    }
                }
            }
        }

        page.updateCollections(collections, resetPage);
    }

    // ------------------ ХЕЛПЕРЫ ------------------

    /** Сколько всего предметов под ingredient есть в GUI (кроме выходного слота). */
    private int concoction$countAvailable(Ingredient ingredient, int resultIndex) {
        if (ingredient == null || ingredient.isEmpty()) return 0;
        int total = 0;

        for (int i = 0; i < this.menu.slots.size(); i++) {
            if (i == resultIndex) continue;
            Slot s = this.menu.slots.get(i);
            ItemStack stack = s.getItem();
            if (!stack.isEmpty() && ingredient.test(stack)) {
                total += stack.getCount();
            }
        }

        return total;
    }

    /** Просто рисуем призрак в указанном GUI-слоте. */
    private void concoction$addGhostRaw(List<Slot> slots,
                                        Ingredient ingredient,
                                        int guiIndex) {
        if (ingredient == null || ingredient.isEmpty()) return;
        if (guiIndex < 0 || guiIndex >= slots.size()) return;

        Slot guiSlot = slots.get(guiIndex);
        this.ghostRecipe.addIngredient(ingredient, guiSlot.x, guiSlot.y);
    }

    /**
     * Возвращаем ВСЕ ингредиенты духовки (бутылка, крафтовые, миска)
     * обратно игроку через shift-клик, если рецепт неполный.
     * Топливо не трогаем, выходной слот уже обработан отдельно.
     */
    private void concoction$returnOvenInputsToPlayer(MultiPlayerGameMode gameMode,
                                                     int resultIndex) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        // наши слоты духовки:
        // 36 — бутылка
        // 37..42 — ингредиенты
        // 43 — миска
        int[] inputSlots = {36, 37, 38, 39, 40, 41, 42, 43};

        for (int slotIndex : inputSlots) {
            if (slotIndex == resultIndex) continue;
            if (slotIndex < 0 || slotIndex >= this.menu.slots.size()) continue;

            Slot slot = this.menu.slots.get(slotIndex);
            if (slot != null && slot.hasItem()) {
                gameMode.handleInventoryMouseClick(
                        this.menu.containerId,
                        slotIndex,
                        0,
                        ClickType.QUICK_MOVE,
                        this.minecraft.player
                );
            }
        }
    }

    // ------------------ КЛИК ПО РЕЦЕПТУ ------------------

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePlaceRecipe(ILnet/minecraft/world/item/crafting/RecipeHolder;Z)V"
            )
    )
    private void concoction$customPlaceRecipe(MultiPlayerGameMode gameMode,
                                              int containerId,
                                              RecipeHolder<?> recipeHolder,
                                              boolean shiftDown) {

        // не наша духовка / не наш рецепт — ваниль
        if (!(this.menu instanceof OvenMenu)
                || this.minecraft == null
                || this.minecraft.level == null
                || !(recipeHolder.value() instanceof OvenRecipe ovenRecipe)) {

            gameMode.handlePlaceRecipe(containerId, recipeHolder, shiftDown);
            return;
        }

        // очищаем предыдущий призрак
        this.ghostRecipe.clear();

        int resultIndex = this.menu.getResultSlotIndex();

        // 1. если в выходном слоте что-то есть — сначала шифткликнем его в инвентарь
        if (resultIndex >= 0 && resultIndex < this.menu.slots.size()) {
            Slot resultSlot = this.menu.slots.get(resultIndex);
            if (resultSlot != null && resultSlot.hasItem()) {
                gameMode.handleInventoryMouseClick(
                        this.menu.containerId,
                        resultIndex,
                        0,
                        ClickType.QUICK_MOVE,
                        this.minecraft.player
                );
            }
        }

        // 2. Готовим структуру ингредиентов
        List<Ingredient> all = ovenRecipe.getIngredients();
        int craftCount = ovenRecipe.getCraftingIngredients().size();

        int idx = 0;
        int bottleIndex = -1;
        int bowlIndex = -1;

        idx = craftCount;

        boolean hasBottle = !ovenRecipe.getBottleIngredient().isEmpty() && idx < all.size();
        if (hasBottle) {
            bottleIndex = idx;
            idx++;
        }

        boolean hasBowl = !ovenRecipe.getBowlIngredient().isEmpty() && idx < all.size();
        if (hasBowl) {
            bowlIndex = idx;
        }

        // ---------- ПРОВЕРЯЕМ, ХВАТАЕТ ЛИ ВСЕХ ИНГРЕДИЕНТОВ ----------

        boolean anyMissing = false;

        // крафтовые по 1 штуке
        for (int ci = 0; ci < craftCount; ci++) {
            Ingredient ing = all.get(ci);
            int required = 1;
            int available = concoction$countAvailable(ing, resultIndex);
            if (available < required) {
                anyMissing = true;
            }
        }

        // бутылочка — 1 штука
        if (hasBottle && bottleIndex >= 0) {
            Ingredient ing = all.get(bottleIndex);
            int required = 1;
            int available = concoction$countAvailable(ing, resultIndex);
            if (available < required) {
                anyMissing = true;
            }
        }

        // миски — столько, сколько выдаёт рецепт (или минимум 1)
        int requiredBowls = Math.max(1, ovenRecipe.getOutputCount());
        if (hasBowl && bowlIndex >= 0) {
            Ingredient ing = all.get(bowlIndex);
            int available = concoction$countAvailable(ing, resultIndex);
            if (available < requiredBowls) {
                anyMissing = true;
            }
        }

        // ---------- ВЕТКА 1: всего хватает → ванильная раскладка, без призраков ----------

        if (!anyMissing) {
            gameMode.handlePlaceRecipe(containerId, recipeHolder, shiftDown);
            this.ghostRecipe.clear();
            return;
        }

        // ---------- ВЕТКА 2: чего-то не хватает ----------
        // 2.1. сначала возвращаем ВСЕ входные слоты духовки игроку
        concoction$returnOvenInputsToPlayer(gameMode, resultIndex);

        // 2.2. рисуем красный макет рецепта
        this.ghostRecipe.clear();
        this.ghostRecipe.setRecipe(recipeHolder);

        List<Slot> slots = this.menu.slots;

        // крафтовые 1..6 → GUI 37..42
        for (int ci = 0; ci < craftCount; ci++) {
            int guiIndex = 37 + ci;
            concoction$addGhostRaw(slots, all.get(ci), guiIndex);
        }

        // бутылочка → GUI 36
        if (hasBottle && bottleIndex >= 0) {
            concoction$addGhostRaw(slots, all.get(bottleIndex), 36);
        }

        // миска → GUI 43
        if (hasBowl && bowlIndex >= 0) {
            concoction$addGhostRaw(slots, all.get(bowlIndex), 43);
        }

        // handlePlaceRecipe тут специально НЕ зовём:
        // предыдущий рецепт полностью выгружен, новый только подсвечен как "недоступный".
    }
}
