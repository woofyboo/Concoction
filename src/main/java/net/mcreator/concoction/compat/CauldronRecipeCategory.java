package net.mcreator.concoction.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;


import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CauldronRecipeCategory implements IRecipeCategory<CauldronBrewingRecipe> {
//    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cauldron_brewing");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,
            "textures/gui/crystallizer/crystallizer_gui.png");

    public static final RecipeType<CauldronBrewingRecipe> CAULDRON_RECIPE_TYPE = RecipeType.create(ConcoctionMod.MODID, "cauldron_brewing",
            CauldronBrewingRecipe.class);

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public CauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.WATER_CAULDRON));
    }

    @Override
    public RecipeType<CauldronBrewingRecipe> getRecipeType() {
        return CAULDRON_RECIPE_TYPE;
    }
    @Override
    public Component getTitle() {
        return Component.literal("Cooking Cauldron");
    }

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
    public void setRecipe(IRecipeLayoutBuilder builder, CauldronBrewingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 34).addIngredients(recipe.getIngredient(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 54, 34).addIngredients(recipe.getIngredient(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 84, 34).addIngredients(recipe.getIngredient(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 64).addIngredients(recipe.getIngredient(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 54, 64).addIngredients(recipe.getIngredient(4));
        builder.addSlot(RecipeIngredientRole.INPUT, 84, 64).addIngredients(recipe.getIngredient(5));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 50).addItemStack(
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getOutput().get("id"))),
                        Integer.parseInt(recipe.getOutput().get("count")) )
        );

        ItemStack catalyst;
        switch (recipe.getOutput().get("interactionType")) {
            case "hand":
                catalyst = ItemStack.EMPTY;
                break;
            case "bottle":
               catalyst = new ItemStack(Items.GLASS_BOTTLE);
                break;
            case "bowl":
                catalyst = new ItemStack(Items.BOWL);
                break;
            default:
                catalyst = ItemStack.EMPTY;
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 150, 45).addItemStack(catalyst);
    }
}
