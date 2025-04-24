package net.mcreator.concoction.utils;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class Utils {

    public static void addAchievement(ServerPlayer player, String achievement) {
        AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.parse(achievement));
        if (adv != null) {
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(adv);
            if (!_ap.isDone()) {
                for (String criteria : _ap.getRemainingCriteria())
                    player.getAdvancements().award(adv, criteria);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        float durabilityPercent = 1.0f - (float)stack.getDamageValue() / stack.getMaxDamage();
        if (durabilityPercent < 0.2f) {
            return 0x5E4E87;
        } else if (durabilityPercent < 0.36f) {
            return 0x847799;
        } else if (durabilityPercent < 0.52f) {
            return 0xB6ACAF;
        } else if (durabilityPercent < 0.68f) {
            return 0xD0C4B1;
        } else if (durabilityPercent < 0.84f) {
            return 0xDBC89E;
        } else {
            return 0xFFCB4C;
        }
    }
}
