package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber
public class CreamyBoostHandler {

    private static final String NBT_TAG = "concoction_creamy_boosted";
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);

    private static void boostAllActive(LivingEntity entity, int creamyBonus) {
        if (REENTRY.get()) return;
        for (MobEffectInstance inst : entity.getActiveEffects()) {
            if (inst.getEffect() == ConcoctionModMobEffects.CREAMY) continue;
            maybeBoost(entity, inst, creamyBonus);
        }
    }

    private static void maybeBoost(LivingEntity entity, MobEffectInstance inst, int creamyBonus) {
        if (REENTRY.get()) return;
        if (!entity.hasEffect(ConcoctionModMobEffects.CREAMY)) return;

        String id = effectKeyString(inst);
        Set<String> boosted = getBoostedSet(entity);
        if (boosted.contains(id)) return; // уже усилен — ждём, пока спадёт

        int newAmp = inst.getAmplifier() + creamyBonus;
        MobEffectInstance upgraded = new MobEffectInstance(
                inst.getEffect(),
                inst.getDuration(),
                newAmp,
                inst.isAmbient(),
                inst.isVisible(),
                inst.showIcon(),
                null
        );

        boosted.add(id);
        setBoostedSet(entity, boosted);

        REENTRY.set(true);
        try {
            entity.removeEffect(inst.getEffect());
            entity.addEffect(upgraded);
        } finally {
            REENTRY.set(false);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance inst = event.getEffectInstance();

        if (inst.getEffect() == ConcoctionModMobEffects.CREAMY) {
            int bonus = inst.getAmplifier() + 1;
            boostAllActive(entity, bonus);
            return;
        }

        MobEffectInstance creamy = entity.getEffect(ConcoctionModMobEffects.CREAMY);
        if (creamy != null) {
            int bonus = creamy.getAmplifier() + 1;
            maybeBoost(entity, inst, bonus);
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) { // <= Remove, не Removed
        LivingEntity entity = event.getEntity();
        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;

        String id = effectKeyString(inst);
        Set<String> boosted = getBoostedSet(entity);
        if (boosted.remove(id)) {
            setBoostedSet(entity, boosted);
        }
    }

    // ---------- helpers ----------

    private static String effectKeyString(MobEffectInstance inst) {
        // Пытаемся получить реестр-ключ; если не удалось — падёмся на descriptionId (стабильный для vanilla)
        ResourceLocation loc = inst.getEffect().unwrapKey().map(k -> k.location()).orElse(null);
        return (loc != null) ? loc.toString() : inst.getDescriptionId();
    }

    private static Set<String> getBoostedSet(LivingEntity e) {
        var tag = e.getPersistentData();
        ListTag list = tag.getList(NBT_TAG, 8); // 8 = StringTag
        Set<String> s = new HashSet<>();
        for (int i = 0; i < list.size(); i++) s.add(list.getString(i));
        return s;
    }

    private static void setBoostedSet(LivingEntity e, Set<String> set) {
        ListTag list = new ListTag();
        for (String str : set) list.add(StringTag.valueOf(str));
        e.getPersistentData().put(NBT_TAG, list);
    }
}
