package net.mcreator.concoction.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModEnchantments;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private static final TagKey<Item> MEAT_ITEMS =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:meat"));

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
            int butcheringLevel = getButcheringLevel(attacker);
            if (butcheringLevel > 0 && qualifiesForButchering(event.getEntity())) {
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
        if (!(source instanceof LivingEntity attacker) || !(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity victim = event.getEntity();
        int butcheringLevel = getButcheringLevel(attacker);
        if (butcheringLevel <= 0 || !qualifiesForButchering(victim) || serverLevel.random.nextFloat() >= butcheringLevel * 0.2F) {
            return;
        }

        spawnButcheringDrop(serverLevel, victim, getButcheringBonusDrop(victim));
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

    private static int getButcheringLevel(LivingEntity attacker) {
        ItemStack weapon = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment = attacker.level()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ConcoctionModEnchantments.BUTCHERING);
        return weapon.getEnchantmentLevel(enchantment);
    }

    private static boolean qualifiesForButchering(LivingEntity victim) {
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        ResourceKey<LootTable> lootTableKey = victim.getLootTable();
        return lootTableKey != null && lootTableContainsMeat(serverLevel, lootTableKey, new HashSet<>());
    }

    private static boolean lootTableContainsMeat(ServerLevel serverLevel, ResourceKey<LootTable> lootTableKey, Set<ResourceKey<LootTable>> visitedTables) {
        if (!visitedTables.add(lootTableKey)) {
            return false;
        }

        JsonElement lootJson = loadLootTableJson(serverLevel, lootTableKey);
        return lootJson != null && serializedLootContainsMeat(serverLevel, lootJson, visitedTables);
    }

    private static boolean serializedLootContainsMeat(ServerLevel serverLevel, JsonElement element, Set<ResourceKey<LootTable>> visitedTables) {
        if (element == null || element.isJsonNull()) {
            return false;
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (serializedLootContainsMeat(serverLevel, child, visitedTables)) {
                    return true;
                }
            }
            return false;
        }

        if (!element.isJsonObject()) {
            return false;
        }

        JsonObject object = element.getAsJsonObject();
        String type = getStringProperty(object, "type");
        if ("minecraft:item".equals(type) && jsonItemMatchesMeat(serverLevel, object.get("name"))) {
            return true;
        }

        if ("minecraft:tag".equals(type) && jsonTagCanProduceMeat(serverLevel, object.get("name"))) {
            return true;
        }

        if ("minecraft:loot_table".equals(type) && nestedLootTableContainsMeat(serverLevel, object.get("value"), visitedTables)) {
            return true;
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (serializedLootContainsMeat(serverLevel, entry.getValue(), visitedTables)) {
                return true;
            }
        }

        return false;
    }

    private static boolean nestedLootTableContainsMeat(ServerLevel serverLevel, JsonElement nestedValue, Set<ResourceKey<LootTable>> visitedTables) {
        if (nestedValue == null || nestedValue.isJsonNull()) {
            return false;
        }

        if (nestedValue.isJsonPrimitive()) {
            ResourceLocation lootTableId = parseResourceLocation(nestedValue.getAsString());
            return lootTableId != null
                    && lootTableContainsMeat(serverLevel, ResourceKey.create(Registries.LOOT_TABLE, lootTableId), visitedTables);
        }

        return serializedLootContainsMeat(serverLevel, nestedValue, visitedTables);
    }

    private static boolean jsonItemMatchesMeat(ServerLevel serverLevel, JsonElement itemElement) {
        ResourceLocation itemId = readId(itemElement);
        if (itemId == null) {
            return false;
        }

        return serverLevel.registryAccess()
                .lookupOrThrow(Registries.ITEM)
                .get(ResourceKey.create(Registries.ITEM, itemId))
                .map(holder -> holder.is(MEAT_ITEMS))
                .orElse(false);
    }

    private static boolean jsonTagCanProduceMeat(ServerLevel serverLevel, JsonElement tagElement) {
        ResourceLocation tagId = readId(tagElement);
        if (tagId == null) {
            return false;
        }

        TagKey<Item> itemTag = TagKey.create(Registries.ITEM, tagId);
        if (itemTag.equals(MEAT_ITEMS)) {
            return true;
        }

        return serverLevel.registryAccess()
                .lookupOrThrow(Registries.ITEM)
                .get(itemTag)
                .map(tag -> tag.stream().anyMatch(holder -> holder.is(MEAT_ITEMS)))
                .orElse(false);
    }

    private static ItemStack getButcheringBonusDrop(LivingEntity victim) {
        if (victim instanceof Chicken) {
            return new ItemStack(Items.FEATHER);
        }

        if (victim instanceof Pig || victim instanceof Hoglin) {
            return new ItemStack(ConcoctionModItems.ANIMAL_FAT.get());
        }

        if (victim instanceof Cow || victim instanceof Horse) {
            return new ItemStack(Items.LEATHER);
        }

        if (victim instanceof Sheep sheep) {
            return new ItemStack(getWoolBlock(sheep.getColor()));
        }

        if (victim instanceof Goat) {
            return new ItemStack(Blocks.WHITE_WOOL);
        }

        return new ItemStack(Items.BONE);
    }

    private static Item getWoolBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_WOOL.asItem();
            case ORANGE -> Blocks.ORANGE_WOOL.asItem();
            case MAGENTA -> Blocks.MAGENTA_WOOL.asItem();
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL.asItem();
            case YELLOW -> Blocks.YELLOW_WOOL.asItem();
            case LIME -> Blocks.LIME_WOOL.asItem();
            case PINK -> Blocks.PINK_WOOL.asItem();
            case GRAY -> Blocks.GRAY_WOOL.asItem();
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL.asItem();
            case CYAN -> Blocks.CYAN_WOOL.asItem();
            case PURPLE -> Blocks.PURPLE_WOOL.asItem();
            case BLUE -> Blocks.BLUE_WOOL.asItem();
            case BROWN -> Blocks.BROWN_WOOL.asItem();
            case GREEN -> Blocks.GREEN_WOOL.asItem();
            case RED -> Blocks.RED_WOOL.asItem();
            case BLACK -> Blocks.BLACK_WOOL.asItem();
        };
    }

    private static void spawnButcheringDrop(ServerLevel serverLevel, LivingEntity victim, ItemStack dropStack) {
        if (dropStack.isEmpty()) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(serverLevel, victim.getX(), victim.getY() + 0.5D, victim.getZ(), dropStack);
        itemEntity.setPickUpDelay(10);
        serverLevel.addFreshEntity(itemEntity);
    }

    private static String getStringProperty(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    private static ResourceLocation readId(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return null;
        }

        return parseResourceLocation(element.getAsString());
    }

    private static ResourceLocation parseResourceLocation(String id) {
        try {
            return ResourceLocation.parse(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static JsonElement loadLootTableJson(ServerLevel serverLevel, ResourceKey<LootTable> lootTableKey) {
        ResourceLocation lootTableId = lootTableKey.location();
        ResourceLocation resourceId = ResourceLocation.fromNamespaceAndPath(
                lootTableId.getNamespace(),
                "loot_table/" + lootTableId.getPath() + ".json"
        );

        return serverLevel.getServer()
                .getResourceManager()
                .getResource(resourceId)
                .map(PlayerHandler::readJsonResource)
                .orElse(null);
    }

    private static JsonElement readJsonResource(Resource resource) {
        try (var reader = resource.openAsReader()) {
            return com.google.gson.JsonParser.parseReader(reader);
        } catch (IOException | RuntimeException ignored) {
            return null;
        }
    }
}
