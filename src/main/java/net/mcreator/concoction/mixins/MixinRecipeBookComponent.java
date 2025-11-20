package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.world.inventory.OvenGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class MixinRecipeBookComponent {

    // ====== поля из RecipeBookComponent, которые нам нужны ======

    @Shadow protected RecipeBookMenu<?, ?> menu;
    @Shadow protected Minecraft minecraft;
    @Shadow @Final protected GhostRecipe ghostRecipe;

    @Shadow protected RecipeBookPage recipeBookPage;
    @Shadow protected EditBox searchBox;
    @Shadow protected StateSwitchingButton filterButton;
    @Shadow @Final protected List<RecipeBookTabButton> tabButtons;
    @Shadow protected RecipeBookTabButton selectedTab;

    @Shadow protected int xOffset;
    @Shadow protected int width;
    @Shadow protected int height;

    // методы, которые мы вызываем из своей реализации mouseClicked
    @Shadow protected abstract boolean isVisible();
    @Shadow protected abstract boolean isOffsetNextToMainGUI();
    @Shadow protected abstract void setVisible(boolean visible);
    @Shadow protected abstract void updateFilterButtonTooltip();
    @Shadow protected abstract void sendUpdateSettings();
    @Shadow protected abstract void updateCollections(boolean reset);

    @Shadow protected abstract boolean toggleFiltering();

    // ====== 1) фильтр коллекций (как у тебя было) ======

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

        if (this.menu instanceof OvenGUIMenu
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

    // ====== 2) полная подмена mouseClicked с нашей вставкой призрака ======

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void concoction$mouseClicked(double mouseX, double mouseY, int button,
                                         CallbackInfoReturnable<Boolean> cir) {
        boolean result = concoction$mouseClickedImpl(mouseX, mouseY, button);
        cir.setReturnValue(result);
        cir.cancel();
    }

    /**
     * Почти точная копия ванильного RecipeBookComponent.mouseClicked,
     * но с одним отличием:
     *  - после ghostRecipe.clear() мы вызываем buildOvenGhostIfNeeded(...)
     *    чтобы для нашей духовки заполнить призрачный рецепт.
     */
    private boolean concoction$mouseClickedImpl(double mouseX, double mouseY, int button) {
        if (this.isVisible() && !this.minecraft.player.isSpectator()) {
            int left = (this.width - 147) / 2 - this.xOffset;
            int top = (this.height - 166) / 2;

            if (this.recipeBookPage.mouseClicked(mouseX, mouseY, button, left, top, 147, 166)) {
                RecipeHolder<?> recipeholder = this.recipeBookPage.getLastClickedRecipe();
                RecipeCollection recipecollection = this.recipeBookPage.getLastClickedRecipeCollection();
                if (recipeholder != null && recipecollection != null) {
                    if (!recipecollection.isCraftable(recipeholder) && this.ghostRecipe.getRecipe() == recipeholder) {
                        return false;
                    }

                    // ваниль: очищаем предыдущий призрак
                    this.ghostRecipe.clear();

                    // НАША ВСТАВКА: если это духовка — заполняем ghostRecipe сами
                    buildOvenGhostIfNeeded(recipeholder);

                    // ваниль: просим MultiPlayerGameMode разложить рецепт
                    this.minecraft.gameMode.handlePlaceRecipe(
                            this.minecraft.player.containerMenu.containerId,
                            recipeholder,
                            Screen.hasShiftDown()
                    );

                    if (!this.isOffsetNextToMainGUI()) {
                        this.setVisible(false);
                    }
                }

                return true;
            } else if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
                this.searchBox.setFocused(true);
                return true;
            } else {
                this.searchBox.setFocused(false);
                if (this.filterButton.mouseClicked(mouseX, mouseY, button)) {
                    boolean flag = this.toggleFiltering();
                    this.filterButton.setStateTriggered(flag);
                    this.updateFilterButtonTooltip();
                    this.sendUpdateSettings();
                    this.updateCollections(false);
                    return true;
                } else {
                    for (RecipeBookTabButton tabButton : this.tabButtons) {
                        if (tabButton.mouseClicked(mouseX, mouseY, button)) {
                            if (this.selectedTab != tabButton) {
                                if (this.selectedTab != null) {
                                    this.selectedTab.setStateTriggered(false);
                                }

                                this.selectedTab = tabButton;
                                this.selectedTab.setStateTriggered(true);
                                this.updateCollections(true);
                            }

                            return true;
                        }
                    }

                    return false;
                }
            }
        } else {
            return false;
        }
    }

    // ====== 3) сборка призрачного рецепта для духовки ======

    private void buildOvenGhostIfNeeded(RecipeHolder<?> holder) {
        if (!(this.menu instanceof OvenGUIMenu)) {
            return;
        }
        if (holder == null || holder.value() == null) {
            return;
        }
        if (!(holder.value() instanceof OvenRecipe ovenRecipe)) {
            return;
        }
        if (ovenRecipe.getType() != ConcoctionModRecipes.OVEN_RECIPE_TYPE.get()) {
            return;
        }

        if (this.minecraft.level == null) {
            return;
        }

        NonNullList<Slot> menuSlots = this.menu.slots;

        // результат
        ItemStack resultStack = ovenRecipe.getResultItem(this.minecraft.level.registryAccess());
        int resultIndex = this.menu.getResultSlotIndex();
        if (resultIndex >= 0 && resultIndex < menuSlots.size()) {
            Slot resultSlot = menuSlots.get(resultIndex);
            this.ghostRecipe.addIngredient(Ingredient.of(resultStack), resultSlot.x, resultSlot.y);
        }

        // бутылка (слот 36)
        if (!ovenRecipe.getBottleIngredient().isEmpty() && menuSlots.size() > 36) {
            Slot s = menuSlots.get(36);
            this.ghostRecipe.addIngredient(ovenRecipe.getBottleIngredient(), s.x, s.y);
        }

        // миска (слот 43)
        if (!ovenRecipe.getBowlIngredient().isEmpty() && menuSlots.size() > 43) {
            Slot s = menuSlots.get(43);
            this.ghostRecipe.addIngredient(ovenRecipe.getBowlIngredient(), s.x, s.y);
        }

        // 3×2 сетка (слоты 37..42) — берём только craftingIngredients
        int[] gridSlots = {37, 38, 39, 40, 41, 42};
        List<Ingredient> craft = ovenRecipe.getCraftingIngredients();
        int max = Math.min(craft.size(), gridSlots.length);

        for (int i = 0; i < max; i++) {
            Ingredient ing = craft.get(i);
            if (ing.isEmpty()) continue;

            int idx = gridSlots[i];
            if (idx < 0 || idx >= menuSlots.size()) continue;

            Slot s = menuSlots.get(idx);
            this.ghostRecipe.addIngredient(ing, s.x, s.y);
        }
    }
}
