package net.mcreator.concoction.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HotSauceAdditionRecipe extends CustomRecipe {
    private static final String GROUP = "hot_sauce_addition";
    public static final TagKey<Item> SPICY_SAUCE_TARGETS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "foods/spicy_sauce_targets")
    );
    private static final Set<FoodPassiveEffectType> HOT_SAUCE_INCOMPATIBLE_EFFECTS = EnumSet.of(
            FoodPassiveEffectType.SPICE_INFUSED_MEAT,
            FoodPassiveEffectType.CONCENTRATED_SPICINESS
    );
    private static final Ingredient HOT_SAUCE_INGREDIENT = Ingredient.of(ConcoctionModItems.HOT_SAUCE_BOTTLE.get());
    private static final Ingredient TARGET_INGREDIENT = Ingredient.of(SPICY_SAUCE_TARGETS);
    private static final List<BulkApplicationRule> BULK_APPLICATION_RULES = List.of(
            BulkApplicationRule.of(ConcoctionModItems.TAHCHIN_SLICE, 4)
    );

    public HotSauceAdditionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        MatchContext match = findMatch(input);
        if (match == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = createResultStack(match.targetStack());
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        result.setCount(match.targetCount());
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(ConcoctionModItems.HOT_SAUCE_BOTTLE.get())) {
                remainingItems.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remainingItems;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HOT_SAUCE_ADDITION.get();
    }

    @Override
    public String getGroup() {
        return GROUP;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, HOT_SAUCE_INGREDIENT, TARGET_INGREDIENT);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return createResultStack(new ItemStack(ConcoctionModItems.VEGETABLE_SOUP.get()));
    }

    public static boolean isValidTarget(ItemStack stack) {
        return !stack.isEmpty() && stack.is(SPICY_SAUCE_TARGETS);
    }

    public static boolean canApplyHotSauce(ItemStack stack) {
        List<FoodPassiveEffectComponent> passiveEffects = FoodPassiveEffects.get(stack);
        for (FoodPassiveEffectComponent passiveEffect : passiveEffects) {
            if (HOT_SAUCE_INCOMPATIBLE_EFFECTS.contains(passiveEffect.type())) {
                return false;
            }
        }
        return true;
    }

    public static ItemStack createResultStack(ItemStack targetStack) {
        if (!isValidTarget(targetStack) || !canApplyHotSauce(targetStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = targetStack.copyWithCount(1);
        result.set(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(), buildModifiedPassiveEffects(targetStack));
        return result;
    }

    private static List<FoodPassiveEffectComponent> buildModifiedPassiveEffects(ItemStack stack) {
        List<FoodPassiveEffectComponent> modifiedEffects = new ArrayList<>();
        boolean upgradedSpiciness = false;

        for (FoodPassiveEffectComponent passiveEffect : FoodPassiveEffects.get(stack)) {
            if (passiveEffect.type() == FoodPassiveEffectType.CONCENTRATED_SPICINESS) {
                return List.copyOf(FoodPassiveEffects.get(stack));
            }

            if (passiveEffect.type() == FoodPassiveEffectType.SPICINESS) {
                if (!upgradedSpiciness) {
                    modifiedEffects.add(FoodPassiveEffectComponent.of(FoodPassiveEffectType.CONCENTRATED_SPICINESS));
                    upgradedSpiciness = true;
                }
                continue;
            }

            modifiedEffects.add(passiveEffect);
        }

        if (!upgradedSpiciness) {
            modifiedEffects.add(FoodPassiveEffectComponent.of(FoodPassiveEffectType.SPICINESS));
        }

        return List.copyOf(modifiedEffects);
    }

    private static MatchContext findMatch(CraftingInput input) {
        int hotSauceCount = 0;
        int targetCount = 0;
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ConcoctionModItems.HOT_SAUCE_BOTTLE.get())) {
                hotSauceCount++;
                continue;
            }

            if (!isValidTarget(stack) || !canApplyHotSauce(stack)) {
                return null;
            }

            if (target.isEmpty()) {
                target = stack.copyWithCount(1);
            } else if (!ItemStack.isSameItemSameComponents(target, stack.copyWithCount(1))) {
                return null;
            }

            targetCount++;
        }

        if (hotSauceCount != 1 || target.isEmpty()) {
            return null;
        }

        BulkApplicationRule bulkRule = findBulkRule(target);
        if (bulkRule != null) {
            return targetCount == bulkRule.requiredTargetCount()
                    ? new MatchContext(target, targetCount)
                    : null;
        }

        return targetCount == 1 ? new MatchContext(target, 1) : null;
    }

    private static BulkApplicationRule findBulkRule(ItemStack target) {
        for (BulkApplicationRule rule : BULK_APPLICATION_RULES) {
            if (target.is(rule.item().get())) {
                return rule;
            }
        }
        return null;
    }

    private record MatchContext(ItemStack targetStack, int targetCount) {
    }

    private record BulkApplicationRule(Supplier<? extends Item> item, int requiredTargetCount) {
        private static BulkApplicationRule of(Supplier<? extends Item> item, int requiredTargetCount) {
            return new BulkApplicationRule(item, requiredTargetCount);
        }
    }

    public static class Serializer implements RecipeSerializer<HotSauceAdditionRecipe> {
        private static final MapCodec<HotSauceAdditionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(HotSauceAdditionRecipe::category)
                ).apply(inst, HotSauceAdditionRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, HotSauceAdditionRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                        },
                        buf -> new HotSauceAdditionRecipe(CraftingBookCategory.MISC)
                );

        @Override
        public MapCodec<HotSauceAdditionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotSauceAdditionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
