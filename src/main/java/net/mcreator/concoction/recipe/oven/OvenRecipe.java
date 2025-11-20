package net.mcreator.concoction.recipe.oven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OvenRecipe implements Recipe<OvenRecipeInput> {
    private final int cookingTime;
    private final List<Ingredient> craftingIngredients; // 1..6 — слоты для крафта
    private final Ingredient bottleIngredient;          // слот 0 — бутылочка
    private final Ingredient bowlIngredient;            // слот 7 — миска
    private final Map<String, String> result;

    // кэш результата для книги рецептов
    private final ItemStack resultStack;

    public OvenRecipe(int cookingTime,
                      List<Ingredient> craftingIngredients,
                      Ingredient bottleIngredient,
                      Ingredient bowlIngredient,
                      Map<String, String> result) {
        this.cookingTime = cookingTime;
        this.craftingIngredients = craftingIngredients;
        this.bottleIngredient = bottleIngredient;
        this.bowlIngredient = bowlIngredient;
        this.result = result;
        this.resultStack = createResultStack(result);
    }

    private static ItemStack createResultStack(Map<String, String> result) {
        if (result == null) return ItemStack.EMPTY;

        String id = result.getOrDefault("id", "");
        if (id.isEmpty()) return ItemStack.EMPTY;

        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return ItemStack.EMPTY;

        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null) return ItemStack.EMPTY;

        int count = 1;
        String rawCount = result.getOrDefault("count", result.getOrDefault("amount", "1"));
        try {
            count = Math.max(1, Integer.parseInt(rawCount.trim()));
        } catch (NumberFormatException ignored) {}

        return new ItemStack(item, count);
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public List<Ingredient> getCraftingIngredients() {
        return craftingIngredients;
    }

    public Ingredient getBottleIngredient() {
        return bottleIngredient;
    }

    public Ingredient getBowlIngredient() {
        return bowlIngredient;
    }

    public Map<String, String> getResult() {
        return result;
    }

    /** Сколько предметов получится на выходе (по умолчанию 1). Читает result.count/amount. */
    public int getOutputCount() {
        if (resultStack.isEmpty()) return 1;
        return resultStack.getCount();
    }

    /** Сколько мисок требуется списать за одну готовку (0, если миска не нужна). */
    public int getBowlCost() {
        return bowlIngredient.isEmpty() ? 0 : getOutputCount();
    }

    private boolean matchesIngredients(NonNullList<ItemStack> inventory) {
        // Слоты крафта 1..6
        NonNullList<ItemStack> craftingSlots = NonNullList.withSize(6, ItemStack.EMPTY);
        for (int i = 1; i <= 6; i++) {
            craftingSlots.set(i - 1, inventory.get(i));
        }

        // Бутылочка (0) и миска (7)
        ItemStack bottleSlot = inventory.get(0);
        ItemStack bowlSlot   = inventory.get(7);

        // Проверяем, что количество непустых слотов крафта совпадает с количеством ингредиентов (порядок не важен)
        int nonEmpty = 0;
        for (ItemStack st : craftingSlots) if (!st.isEmpty()) nonEmpty++;
        if (nonEmpty != craftingIngredients.size()) return false;

        // Сопоставление ингредиентов без учёта порядка
        Map<Ingredient, Integer> needed = new HashMap<>();
        for (Ingredient ing : craftingIngredients) {
            needed.merge(ing, 1, Integer::sum);
        }
        for (ItemStack st : craftingSlots) {
            if (st.isEmpty()) continue;
            boolean matched = false;
            for (Map.Entry<Ingredient, Integer> e : needed.entrySet()) {
                if (e.getValue() > 0 && e.getKey().test(st)) {
                    e.setValue(e.getValue() - 1);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        if (!needed.values().stream().allMatch(v -> v <= 0)) return false;

        // Проверка бутылочки
        if (!bottleIngredient.isEmpty() && !bottleIngredient.test(bottleSlot)) return false;
        if (bottleIngredient.isEmpty() && !bottleSlot.isEmpty()) return false;

        // Проверка миски (тип)
        if (!bowlIngredient.isEmpty() && !bowlIngredient.test(bowlSlot)) return false;
        if (bowlIngredient.isEmpty() && !bowlSlot.isEmpty()) return false;

        // Доп. проверка количества мисок относительно выхода
        if (!bowlIngredient.isEmpty()) {
            int required = getOutputCount();
            if (bowlSlot.getCount() < required) return false;
        }

        return true;
    }

    @Override
    public boolean matches(OvenRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        return matchesIngredients(input.items());
    }

    /** В книгу рецептов важен этот метод — тут должен быть итоговый стак. */
    @Override
    public ItemStack assemble(OvenRecipeInput input, HolderLookup.Provider registries) {
        return resultStack.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // сколько "клеток" доступно меню (рецептбуку пофиг, где именно они расположены)
        int gridSlots = width * height;

        // нам нужна хотя бы по одной "клетке" на каждый крафтовый ингредиент
        // (бутылка и миска тут не считаются — они отдельные слоты)
        return gridSlots >= this.craftingIngredients.size();
    }


    /** Тоже для книги рецептов — превью результата. */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        if (result == null) return ItemStack.EMPTY;

        String idStr = result.get("id");
        if (idStr == null || idStr.isEmpty()) return ItemStack.EMPTY;

        ResourceLocation id = ResourceLocation.parse(idStr);
        int count = getOutputCount();

        return new ItemStack(BuiltInRegistries.ITEM.get(id), count);
    }



    /** ВАЖНО: книга берёт список ингредиентов из этого метода. */
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        // основные ингредиенты 1..6
        list.addAll(this.craftingIngredients);

        // бутылочка, если есть
        if (!this.bottleIngredient.isEmpty()) {
            list.add(this.bottleIngredient);
        }

        // миска/ведро/посуда, если есть
        if (!this.bowlIngredient.isEmpty()) {
            list.add(this.bowlIngredient);
        }

        return list;
    }



    @Override
    public ItemStack getToastSymbol() {
        // Можешь заменить на свой предмет духовки, если он есть
        return new ItemStack(Items.FURNACE);
    }

    /** Пусть будут обычными, не "особенными" — так проще с книгой. */
    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConcoctionModRecipes.OVEN_RECIPE_SERIALIZER.get();
    }




    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
    }

    // --- Сериализация
    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        public static final MapCodec<OvenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("cooking_time").orElse(200).forGetter(OvenRecipe::getCookingTime),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("crafting_ingredients").forGetter(OvenRecipe::getCraftingIngredients),
                Ingredient.CODEC.optionalFieldOf("bottle_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBottleIngredient),
                Ingredient.CODEC.optionalFieldOf("bowl_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBowlIngredient),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("result").forGetter(OvenRecipe::getResult)
        ).apply(inst, OvenRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT, OvenRecipe::getCookingTime,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), OvenRecipe::getCraftingIngredients,
                        Ingredient.CONTENTS_STREAM_CODEC, OvenRecipe::getBottleIngredient,
                        Ingredient.CONTENTS_STREAM_CODEC, OvenRecipe::getBowlIngredient,
                        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), OvenRecipe::getResult,
                        OvenRecipe::new);

        @Override
        public MapCodec<OvenRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
