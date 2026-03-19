package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.Random;

@EventBusSubscriber
public class PlayerHandler {
    private static final ResourceKey<DamageType> SPICY_DAMAGE_KEY =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:spicy_damage"));

    private static final TagKey<Item> SOULLAND_RELATION =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("concoction:soulland_relation"));
    private static final TagKey<Item> SPECIAL_FOOD =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));
    private static final TagKey<Item> SPECIAL_SOUP =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/soup"));
    private static final TagKey<EntityType<?>> FARM_ANIMALS =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("c:farm_animals"));

    @SubscribeEvent
    public static void playerInventoryChangeEvent(PlayerContainerEvent.Close event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean hasTaggedItem = false;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(SOULLAND_RELATION)) {
                hasTaggedItem = true;
                break;
            }
        }

        if (hasTaggedItem) {
            Utils.grantAdvancement(player, "concoction:nether_mutation");
        }
    }

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (itemStack.is(SPECIAL_FOOD) || itemStack.is(SPECIAL_SOUP)) {
            Utils.grantAdvancement(player, "concoction:eat_dish");
        }

        FoodEffectComponent[] components = new FoodEffectComponent[]{
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_2.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_3.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_4.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_5.value())
        };

        for (FoodEffectComponent component : components) {
            if (component != null
                    && component.type() == FoodEffectType.BREAKFAST
                    && player.getPersistentData().getInt(BreakfastPlayerHandler.SLEEP_TIMER_KEY) > 0) {
                Utils.grantAdvancement(player, "concoction:breakfast_check");
                break;
            }
        }
    }

    @SubscribeEvent
    public static void entityAttacked(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();

        if (event.getEntity() instanceof Player player
                && player.hasEffect(ConcoctionModMobEffects.CREAMY)
                && event.getSource().is(SPICY_DAMAGE_KEY)) {
            event.setCanceled(true);
            return;
        }

        if (source instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getItemInHand(InteractionHand.MAIN_HAND);
            Level level = attacker.level();
            int butcheringLevel = weapon.getEnchantmentLevel(
                    level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering")))
            );

            if (butcheringLevel > 0 && event.getEntity().getType().is(FARM_ANIMALS)) {
                event.setAmount((float) (event.getAmount() + butcheringLevel * 2.5));
            }
        }

        if (source instanceof ServerPlayer player && OvergrownToolHandler.shouldCancelAttack(player.getInventory().getSelected())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void entityDied(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) {
            return;
        }

        ItemStack weapon = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        Level level = attacker.level();
        int butcheringLevel = weapon.getEnchantmentLevel(
                level.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering")))
        );

        Entity victim = event.getEntity();
        if (butcheringLevel <= 0 || !victim.getType().is(FARM_ANIMALS) || new Random().nextInt(0, 10) >= butcheringLevel) {
            return;
        }

        if (victim instanceof Chicken) {
            ItemEntity feather = new ItemEntity(level, victim.getX(), victim.getY() + 0.5D, victim.getZ(), new ItemStack(Items.FEATHER, 1));
            feather.setPickUpDelay(10);
            level.addFreshEntity(feather);
        }

        if (victim instanceof Cow) {
            ItemEntity leather = new ItemEntity(level, victim.getX(), victim.getY() + 0.5D, victim.getZ(), new ItemStack(Items.LEATHER, 1));
            leather.setPickUpDelay(10);
            level.addFreshEntity(leather);
        }

        ItemEntity fat = new ItemEntity(level, victim.getX(), victim.getY() + 0.5D, victim.getZ(), new ItemStack(ConcoctionModItems.ANIMAL_FAT.get(), 1));
        fat.setPickUpDelay(10);
        level.addFreshEntity(fat);
    }

    @SubscribeEvent
    public static void onGiveExperience(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (!player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {
            return;
        }

        MobEffectInstance bitternessEffect = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
        int experience = event.getOrb().getValue();
        event.getOrb().value = (int) (experience * (1 + (0.5 * (bitternessEffect.getAmplifier() + 1))));
    }
}
