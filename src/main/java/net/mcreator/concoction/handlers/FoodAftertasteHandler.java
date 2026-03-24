package net.mcreator.concoction.handlers;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffects;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.mcreator.concoction.network.FoodAftertasteSyncPayload;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class FoodAftertasteHandler {
    private static final String FOOD_HISTORY_TAG = "concoction_recent_food_history";
    private static final String FOOD_STACK_TAG = "stack";
    private static final String FOOD_PASSIVE_EFFECTS_TAG = "passive_effects";
    private static final String FOOD_DISABLED_AFTERTASTES_TAG = "disabled_aftertastes";
    private static final String PERMANENT_AFTERTASTES_TAG = "concoction_permanent_aftertastes";
    private static final String DISABLED_PERMANENT_AFTERTASTES_TAG = "concoction_disabled_permanent_aftertastes";
    private static final String CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG = "concoction_active_aftertaste_entries_client";
    private static final int FOOD_HISTORY_LIMIT = 8;
    private static final int ACTIVE_AFTERTASTE_FOOD_LIMIT = 3;
    private static final float CRISPY_CRUST_BREAK_SPEED_MULTIPLIER = 1.5F;
    private static final float BOUNCY_JELLY_DAMAGE_MULTIPLIER = 0.1F;
    private static final double BOUNCY_JELLY_MIN_BOUNCE = 0.6D;
    private static final double BOUNCY_JELLY_MAX_BOUNCE = 1.35D;
    private static final double STICKY_MOVEMENT_SPEED_MULTIPLIER = -0.15D;
    private static final double STICKY_KNOCKBACK_RESISTANCE_BONUS = 0.75D;
    private static final double STICKY_JUMP_MULTIPLIER = 0.9D;
    private static final ResourceLocation STICKY_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            ConcoctionMod.MODID,
            "aftertaste_sticky_movement_speed"
    );
    private static final ResourceLocation STICKY_KNOCKBACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            ConcoctionMod.MODID,
            "aftertaste_sticky_knockback_resistance"
    );

    private FoodAftertasteHandler() {
    }

    public static void recordConsumedFood(LivingEntity living, ItemStack consumedStack) {
        if (consumedStack.getFoodProperties(living) == null && FoodPassiveEffects.get(consumedStack).isEmpty()) {
            return;
        }

        CompoundTag entry = new CompoundTag();
        entry.put(FOOD_STACK_TAG, consumedStack.copyWithCount(1).save(living.level().registryAccess(), new CompoundTag()));
        entry.put(FOOD_PASSIVE_EFFECTS_TAG, writePassiveEffects(consumedStack));
        entry.put(FOOD_DISABLED_AFTERTASTES_TAG, new ListTag());

        ListTag updatedHistory = new ListTag();
        updatedHistory.add(entry);

        ListTag existingHistory = getHistoryTag(living);
        for (int i = 0; i < Math.min(existingHistory.size(), FOOD_HISTORY_LIMIT - 1); i++) {
            updatedHistory.add(existingHistory.getCompound(i).copy());
        }

        setHistory(living, updatedHistory);
        refreshAftertasteState(living);
    }

    public static boolean hasEnabledAftertaste(LivingEntity living, FoodPassiveEffectType type) {
        return getActiveAftertasteEntries(living).stream().anyMatch(entry -> entry.type() == type && !entry.disabled());
    }

    public static List<FoodPassiveEffectType> getActiveAftertastes(LivingEntity living) {
        return getActiveAftertasteEntries(living).stream()
                .filter(entry -> !entry.disabled())
                .map(ActiveAftertasteEntry::type)
                .toList();
    }

    public static int countRecentFoodsWithPassiveEffect(LivingEntity living, FoodPassiveEffectType type) {
        int count = 0;
        for (FoodHistoryEntry entry : readHistoryEntries(living, getHistoryTag(living))) {
            if (entry.passiveEffects().contains(type)) {
                count++;
            }
        }
        return count;
    }

    public static int countRecentFoodsMatchingStack(LivingEntity living, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        String targetSignature = stack.copyWithCount(1).save(living.level().registryAccess(), new CompoundTag()).toString();
        int count = 0;
        for (FoodHistoryEntry entry : readHistoryEntries(living, getHistoryTag(living))) {
            if (entry.foodSignature().equals(targetSignature)) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasOnlyConsumedMatchingFoodRecently(LivingEntity living, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        List<FoodHistoryEntry> entries = readHistoryEntries(living, getHistoryTag(living));
        if (entries.isEmpty()) {
            return false;
        }

        String targetSignature = stack.copyWithCount(1).save(living.level().registryAccess(), new CompoundTag()).toString();
        for (FoodHistoryEntry entry : entries) {
            if (!entry.foodSignature().equals(targetSignature)) {
                return false;
            }
        }
        return true;
    }

    public static List<ActiveAftertasteEntry> getActiveAftertasteEntries(LivingEntity living) {
        if (living.level().isClientSide()) {
            return readClientActiveAftertasteEntries(living);
        }
        return readActiveAftertasteEntriesFromState(living);
    }

    public static void removeOldestAftertasteFoodOrOldestFood(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        ListTag history = getHistoryTag(living);
        if (history.isEmpty()) {
            return;
        }

        List<FoodHistoryEntry> entries = readHistoryEntries(living, history);
        int removeIndex = -1;
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (entries.get(i).hasAnyAftertaste()) {
                removeIndex = entries.get(i).historyIndex();
                break;
            }
        }

        if (removeIndex < 0) {
            removeIndex = history.size() - 1;
        }

        history.remove(removeIndex);
        setHistory(living, history);
        refreshAftertasteState(living);
    }

    public static void clearFoodHistory(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        living.getPersistentData().remove(FOOD_HISTORY_TAG);
        refreshAftertasteState(living);
    }

    public static boolean addPermanentAftertaste(LivingEntity living, FoodPassiveEffectType type) {
        if (living.level().isClientSide() || !type.isAftertaste()) {
            return false;
        }

        EnumSet<FoodPassiveEffectType> permanent = getPermanentAftertastes(living);
        EnumSet<FoodPassiveEffectType> disabledPermanent = getDisabledPermanentAftertastes(living);
        boolean changed = permanent.add(type);
        changed |= disabledPermanent.remove(type);
        if (changed) {
            setPermanentAftertastes(living, permanent);
            setDisabledPermanentAftertastes(living, disabledPermanent);
            refreshAftertasteState(living);
        }
        return changed;
    }

    public static boolean removeAftertasteCompletely(LivingEntity living, FoodPassiveEffectType type) {
        if (living.level().isClientSide() || !type.isAftertaste()) {
            return false;
        }

        boolean changed = false;
        EnumSet<FoodPassiveEffectType> permanent = getPermanentAftertastes(living);
        EnumSet<FoodPassiveEffectType> disabledPermanent = getDisabledPermanentAftertastes(living);
        if (permanent.remove(type)) {
            setPermanentAftertastes(living, permanent);
            changed = true;
        }
        if (disabledPermanent.remove(type)) {
            setDisabledPermanentAftertastes(living, disabledPermanent);
            changed = true;
        }

        ListTag history = getHistoryTag(living);
        for (int i = 0; i < history.size(); i++) {
            CompoundTag entry = history.getCompound(i).copy();
            if (!readPassiveEffects(living, entry).contains(type)) {
                continue;
            }

            EnumSet<FoodPassiveEffectType> disabled = readDisabledAftertastes(entry);
            if (disabled.add(type)) {
                entry.put(FOOD_DISABLED_AFTERTASTES_TAG, writeEffectNames(disabled));
                history.set(i, entry);
                changed = true;
            }
        }

        if (changed) {
            setHistory(living, history);
            refreshAftertasteState(living);
        }
        return changed;
    }

    public static void clearAllAftertastes(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        living.getPersistentData().remove(PERMANENT_AFTERTASTES_TAG);
        living.getPersistentData().remove(DISABLED_PERMANENT_AFTERTASTES_TAG);
        living.getPersistentData().remove(FOOD_HISTORY_TAG);
        refreshAftertasteState(living);
    }

    public static boolean reactivateExhaustedAftertastes(LivingEntity living) {
        if (living.level().isClientSide()) {
            return false;
        }

        boolean changed = false;

        EnumSet<FoodPassiveEffectType> disabledPermanent = getDisabledPermanentAftertastes(living);
        EnumSet<FoodPassiveEffectType> reactivatedPermanent = EnumSet.noneOf(FoodPassiveEffectType.class);
        for (FoodPassiveEffectType type : disabledPermanent) {
            if (type.canBeReactivated()) {
                reactivatedPermanent.add(type);
            }
        }
        if (!reactivatedPermanent.isEmpty()) {
            disabledPermanent.removeAll(reactivatedPermanent);
            setDisabledPermanentAftertastes(living, disabledPermanent);
            changed = true;
        }

        ListTag history = getHistoryTag(living);
        for (int i = 0; i < history.size(); i++) {
            CompoundTag entry = history.getCompound(i).copy();
            EnumSet<FoodPassiveEffectType> disabled = readDisabledAftertastes(entry);
            EnumSet<FoodPassiveEffectType> reactivated = EnumSet.noneOf(FoodPassiveEffectType.class);
            for (FoodPassiveEffectType type : disabled) {
                if (type.canBeReactivated()) {
                    reactivated.add(type);
                }
            }

            if (reactivated.isEmpty()) {
                continue;
            }

            disabled.removeAll(reactivated);
            entry.put(FOOD_DISABLED_AFTERTASTES_TAG, writeEffectNames(disabled));
            history.set(i, entry);
            changed = true;
        }

        if (changed) {
            setHistory(living, history);
            refreshAftertasteState(living);
        }

        return changed;
    }

    public static boolean disableActiveAftertaste(LivingEntity living, FoodPassiveEffectType type) {
        if (living.level().isClientSide()) {
            return false;
        }

        EnumSet<FoodPassiveEffectType> permanent = getPermanentAftertastes(living);
        if (permanent.contains(type)) {
            EnumSet<FoodPassiveEffectType> disabledPermanent = getDisabledPermanentAftertastes(living);
            if (disabledPermanent.add(type)) {
                setDisabledPermanentAftertastes(living, disabledPermanent);
                refreshAftertasteState(living);
                return true;
            }
        }

        ListTag history = getHistoryTag(living);
        if (history.isEmpty()) {
            return false;
        }

        List<FoodHistoryEntry> selectedFoods = selectAftertasteFoods(readHistoryEntries(living, history));
        for (FoodHistoryEntry entry : selectedFoods) {
            if (!entry.aftertasteTypes().contains(type) || entry.disabledAftertastes().contains(type)) {
                continue;
            }

            CompoundTag updatedEntry = history.getCompound(entry.historyIndex()).copy();
            EnumSet<FoodPassiveEffectType> disabled = EnumSet.noneOf(FoodPassiveEffectType.class);
            disabled.addAll(entry.disabledAftertastes());
            disabled.add(type);
            updatedEntry.put(FOOD_DISABLED_AFTERTASTES_TAG, writeEffectNames(disabled));
            history.set(entry.historyIndex(), updatedEntry);
            setHistory(living, history);
            refreshAftertasteState(living);
            return true;
        }

        return false;
    }

    public static void syncToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(
                player,
                new FoodAftertasteSyncPayload(
                        readActiveAftertasteEntriesFromState(player).stream()
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
        if (!hasEnabledAftertaste(player, FoodPassiveEffectType.CRISPY_CRUST)) {
            return;
        }

        if (!player.getMainHandItem().isCorrectToolForDrops(event.getState())) {
            return;
        }

        event.setNewSpeed(event.getNewSpeed() * CRISPY_CRUST_BREAK_SPEED_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) {
            return;
        }

        if (!event.getSource().is(DamageTypeTags.IS_FALL) || event.getAmount() <= 0.0F) {
            return;
        }

        if (!hasEnabledAftertaste(living, FoodPassiveEffectType.JITTERING_JELLY)) {
            return;
        }

        event.setAmount(event.getAmount() * BOUNCY_JELLY_DAMAGE_MULTIPLIER);

        Vec3 movement = living.getDeltaMovement();
        double bounceStrength = Math.max(
                BOUNCY_JELLY_MIN_BOUNCE,
                Math.min(BOUNCY_JELLY_MAX_BOUNCE, Math.sqrt(Math.max(living.fallDistance, 3.0F)) * 0.35D)
        );
        living.setDeltaMovement(movement.x, bounceStrength, movement.z);
        living.hurtMarked = true;
        disableActiveAftertaste(living, FoodPassiveEffectType.JITTERING_JELLY);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refreshAftertasteState(player);
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
        refreshAftertasteState(newPlayer);
    }

    private static void refreshAftertasteState(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        boolean stickyActive = hasEnabledAftertasteInState(living, FoodPassiveEffectType.STICKY_VISCOSITY);
        updateAttributeModifier(
                living,
                Attributes.MOVEMENT_SPEED,
                STICKY_SPEED_MODIFIER_ID,
                STICKY_MOVEMENT_SPEED_MULTIPLIER,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                stickyActive
        );
        updateAttributeModifier(
                living,
                Attributes.KNOCKBACK_RESISTANCE,
                STICKY_KNOCKBACK_MODIFIER_ID,
                STICKY_KNOCKBACK_RESISTANCE_BONUS,
                AttributeModifier.Operation.ADD_VALUE,
                stickyActive
        );

        if (living instanceof ServerPlayer serverPlayer) {
            syncToClient(serverPlayer);
        }
    }

    private static void updateAttributeModifier(
            LivingEntity living,
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount,
            AttributeModifier.Operation operation,
            boolean enabled
    ) {
        AttributeInstance attributeInstance = living.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }

        attributeInstance.removeModifier(modifierId);
        if (enabled) {
            attributeInstance.addTransientModifier(new AttributeModifier(modifierId, amount, operation));
        }
    }

    private static boolean hasEnabledAftertasteInState(LivingEntity living, FoodPassiveEffectType type) {
        return readActiveAftertasteEntriesFromState(living).stream().anyMatch(entry -> entry.type() == type && !entry.disabled());
    }

    private static List<ActiveAftertasteEntry> readClientActiveAftertasteEntries(LivingEntity living) {
        List<ActiveAftertasteEntry> entries = new ArrayList<>();
        ListTag listTag = living.getPersistentData().getList(CLIENT_ACTIVE_AFTERTASTE_ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            parseActiveAftertasteEntry(living, listTag.getCompound(i)).ifPresent(entries::add);
        }
        return entries;
    }

    private static List<ActiveAftertasteEntry> readActiveAftertasteEntriesFromState(LivingEntity living) {
        List<FoodHistoryEntry> historyEntries = readHistoryEntries(living, getHistoryTag(living));
        List<FoodHistoryEntry> selectedFoods = selectAftertasteFoods(historyEntries);
        List<ActiveAftertasteEntry> visibleAftertastes = new ArrayList<>();
        EnumSet<FoodPassiveEffectType> seen = EnumSet.noneOf(FoodPassiveEffectType.class);

        for (FoodPassiveEffectType permanentAftertaste : getPermanentAftertastes(living)) {
            if (!seen.add(permanentAftertaste)) {
                continue;
            }

            visibleAftertastes.add(new ActiveAftertasteEntry(
                    permanentAftertaste,
                    FoodPassiveEffects.getRepresentativeStack(permanentAftertaste),
                    false,
                    getDisabledPermanentAftertastes(living).contains(permanentAftertaste)
            ));
        }

        for (FoodHistoryEntry selectedFood : selectedFoods) {
            for (FoodPassiveEffectType effectType : selectedFood.aftertasteTypes()) {
                if (!seen.add(effectType)) {
                    continue;
                }

                boolean disabled = selectedFood.disabledAftertastes().contains(effectType);
                boolean imminentExpiration = isAftertasteImminent(historyEntries, selectedFood, effectType);
                visibleAftertastes.add(new ActiveAftertasteEntry(
                        effectType,
                        selectedFood.sourceStack().copy(),
                        imminentExpiration,
                        disabled
                ));
            }
        }

        return visibleAftertastes;
    }

    private static EnumSet<FoodPassiveEffectType> getPermanentAftertastes(LivingEntity living) {
        return readEffectSet(living.getPersistentData().getList(PERMANENT_AFTERTASTES_TAG, Tag.TAG_STRING), true);
    }

    private static EnumSet<FoodPassiveEffectType> getDisabledPermanentAftertastes(LivingEntity living) {
        return readEffectSet(living.getPersistentData().getList(DISABLED_PERMANENT_AFTERTASTES_TAG, Tag.TAG_STRING), true);
    }

    private static EnumSet<FoodPassiveEffectType> readEffectSet(ListTag savedEffects, boolean aftertasteOnly) {
        EnumSet<FoodPassiveEffectType> permanent = EnumSet.noneOf(FoodPassiveEffectType.class);
        for (int i = 0; i < savedEffects.size(); i++) {
            try {
                FoodPassiveEffectType type = FoodPassiveEffectType.getByName(savedEffects.getString(i));
                if (!aftertasteOnly || type.isAftertaste()) {
                    permanent.add(type);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return permanent;
    }

    private static void setPermanentAftertastes(LivingEntity living, EnumSet<FoodPassiveEffectType> permanent) {
        if (permanent.isEmpty()) {
            living.getPersistentData().remove(PERMANENT_AFTERTASTES_TAG);
            return;
        }
        living.getPersistentData().put(PERMANENT_AFTERTASTES_TAG, writeEffectNames(permanent));
    }

    private static void setDisabledPermanentAftertastes(LivingEntity living, EnumSet<FoodPassiveEffectType> disabledPermanent) {
        if (disabledPermanent.isEmpty()) {
            living.getPersistentData().remove(DISABLED_PERMANENT_AFTERTASTES_TAG);
            return;
        }
        living.getPersistentData().put(DISABLED_PERMANENT_AFTERTASTES_TAG, writeEffectNames(disabledPermanent));
    }

    private static List<FoodHistoryEntry> readHistoryEntries(LivingEntity living, ListTag history) {
        List<FoodHistoryEntry> entries = new ArrayList<>(history.size());
        for (int i = 0; i < history.size(); i++) {
            CompoundTag entry = history.getCompound(i);
            ItemStack stack = readEntryStack(living, entry);
            entries.add(new FoodHistoryEntry(
                    i,
                    stack,
                    readPassiveEffects(living, entry),
                    readDisabledAftertastes(entry),
                    getFoodSignature(entry, stack)
            ));
        }
        return entries;
    }

    private static List<FoodHistoryEntry> selectAftertasteFoods(List<FoodHistoryEntry> historyEntries) {
        List<FoodHistoryEntry> selectedFoods = new ArrayList<>();
        Set<String> seenFoodSignatures = new HashSet<>();

        for (FoodHistoryEntry entry : historyEntries) {
            if (!entry.hasAnyAftertaste()) {
                continue;
            }

            if (!seenFoodSignatures.add(entry.foodSignature())) {
                continue;
            }

            selectedFoods.add(entry);
            if (selectedFoods.size() >= ACTIVE_AFTERTASTE_FOOD_LIMIT) {
                break;
            }
        }

        return selectedFoods;
    }

    private static boolean isAftertasteImminent(
            List<FoodHistoryEntry> historyEntries,
            FoodHistoryEntry sourceFood,
            FoodPassiveEffectType type
    ) {
        if (historyEntries.size() < FOOD_HISTORY_LIMIT) {
            return false;
        }

        List<FoodHistoryEntry> simulatedHistory = new ArrayList<>();
        simulatedHistory.add(FoodHistoryEntry.placeholder());
        for (int i = 0; i < Math.min(historyEntries.size(), FOOD_HISTORY_LIMIT - 1); i++) {
            simulatedHistory.add(historyEntries.get(i));
        }

        List<FoodHistoryEntry> simulatedSelectedFoods = selectAftertasteFoods(simulatedHistory);
        EnumSet<FoodPassiveEffectType> seen = EnumSet.noneOf(FoodPassiveEffectType.class);
        for (FoodHistoryEntry selectedFood : simulatedSelectedFoods) {
            for (FoodPassiveEffectType simulatedType : selectedFood.aftertasteTypes()) {
                if (!seen.add(simulatedType)) {
                    continue;
                }

                if (simulatedType == type && selectedFood.foodSignature().equals(sourceFood.foodSignature())) {
                    return false;
                }
            }
        }

        return true;
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

    private static EnumSet<FoodPassiveEffectType> readDisabledAftertastes(CompoundTag entry) {
        EnumSet<FoodPassiveEffectType> disabled = EnumSet.noneOf(FoodPassiveEffectType.class);
        ListTag savedEffects = entry.getList(FOOD_DISABLED_AFTERTASTES_TAG, Tag.TAG_STRING);
        for (int i = 0; i < savedEffects.size(); i++) {
            try {
                disabled.add(FoodPassiveEffectType.getByName(savedEffects.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return disabled;
    }

    private static ListTag writePassiveEffects(ItemStack stack) {
        ListTag listTag = new ListTag();
        List<FoodPassiveEffectComponent> passiveEffects = getPassiveEffectsFromStack(stack);
        for (FoodPassiveEffectComponent passiveEffect : passiveEffects) {
            listTag.add(StringTag.valueOf(passiveEffect.type().getSerializedName()));
        }
        return listTag;
    }

    private static ListTag writeEffectNames(Iterable<FoodPassiveEffectType> effects) {
        ListTag listTag = new ListTag();
        for (FoodPassiveEffectType effect : effects) {
            listTag.add(StringTag.valueOf(effect.getSerializedName()));
        }
        return listTag;
    }

    private static void setHistory(LivingEntity living, ListTag history) {
        if (history.isEmpty()) {
            living.getPersistentData().remove(FOOD_HISTORY_TAG);
        } else {
            living.getPersistentData().put(FOOD_HISTORY_TAG, history);
        }
    }

    private static ListTag getHistoryTag(LivingEntity living) {
        return living.getPersistentData().getList(FOOD_HISTORY_TAG, Tag.TAG_COMPOUND);
    }

    private static List<FoodPassiveEffectComponent> getPassiveEffectsFromStack(ItemStack stack) {
        return FoodPassiveEffects.get(stack);
    }

    private static String getFoodSignature(CompoundTag entry, ItemStack stack) {
        if (entry.contains(FOOD_STACK_TAG, Tag.TAG_COMPOUND)) {
            return entry.getCompound(FOOD_STACK_TAG).toString();
        }
        return stack.getItem().toString();
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
        tag.putBoolean("disabled", entry.disabled());
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
                    tag.getBoolean("imminent_expiration"),
                    tag.getBoolean("disabled")
            ));
        } catch (IllegalArgumentException ignored) {
            return java.util.Optional.empty();
        }
    }

    public record ActiveAftertasteEntry(
            FoodPassiveEffectType type,
            ItemStack sourceStack,
            boolean imminentExpiration,
            boolean disabled
    ) {
    }

    private record FoodHistoryEntry(
            int historyIndex,
            ItemStack sourceStack,
            List<FoodPassiveEffectType> passiveEffects,
            EnumSet<FoodPassiveEffectType> disabledAftertastes,
            String foodSignature
    ) {
        private static FoodHistoryEntry placeholder() {
            return new FoodHistoryEntry(-1, ItemStack.EMPTY, List.of(), EnumSet.noneOf(FoodPassiveEffectType.class), "__next_food__");
        }

        private List<FoodPassiveEffectType> aftertasteTypes() {
            return passiveEffects.stream().filter(FoodPassiveEffectType::isAftertaste).toList();
        }

        private boolean hasAnyAftertaste() {
            return passiveEffects.stream().anyMatch(FoodPassiveEffectType::isAftertaste);
        }
    }
}
