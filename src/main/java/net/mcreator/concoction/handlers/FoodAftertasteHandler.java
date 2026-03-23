package net.mcreator.concoction.handlers;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.mcreator.concoction.network.FoodAftertasteSyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class FoodAftertasteHandler {
    private static final String FOOD_HISTORY_TAG = "concoction_recent_food_history";
    private static final String FOOD_STACK_TAG = "stack";
    private static final String FOOD_PASSIVE_EFFECTS_TAG = "passive_effects";
    private static final String CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG = "concoction_active_aftertaste_entries_client";
    private static final int FOOD_HISTORY_LIMIT = 8;
    private static final int ACTIVE_AFTERTASTE_LIMIT = 3;
    private static final float CRISPY_CRUST_BREAK_SPEED_MULTIPLIER = 1.5F;

    private FoodAftertasteHandler() {
    }

    public static void recordConsumedFood(LivingEntity living, ItemStack consumedStack) {
        if (consumedStack.getFoodProperties(living) == null) {
            return;
        }

        CompoundTag entry = new CompoundTag();
        entry.put(FOOD_STACK_TAG, consumedStack.copyWithCount(1).save(living.level().registryAccess(), new CompoundTag()));
        entry.put(FOOD_PASSIVE_EFFECTS_TAG, writePassiveEffects(consumedStack));

        ListTag updatedHistory = new ListTag();
        updatedHistory.add(entry);

        ListTag existingHistory = getHistoryTag(living);
        for (int i = 0; i < Math.min(existingHistory.size(), FOOD_HISTORY_LIMIT - 1); i++) {
            updatedHistory.add(existingHistory.getCompound(i).copy());
        }

        living.getPersistentData().put(FOOD_HISTORY_TAG, updatedHistory);

        if (!living.level().isClientSide() && living instanceof ServerPlayer serverPlayer) {
            syncToClient(serverPlayer);
        }
    }

    public static boolean hasActiveAftertaste(LivingEntity living, FoodPassiveEffectType type) {
        return getActiveAftertasteEntries(living).stream().anyMatch(entry -> entry.type() == type);
    }

    public static List<FoodPassiveEffectType> getActiveAftertastes(LivingEntity living) {
        return getActiveAftertasteEntries(living).stream()
                .map(ActiveAftertasteEntry::type)
                .toList();
    }

    public static List<ActiveAftertasteEntry> getActiveAftertasteEntries(LivingEntity living) {
        if (living.level().isClientSide()) {
            return readClientActiveAftertasteEntries(living);
        }
        return readActiveAftertasteEntriesFromHistory(living);
    }

    public static void syncToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(
                player,
                new FoodAftertasteSyncPayload(
                        readActiveAftertasteEntriesFromHistory(player).stream()
                                .map(entry -> createSyncEntryTag(player, entry))
                                .toList()
                )
        );
    }

    public static void applyClientSync(Player player, List<CompoundTag> aftertasteEntries) {
        ListTag syncedEntries = new ListTag();
        for (CompoundTag entry : aftertasteEntries) {
            syncedEntries.add(entry.copy());
        }
        player.getPersistentData().put(CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG, syncedEntries);
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!hasActiveAftertaste(player, FoodPassiveEffectType.CRISPY_CRUST)) {
            return;
        }

        boolean hasCorrectTool = player.getMainHandItem().isCorrectToolForDrops(event.getState());
        if (!hasCorrectTool) {
            return;
        }

        event.setNewSpeed(event.getNewSpeed() * CRISPY_CRUST_BREAK_SPEED_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        if (newPlayer.level().isClientSide()) {
            return;
        }

        if (!event.isWasDeath()) {
            ListTag oldHistory = getHistoryTag(oldPlayer);
            if (!oldHistory.isEmpty()) {
                newPlayer.getPersistentData().put(FOOD_HISTORY_TAG, oldHistory.copy());
            }
        } else {
            newPlayer.getPersistentData().remove(FOOD_HISTORY_TAG);
        }

        newPlayer.getPersistentData().remove(CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG);

        if (newPlayer instanceof ServerPlayer serverPlayer) {
            syncToClient(serverPlayer);
        }
    }

    private static List<ActiveAftertasteEntry> readClientActiveAftertasteEntries(LivingEntity living) {
        List<ActiveAftertasteEntry> entries = new ArrayList<>();
        ListTag listTag = living.getPersistentData().getList(CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            parseActiveAftertasteEntry(living, listTag.getCompound(i)).ifPresent(entries::add);
        }
        return entries;
    }

    private static List<ActiveAftertasteEntry> readActiveAftertasteEntriesFromHistory(LivingEntity living) {
        List<ActiveAftertasteEntry> activeAftertastes = new ArrayList<>();
        EnumSet<FoodPassiveEffectType> seen = EnumSet.noneOf(FoodPassiveEffectType.class);
        ListTag history = getHistoryTag(living);

        for (int i = 0; i < history.size() && activeAftertastes.size() < ACTIVE_AFTERTASTE_LIMIT; i++) {
            CompoundTag entry = history.getCompound(i);
            ItemStack sourceStack = readEntryStack(living, entry);
            for (FoodPassiveEffectType effectType : readPassiveEffects(living, entry)) {
                if (!effectType.isAftertaste() || !seen.add(effectType)) {
                    continue;
                }

                boolean imminentExpiration = isAftertasteImminent(living, history, effectType);
                activeAftertastes.add(new ActiveAftertasteEntry(effectType, sourceStack.copy(), imminentExpiration));
                if (activeAftertastes.size() >= ACTIVE_AFTERTASTE_LIMIT) {
                    break;
                }
            }
        }

        return activeAftertastes;
    }

    private static List<FoodPassiveEffectType> readPassiveEffects(LivingEntity living, CompoundTag entry) {
        List<FoodPassiveEffectType> passiveEffects = new ArrayList<>();
        ListTag savedEffects = entry.getList(FOOD_PASSIVE_EFFECTS_TAG, Tag.TAG_STRING);
        for (int i = 0; i < savedEffects.size(); i++) {
            try {
                passiveEffects.add(FoodPassiveEffectType.getByName(savedEffects.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (!passiveEffects.isEmpty()) {
            return passiveEffects;
        }

        if (!entry.contains(FOOD_STACK_TAG, Tag.TAG_COMPOUND)) {
            return passiveEffects;
        }

        ItemStack stack = readEntryStack(living, entry);
        for (FoodPassiveEffectComponent passiveEffect : getPassiveEffectsFromStack(stack)) {
            passiveEffects.add(passiveEffect.type());
        }
        return passiveEffects;
    }

    private static ListTag writePassiveEffects(ItemStack stack) {
        ListTag listTag = new ListTag();
        List<FoodPassiveEffectComponent> passiveEffects = getPassiveEffectsFromStack(stack);
        for (FoodPassiveEffectComponent passiveEffect : passiveEffects) {
            listTag.add(StringTag.valueOf(passiveEffect.type().getSerializedName()));
        }
        return listTag;
    }

    private static ListTag getHistoryTag(LivingEntity living) {
        return living.getPersistentData().getList(FOOD_HISTORY_TAG, Tag.TAG_COMPOUND);
    }

    private static List<FoodPassiveEffectComponent> getPassiveEffectsFromStack(ItemStack stack) {
        List<FoodPassiveEffectComponent> passiveEffects = stack.get(FOOD_PASSIVE_EFFECTS.get());
        return passiveEffects != null ? passiveEffects : List.of();
    }

    private static boolean isAftertasteImminent(LivingEntity living, ListTag history, FoodPassiveEffectType type) {
        if (history.size() < FOOD_HISTORY_LIMIT) {
            return false;
        }

        CompoundTag oldestEntry = history.getCompound(history.size() - 1);
        if (!readPassiveEffects(living, oldestEntry).contains(type)) {
            return false;
        }

        for (int i = 0; i < history.size() - 1; i++) {
            if (readPassiveEffects(living, history.getCompound(i)).contains(type)) {
                return false;
            }
        }

        return true;
    }

    private static ItemStack readEntryStack(LivingEntity living, CompoundTag entry) {
        if (!entry.contains(FOOD_STACK_TAG, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.parseOptional(living.level().registryAccess(), entry.getCompound(FOOD_STACK_TAG));
    }

    private static CompoundTag createSyncEntryTag(ServerPlayer player, ActiveAftertasteEntry entry) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", entry.type().getSerializedName());
        tag.put(FOOD_STACK_TAG, entry.sourceStack().save(player.level().registryAccess(), new CompoundTag()));
        tag.putBoolean("imminent_expiration", entry.imminentExpiration());
        return tag;
    }

    private static java.util.Optional<ActiveAftertasteEntry> parseActiveAftertasteEntry(LivingEntity living, CompoundTag tag) {
        if (!tag.contains("type", Tag.TAG_STRING)) {
            return java.util.Optional.empty();
        }

        try {
            FoodPassiveEffectType type = FoodPassiveEffectType.getByName(tag.getString("type"));
            return java.util.Optional.of(new ActiveAftertasteEntry(
                    type,
                    readEntryStack(living, tag),
                    tag.getBoolean("imminent_expiration")
            ));
        } catch (IllegalArgumentException ignored) {
            return java.util.Optional.empty();
        }
    }

    public record ActiveAftertasteEntry(FoodPassiveEffectType type, ItemStack sourceStack, boolean imminentExpiration) {
    }
}
