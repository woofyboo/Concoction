package net.mcreator.concoction.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
    public ResourceLocation getPluginUid() { return UID; }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        Item soapItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse("concoction:soap"));
        if (soapItem == null) return;

        // Собираем список ванильных shapeless-крафтов (ТОЛЬКО для JEI)
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        int counter = 0;

        // 1) Кожаная броня + конская кожа + волчья броня
        Item[] leatherLike = new Item[] {
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
                Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR
        };
        for (Item it : leatherLike) {
            recipes.add(makeShapelessCraftPreview(
                    id("soap_cleaning/leather_" + (++counter)),
                    new ItemStack(it),                                // результат (чистый)
                    Ingredient.of(soapItem),                          // 1: мыло
                    Ingredient.of(it)                                 // 2: «кожаная вещь»
            ));
        }

        // 2) Щит
        recipes.add(makeShapelessCraftPreview(
                id("soap_cleaning/shield"),
                new ItemStack(Items.SHIELD),
                Ingredient.of(soapItem),
                Ingredient.of(Items.SHIELD)
        ));

        // 3) Все цветные шалкеры -> обычный
        Item[] coloredShulkers = new Item[] {
                Items.WHITE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.BLACK_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
                Items.LIME_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.PINK_SHULKER_BOX
        };
        for (Item it : coloredShulkers) {
            ItemStack in = new ItemStack(it);
            if (in.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sh && sh.getColor() != null) {
                recipes.add(makeShapelessCraftPreview(
                        id("soap_cleaning/shulker_" + (++counter)),
                        new ItemStack(Items.SHULKER_BOX),
                        Ingredient.of(soapItem),
                        Ingredient.of(it)
                ));
            }
        }

        // Добавляем в JEI во вкладку ванильного Crafting
        reg.addRecipes(RecipeTypes.CRAFTING, recipes);
    }


    // ---------- helpers ----------

    private static RecipeHolder<CraftingRecipe> makeShapelessCraftPreview(ResourceLocation id,
                                                                          ItemStack result,
                                                                          Ingredient... inputs) {
        NonNullList<Ingredient> ing = NonNullList.create();
        for (Ingredient i : inputs) ing.add(i);
        // group пустой, категория MISC — как у обычных ванильных
        ShapelessRecipe recipe = new ShapelessRecipe("", CraftingBookCategory.MISC, result, ing);
        return new RecipeHolder<>(id, recipe);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("concoction", path);
    }
}
