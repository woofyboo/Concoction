package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@EventBusSubscriber
public class PlayerHandler {

    private static int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 200;

    private static final int SLEEP_TIMER_DURATION = 8 * 60 * 20; // 8 минут в тиках
    private static final int BASE_REGEN_INTERVAL = 80; // 4 секунды
    private static final double BASE_SPEED_MULTIPLIER = 0.1; // 10% (если потом пригодится)
    private static final int MAX_SURVIVAL_HP = 5; // макс. восстановление при уровне эффекта

    private static final ResourceKey<DamageType> SPICY_DAMAGE_KEY =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.parse("concoction:spicy_damage"));

    private static final TagKey<Item> SOULLAND_RELATION =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("concoction:soulland_relation"));
    private static final TagKey<Block> WILDLIFE_PLANTS =
            TagKey.create(Registries.BLOCK, ResourceLocation.parse("concoction:wildlife_plants"));
    private static final TagKey<Item> SPECIAL_FOOD =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));
    private static final TagKey<Item> SPECIAL_SOUP =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/soup"));
    private static final TagKey<EntityType<?>> FARM_ANIMALS =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("c:farm_animals"));

    // ===== Ачивки / инвентарь =====

    @SubscribeEvent
    public static void playerInventoryChangeEvent(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean hasTaggedItem = false;
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && stack.is(SOULLAND_RELATION)) {
                    hasTaggedItem = true;
                    break;
                }
            }

            if (hasTaggedItem) {
                Utils.addAchievement(player, "concoction:nether_mutation");
            }
        }
    }

    // ===== Еда / вкусы / ачивки =====

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack itemStack = event.getItem();

        // общая ачивка по тэгам
        if (itemStack.is(SPECIAL_FOOD) || itemStack.is(SPECIAL_SOUP)) {
            Utils.addAchievement(player, "concoction:eat_dish");
        }

        // проверяем все FOOD_EFFECT-компоненты
        FoodEffectComponent[] components = new FoodEffectComponent[]{
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_2.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_3.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_4.value()),
                itemStack.get(ConcoctionModDataComponents.FOOD_EFFECT_5.value())
        };

        for (FoodEffectComponent comp : components) {
            if (comp == null) continue;

            if (comp.type() == FoodEffectType.BREAKFAST &&
                    player.getPersistentData().getInt("sleep_timer") > 0) {

                Utils.addAchievement(player, "concoction:breakfast_check");
                break;
            }
        }
    }

    // ===== Блоки / инструменты =====

    @SubscribeEvent
    public static void playerBreaksBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {

            ItemStack itemStack = event.getPlayer().getInventory().getSelected();

            if (itemStack.getItem() instanceof OvergrownHoeItem) {
                event.setCanceled(itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1);
            }

            if (event.getState().is(WILDLIFE_PLANTS)) {
                Utils.addAchievement(player, "concoction:new_crops");
            }
        }
    }

    @SubscribeEvent
    public static void playerBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem ||
                itemStack.getItem() instanceof OvergrownSwordItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();

        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    // ===== Сон / BREAKFAST =====

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        player.getPersistentData().putInt("sleep_timer", SLEEP_TIMER_DURATION);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            // сбрасываем таймер сна
            player.getPersistentData().putInt("sleep_timer", 0);
        }

        if (!(event.getEntity() instanceof Player player)) return;

        if (player.hasEffect(ConcoctionModMobEffects.BREAKFAST) &&
                player.getPersistentData().getInt("sleep_timer") > 0) {

            MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
            int level = effect.getAmplifier() + 1;
            int minHP = Math.min(level, MAX_SURVIVAL_HP);

            // срабатывает только если здоровье ≥ 90% от максимального
            if (player.getHealth() >= player.getMaxHealth() * 0.9F) {
                player.setHealth(minHP);
                event.setCanceled(true); // отменяем смерть
                player.level().broadcastEntityEvent(player, (byte) 35); // харт-эффект
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getPersistentData().getInt("sleep_timer") > 0 &&
                player.hasEffect(ConcoctionModMobEffects.BREAKFAST)) {

            MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
            int level = effect.getAmplifier() + 1;

            // мгновенная реген после удара
            player.heal(1.0F);

            // защита от фатального удара
            float wouldBeHealth = player.getHealth() - event.getAmount();
            int minHP = Math.min(level, 5); // 1–5 HP
            if (wouldBeHealth <= 0 && player.getHealth() > player.getMaxHealth() * 0.9F) {
                event.setAmount(player.getHealth() - minHP);
            }
        }
    }

    // ===== SPICY / CREAMY / зачарование Butchering и т.п. =====

    @SubscribeEvent
    public static void entityAttacked(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();

        // CREAMY полностью игнорит spicy_damage
        if (event.getEntity() instanceof Player p) {
            if (p.hasEffect(ConcoctionModMobEffects.CREAMY) &&
                    event.getSource().is(SPICY_DAMAGE_KEY)) {
                event.setCanceled(true);
                return;
            }
        }

        // Доп. урон по farm_animals от Butchering
        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel =
                    itemStack.getEnchantmentLevel(
                            world.registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .getOrThrow(ResourceKey.create(
                                            Registries.ENCHANTMENT,
                                            ResourceLocation.parse("concoction:butchering")))
                    );

            if (enchantmentLevel > 0) {
                Entity damagedEntity = event.getEntity();
                if (damagedEntity.getType().is(FARM_ANIMALS)) {
                    event.setAmount((float) (event.getAmount() + (enchantmentLevel * 2.5)));
                }
            }
        }

        // защита overgrown инструментов — если почти сломаны, запретить урон
        if (source instanceof ServerPlayer player) {
            ItemStack itemStack = player.getInventory().getSelected();
            if (itemStack.getItem() instanceof OvergrownHoeItem ||
                    itemStack.getItem() instanceof OvergrownAxeItem ||
                    itemStack.getItem() instanceof OvergrownPickaxeItem ||
                    itemStack.getItem() instanceof OvergrownShovelItem ||
                    itemStack.getItem() instanceof OvergrownSwordItem) {
                if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityDied(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel =
                    itemStack.getEnchantmentLevel(
                            world.registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .getOrThrow(ResourceKey.create(
                                            Registries.ENCHANTMENT,
                                            ResourceLocation.parse("concoction:butchering")))
                    );

            if (enchantmentLevel > 0) {
                Entity damagedEntity = event.getEntity();
                if (damagedEntity.getType().is(FARM_ANIMALS)) {
                    if (new Random().nextInt(0, 10) < enchantmentLevel) {

                        if (damagedEntity instanceof Chicken) {
                            ItemEntity entityToSpawn = new ItemEntity(world,
                                    damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                    new ItemStack(Items.FEATHER, 1));
                            entityToSpawn.setPickUpDelay(10);
                            world.addFreshEntity(entityToSpawn);
                        }

                        if (damagedEntity instanceof Cow) {
                            ItemEntity entityToSpawn = new ItemEntity(world,
                                    damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                    new ItemStack(Items.LEATHER, 1));
                            entityToSpawn.setPickUpDelay(10);
                            world.addFreshEntity(entityToSpawn);
                        }

                        ItemEntity fat = new ItemEntity(world,
                                damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                new ItemStack(ConcoctionModItems.ANIMAL_FAT.get(), 1));
                        fat.setPickUpDelay(10);
                        world.addFreshEntity(fat);
                    }
                }
            }
        }
    }

    // ===== XP / BITTERNESS =====

    @SubscribeEvent
    public static void onGiveExperience(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {

            MobEffectInstance bitternessEffect = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
            int experience = event.getOrb().getValue();
            event.getOrb().value = (int) (experience * (1 + (0.5 * (bitternessEffect.getAmplifier() + 1))));
        }
    }

    // ===== Tick-логика: BREAKFAST regen + sleep_timer + ремонт овергроун =====

    @SubscribeEvent
    public static void playerTickEvent(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();

        // BREAKFAST пассивная регенерация
        if (player.getPersistentData().getInt("sleep_timer") > 0 &&
                player.hasEffect(ConcoctionModMobEffects.BREAKFAST)) {

            MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
            int level = effect.getAmplifier() + 1;

            int regenInterval = Math.max(1, BASE_REGEN_INTERVAL - 10 * (level - 1));

            int regenCounter = player.getPersistentData().getInt("breakfast_regen_counter") + 1;
            if (regenCounter >= regenInterval) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0F);
                }
                regenCounter = 0;
            }
            player.getPersistentData().putInt("breakfast_regen_counter", regenCounter);
        }

        // таймер сна: тикает вниз + сообщения
        int timer = player.getPersistentData().getInt("sleep_timer");
        if (timer > 0) {
            timer--;
            player.getPersistentData().putInt("sleep_timer", timer);

            if (timer == 1) {
                player.displayClientMessage(Component.translatable("message.concoction.tired"), true);
            } else if (timer == SLEEP_TIMER_DURATION - 1) {
                player.displayClientMessage(Component.translatable("message.concoction.rested"), true);
            }
        }

        // ремонт overgrown-инструментов от солнца
        tickCounter++;
        if (tickCounter > UPDATE_INTERVAL) {
            if (!player.level().isClientSide()
                    && player.level().isDay()
                    && player.level().canSeeSky(player.blockPosition().above())
                    && player instanceof ServerPlayer) {

                int multiplier = 1;
                if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                    multiplier = 2;
                }

                ItemStack mainHandItem = player.getInventory().getSelected();
                checkAndRepairItem(mainHandItem, multiplier, player.level());

                ItemStack offHandItem = player.getOffhandItem();
                checkAndRepairItem(offHandItem, multiplier, player.level());

                tickCounter = 0;
            }
        }
    }

    private static void checkAndRepairItem(ItemStack itemStack, int multiplier, Level level) {
        if (itemStack.isEmpty()) return;
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem ||
                itemStack.getItem() instanceof OvergrownSwordItem) {

            int currentDamage = itemStack.getDamageValue();
            if (currentDamage > 0) {
                if (itemStack.getEnchantmentLevel(
                        level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING)
                ) != 0 ||
                        itemStack.getEnchantmentLevel(
                                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)
                        ) != 0) {
                    return;
                }

                int newDamage = Math.max(0, currentDamage - 1 * multiplier);
                if (newDamage != currentDamage) {
                    itemStack.setDamageValue(newDamage);
                }
            }
        }
    }
}
