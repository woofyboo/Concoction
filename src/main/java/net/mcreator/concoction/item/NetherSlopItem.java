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

public class NetherSlopItem extends Item {

    // С‚РµРі РІ persistentData РёРіСЂРѕРєР°
    public static final String NETHER_SLOP_STACK_TAG = "concoction_nether_slop_stack";
    private static final int MAX_STACKS = 10;

    public NetherSlopItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                // РјРѕР¶РµС€СЊ СѓР±СЂР°С‚СЊ/РїРѕРјРµРЅСЏС‚СЊ СЌС„С„РµРєС‚, РµСЃР»Рё РѕРЅ Р±РѕР»СЊС€Рµ РЅРµ РЅСѓР¶РµРЅ
                .food(new FoodProperties.Builder()
                        .nutrition(8)              // Р±Р°Р·РѕРІРѕРµ РІРѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёРµ РіРѕР»РѕРґР°
                        .saturationModifier(0.6f)  // Р±Р°Р·РѕРІРѕРµ РЅР°СЃС‹С‰РµРЅРёРµ
                        .build()
                )
        );
    }

    // ========= Р Р°Р±РѕС‚Р° СЃРѕ СЃС‚РµРєР°РјРё РІ NBT =========

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
            // РџСЂРё СЃС‚РµРєРµ 0: 100% С‚РѕС€РЅРѕС‚Р° 30 СЃРµРє + СЏРґ II РЅР° 8 СЃРµРє
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 8 * 20, 1));
        } else {
            // РЁР°РЅСЃ С‚РѕС€РЅРѕС‚С‹ 18 СЃРµРє: 50% - 10% * stacks, РјРёРЅРёРјСѓРј 0
            int chancePercent = 50 - stacks * 10;
            if (chancePercent < 0) {
                chancePercent = 0;
            }

            float chance = chancePercent / 100.0f;
            if (chance > 0.0f && player.getRandom().nextFloat() < chance) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 18 * 20));
            }

            // Р‘РѕРЅСѓСЃ РіРѕР»РѕРґР° Рё РЅР°СЃС‹С‰РµРЅРёСЏ:
            // +1 РіРѕР»РѕРґР° Рё +0.5 РЅР°СЃС‹С‰РµРЅРёСЏ Р·Р° РєР°Р¶РґС‹Р№ СЃС‚Р°Рє, РґРѕ MAX_STACKS
            int effectiveStacks = Math.min(stacks, MAX_STACKS);
            if (effectiveStacks > 0) {
                int extraFood = effectiveStacks;                // +1 Р·Р° СЃС‚Р°Рє
                float extraSaturation = effectiveStacks * 0.5f; // +0.5 Р·Р° СЃС‚Р°Рє

                // vanilla СЃР°РјР° РѕРіСЂР°РЅРёС‡РёС‚ foodLevel в‰¤ 20 Рё saturation в‰¤ foodLevel
                player.getFoodData().eat(extraFood, extraSaturation);
            }
        }

        // РџРѕСЃР»Рµ СЃСЉРµРґР°РЅРёСЏ СЌС‚РѕРіРѕ Р±Р»СЋРґР°: +1 СЃС‚Р°Рє (0..10)
        int newStacks = stacks + 1;
        if (newStacks > MAX_STACKS) {
            newStacks = MAX_STACKS;
        }
        setNetherSlopStack(player, newStacks);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack bowl = new ItemStack(Items.BOWL);

        // СЃС‚Р°РЅРґР°СЂС‚РЅРѕРµ РїРѕРІРµРґРµРЅРёРµ РµРґС‹ (РіРѕР»РѕРґ, РЅР°СЃС‹С‰РµРЅРёРµ, РєРѕРјРїРѕРЅРµРЅС‚С‹)
        super.finishUsingItem(stack, level, entity);

        // РЅР°С€Р° Р»РѕРіРёРєР° вЂ” С‚РѕР»СЊРєРѕ РЅР° СЃРµСЂРІРµСЂРµ Рё С‚РѕР»СЊРєРѕ РґР»СЏ РёРіСЂРѕРєРѕРІ
        if (!level.isClientSide && entity instanceof Player player) {
            applyNetherSlopEffectsAndProgress(player);
        }

        // РїРѕРІРµРґРµРЅРёРµ РјРёСЃРєРё РєР°Рє Сѓ СЃСѓРїРѕРІ
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

