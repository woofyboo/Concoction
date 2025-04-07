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
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.datafix.fixes.ItemStackTagFix;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButterChurnRecipeCategory implements IRecipeCategory<ButterChurnRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,
            "textures/gui/butter_churn_gui_jei.png");

    public static final RecipeType<ButterChurnRecipe> BUTTER_CHURN_RECIPE_TYPE = RecipeType.create(ConcoctionMod.MODID, "butter_churn",
            ButterChurnRecipe.class);

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public ButterChurnRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 134, 58);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ConcoctionModItems.BUTTER_CHURN.get()));
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

    public static boolean isCursorInsideBounds(int iconX, int iconY, int iconWidth, int iconHeight, double cursorX, double cursorY) {
        return iconX <= cursorX && cursorX < iconX + iconWidth && iconY <= cursorY && cursorY < iconY + iconHeight;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ButterChurnRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isCursorInsideBounds(44, 20, 44, 23, mouseX, mouseY)) {
            tooltip.add(Component.translatable("gui.butter_churn.chance"));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ButterChurnRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 20).addItemStack(
                new ItemStack(recipe.getIngredient(0).getItems()[0].getItem(), recipe.getInputItems().size())
        );

        builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 20).addItemStack(
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getOutput().get("id"))),
                        Integer.parseInt(recipe.getOutput().get("count")) )
        );

        TagKey<Item> SHOVELS = TagKey.create(Registries.ITEM, ResourceLocation.parse("shovels"));
        builder.addSlot(RecipeIngredientRole.CATALYST, 48, 2)
                .addItemStacks(Arrays.asList(Ingredient.of(SHOVELS).getItems()));

        ItemStack catalyst = switch (recipe.getOutput().get("interactionType")) {
            case "hand" -> ItemStack.EMPTY;
            case "bottle" -> new ItemStack(Items.GLASS_BOTTLE);
            default -> ItemStack.EMPTY;
        };
        builder.addSlot(RecipeIngredientRole.CATALYST, 109, 20).addItemStack(catalyst);

    }
}
