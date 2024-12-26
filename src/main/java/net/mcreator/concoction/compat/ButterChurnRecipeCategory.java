package net.mcreator.concoction.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ButterChurnRecipeCategory implements IRecipeCategory<ButterChurnRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,
            "textures/gui/cooking_cauldron_gui.png");

    public static final RecipeType<ButterChurnRecipe> BUTTER_CHURN_RECIPE_TYPE = RecipeType.create(ConcoctionMod.MODID, "butter_churn",
            ButterChurnRecipe.class);

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    protected final IDrawableAnimated arrow;

    public ButterChurnRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 63);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ConcoctionModItems.BUTTER_CHURN.get()));
        this.arrow = helper.drawableBuilder(TEXTURE, 122, 0, 44, 23)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<ButterChurnRecipe> getRecipeType() {
        return BUTTER_CHURN_RECIPE_TYPE;
    }
    @Override
    public Component getTitle() {
        return Component.translatable("gui.butter_churn.title");
    }

    @SuppressWarnings("removal")
    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ButterChurnRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 44, 20);
    }

    public static boolean isCursorInsideBounds(int iconX, int iconY, int iconWidth, int iconHeight, double cursorX, double cursorY) {
        return iconX <= cursorX && cursorX < iconX + iconWidth && iconY <= cursorY && cursorY < iconY + iconHeight;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ButterChurnRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isCursorInsideBounds(44, 20, 44, 23, mouseX, mouseY)) {
            int cookTimeSeconds = 20;
            if (cookTimeSeconds > 0)
                tooltip.add(Component.translatable("gui.cooking_cauldron.time.seconds", cookTimeSeconds));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ButterChurnRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 6).addItemStack(
                new ItemStack(recipe.getIngredient(0).getItems()[0].getItem(), recipe.getInputItems().size())
        );

        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 25).addItemStack(
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getOutput().get("id"))),
                        Integer.parseInt(recipe.getOutput().get("count")) )
        );

        ItemStack catalyst = switch (recipe.getOutput().get("interactionType")) {
            case "hand" -> ItemStack.EMPTY;
            case "bottle" -> new ItemStack(Items.GLASS_BOTTLE);
            default -> ItemStack.EMPTY;
        };
        builder.addSlot(RecipeIngredientRole.CATALYST, 57, 2).addItemStack(catalyst);
    }
}
