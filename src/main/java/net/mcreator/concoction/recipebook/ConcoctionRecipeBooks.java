package net.mcreator.concoction.recipebook;

import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.List;

/**
 * Регистрация категорий рецептбука для кастомной духовки (Oven).
 *
 * Что делает:
 *  - говорит игре, какие категории есть у книги типа FURNACE;
 *  - привязывает наш кастомный RecipeType (OVEN_RECIPE_TYPE) к категории FURNACE_FOOD;
 *  - НЕ трогает SMOKER, так что у него остаются только его ванильные рецепты.
 */
public class ConcoctionRecipeBooks {

    public static void register(IEventBus modEventBus) {
        // подписываемся на клиентский эвент регистрации категорий
        modEventBus.addListener(ConcoctionRecipeBooks::onRegisterRecipeBookCategories);
    }

    private static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {

        // 1) Объявляем, какие категории есть у RecipeBookType.FURNACE
        //    (по сути — какие вкладки видит книга рецептов этого типа)
        event.registerBookCategories(
                RecipeBookType.FURNACE,
                List.of(
                        RecipeBookCategories.FURNACE_SEARCH,
                        RecipeBookCategories.FURNACE_FOOD
                )
        );

        // 2) Привязываем КАСТОМНЫЙ тип рецепта к нужной категории
        //
        // Важно:
        //  - используем именно ConcoctionModRecipes.OVEN_RECIPE_TYPE (твоя духовка);
        //  - возвращаем FURNACE_FOOD => рецепты духовки будут показываться в книге печи;
        //  - SMOKER (RecipeBookType.SMOKER / SMOKER_* категории) вообще не трогаем.
        event.registerRecipeCategoryFinder(
                ConcoctionModRecipes.OVEN_RECIPE_TYPE.get(),
                (RecipeHolder<?> holder) -> RecipeBookCategories.FURNACE_FOOD
        );
    }
}
