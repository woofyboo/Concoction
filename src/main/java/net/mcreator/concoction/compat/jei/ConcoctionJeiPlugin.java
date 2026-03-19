package net.mcreator.concoction.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ConcoctionJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath("concoction", "soap_cleaning_jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        Item soapItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse("concoction:soap"));
        if (soapItem == null) {
            return;
        }

        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        int counter = 0;

        Item[] leatherLike = new Item[]{
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
                Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR
        };
        for (Item item : leatherLike) {
            recipes.add(makeShapelessCraftPreview(
                    id("soap_cleaning/leather_" + (++counter)),
                    "soap_cleaning_leather",
                    new ItemStack(item),
                    Ingredient.of(soapItem),
                    Ingredient.of(item)
            ));
        }

        Item[] coloredShulkers = new Item[]{
                Items.WHITE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.BLACK_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
                Items.LIME_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.PINK_SHULKER_BOX
        };
        for (Item item : coloredShulkers) {
            ItemStack stack = new ItemStack(item);
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof ShulkerBoxBlock shulker
                    && shulker.getColor() != null) {
                recipes.add(makeShapelessCraftPreview(
                        id("soap_cleaning/shulker_" + (++counter)),
                        "soap_cleaning_shulker",
                        new ItemStack(Items.SHULKER_BOX),
                        Ingredient.of(soapItem),
                        Ingredient.of(item)
                ));
            }
        }

        reg.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    private static RecipeHolder<CraftingRecipe> makeShapelessCraftPreview(ResourceLocation id,
                                                                          String group,
                                                                          ItemStack result,
                                                                          Ingredient... inputs) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (Ingredient ingredient : inputs) {
            ingredients.add(ingredient);
        }
        ShapelessRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, result, ingredients);
        return new RecipeHolder<>(id, recipe);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("concoction", path);
    }
}
