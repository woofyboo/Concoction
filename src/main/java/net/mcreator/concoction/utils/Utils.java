package net.mcreator.concoction.utils;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
}
