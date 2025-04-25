package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.*;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Random;

@EventBusSubscriber
public class PlayerHandler {
    private static int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 200;

    private static final TagKey<Item> SOULLAND_RELATION = TagKey.create(Registries.ITEM, ResourceLocation.parse("concoction:soulland_relation"));
    private static final TagKey<Block> WILDLIFE_PLANTS = TagKey.create(Registries.BLOCK, ResourceLocation.parse("concoction:wildlife_plants"));
    private static final TagKey<Item> SPECIAL_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));
    private static final TagKey<Item> SPECIAL_SOUP = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/soup"));
    private static final TagKey<EntityType<?>> FARM_ANIMALS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("c:farm_animals"));

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

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack itemStack = event.getItem();
            if (itemStack.is(SPECIAL_FOOD) || itemStack.is(SPECIAL_SOUP)) {
                Utils.addAchievement(player, "concoction:eat_dish");
            }
        }
    }

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
    public static void playerBreaksBlock(PlayerInteractEvent.EntityInteract event) {
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

    @SubscribeEvent
    public static void entityDied(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel = itemStack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering"))));
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

                        ItemEntity entityToSpawn = new ItemEntity(world,
                                damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                new ItemStack(ConcoctionModItems.ANIMAL_FAT.get(), 1));
                        entityToSpawn.setPickUpDelay(10);
                        world.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityAttacked(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel = itemStack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering"))));
            if (enchantmentLevel > 0) {
                Entity damagedEntity = event.getEntity();
                if (damagedEntity.getType().is(FARM_ANIMALS))
                {
                    event.setAmount((float) (event.getAmount() + (enchantmentLevel * 2.5)));
                }

                System.out.println(event.getAmount());
            }
        }

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
    public static void playerTickEvent(PlayerTickEvent.Pre event) {
        tickCounter++;

        if (tickCounter > UPDATE_INTERVAL) {
            Player player = event.getEntity();
            ItemStack mainHandItem = player.getInventory().getSelected();

            if (!player.level().isClientSide() && player.level().isDay() && player.level().canSeeSky(player.blockPosition().above()) && player instanceof ServerPlayer) {
                int multiplier = 1;

                if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                    multiplier = 2;
                }
                
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
                itemStack.getItem() instanceof OvergrownSwordItem)
        {
            int currentDamage = itemStack.getDamageValue();
            if (currentDamage > 0) {
                if (itemStack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING)) != 0 ||
                        itemStack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)) != 0) return;
                int newDamage = Math.max(0, currentDamage - 1*multiplier);
                if (newDamage != currentDamage) {
                    itemStack.setDamageValue(newDamage);
                }
            }
        }
    }

}
