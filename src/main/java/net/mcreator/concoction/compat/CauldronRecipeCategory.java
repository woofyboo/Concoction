package net.mcreator.concoction.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import javax.annotation.Nonnull;

public class CauldronRecipeCategory implements IRecipeCategory<CauldronBrewingRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,
            "textures/gui/cooking_cauldron_gui.png");

    public static final RecipeType<CauldronBrewingRecipe> CAULDRON_RECIPE_TYPE = RecipeType.create(ConcoctionMod.MODID, "cauldron_brewing",
            CauldronBrewingRecipe.class);

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    protected final IDrawableAnimated arrow;

    public CauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 63);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.CAULDRON));
        this.arrow = helper.drawableBuilder(TEXTURE, 122, 0, 44, 23)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<CauldronBrewingRecipe> getRecipeType() {
        return CAULDRON_RECIPE_TYPE;
    }
    @Override
    public Component getTitle() {
        return Component.translatable("gui.cooking_cauldron.title");
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
    public void draw(CauldronBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 44, 20);
    }

    public static boolean isCursorInsideBounds(int iconX, int iconY, int iconWidth, int iconHeight, double cursorX, double cursorY) {
        return iconX <= cursorX && cursorX < iconX + iconWidth && iconY <= cursorY && cursorY < iconY + iconHeight;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CauldronBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isCursorInsideBounds(44, 20, 44, 23, mouseX, mouseY)) {
            int cookTimeSeconds = recipe.getCookingTime() / 20;
            if (cookTimeSeconds > 0)
                tooltip.add(Component.translatable("gui.cooking_cauldron.time.seconds", cookTimeSeconds));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CauldronBrewingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 6).addIngredients(recipe.getIngredient(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 6).addIngredients(recipe.getIngredient(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 24).addIngredients(recipe.getIngredient(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 24).addIngredients(recipe.getIngredient(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 42).addIngredients(recipe.getIngredient(4));
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 42).addIngredients(recipe.getIngredient(5));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 25).addItemStack(
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getOutput().get("id"))),
                        Integer.parseInt(recipe.getOutput().get("count")) )
        );

        ItemStack catalyst = switch (recipe.getOutput().get("interactionType")) {
            case "hand" -> ItemStack.EMPTY;
            case "bottle" -> new ItemStack(Items.GLASS_BOTTLE);
            case "bowl" -> new ItemStack(Items.BOWL);
            default -> ItemStack.EMPTY;
        };
        builder.addSlot(RecipeIngredientRole.CATALYST, 57, 2).addItemStack(catalyst);
    }
}
