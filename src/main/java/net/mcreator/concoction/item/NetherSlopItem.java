package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class NetherSlopItem extends TastefulItem {

    // тег в persistentData игрока
    public static final String NETHER_SLOP_STACK_TAG = "concoction_nether_slop_stack";
    private static final int MAX_STACKS = 10;

    public NetherSlopItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                // можешь убрать/поменять эффект, если он больше не нужен
                .component(FOOD_EFFECT.value(),
                        new FoodEffectComponent(FoodEffectType.HEAL, 1, 180, true))
                .food(new FoodProperties.Builder()
                        .nutrition(8)              // базовое восстановление голода
                        .saturationModifier(0.6f)  // базовое насыщение
                        .build()
                )
        );
    }

    // ========= Работа со стеками в NBT =========

    public static int getNetherSlopStack(Player player) {
        if (player == null) return 0;
        CompoundTag data = player.getPersistentData();
        int value = data.getInt(NETHER_SLOP_STACK_TAG);
        if (value < 0) value = 0;
        if (value > MAX_STACKS) value = MAX_STACKS;
        return value;
    }

    public static void setNetherSlopStack(Player player, int value) {
        if (player == null) return;
        if (value < 0) value = 0;
        if (value > MAX_STACKS) value = MAX_STACKS;
        player.getPersistentData().putInt(NETHER_SLOP_STACK_TAG, value);
    }

    private static void applyNetherSlopEffectsAndProgress(Player player) {
        int stacks = getNetherSlopStack(player);

        if (stacks == 0) {
            // При стеке 0: 100% тошнота 30 сек + яд II на 8 сек
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 8 * 20, 1));
        } else {
            // Шанс тошноты 18 сек: 50% - 10% * stacks, минимум 0
            int chancePercent = 50 - stacks * 10;
            if (chancePercent < 0) {
                chancePercent = 0;
            }

            float chance = chancePercent / 100.0f;
            if (chance > 0.0f && player.getRandom().nextFloat() < chance) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 18 * 20));
            }

            // Бонус голода и насыщения:
            // +1 голода и +0.5 насыщения за каждый стак, до MAX_STACKS
            int effectiveStacks = Math.min(stacks, MAX_STACKS);
            if (effectiveStacks > 0) {
                int extraFood = effectiveStacks;                // +1 за стак
                float extraSaturation = effectiveStacks * 0.5f; // +0.5 за стак

                // vanilla сама ограничит foodLevel ≤ 20 и saturation ≤ foodLevel
                player.getFoodData().eat(extraFood, extraSaturation);
            }
        }

        // После съедания этого блюда: +1 стак (0..10)
        int newStacks = stacks + 1;
        if (newStacks > MAX_STACKS) {
            newStacks = MAX_STACKS;
        }
        setNetherSlopStack(player, newStacks);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack bowl = new ItemStack(Items.BOWL);

        // стандартное поведение еды (голод, насыщение, компоненты)
        super.finishUsingItem(stack, level, entity);

        // наша логика — только на сервере и только для игроков
        if (!level.isClientSide && entity instanceof Player player) {
            applyNetherSlopEffectsAndProgress(player);
        }

        // поведение миски как у супов
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return bowl;
            } else {
                if (!player.getInventory().add(bowl)) {
                    player.drop(bowl, false);
                }
            }
        }

        return stack;
    }
}
