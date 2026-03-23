package net.mcreator.concoction.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcreator.concoction.ConcoctionMod;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FoodTooltipClientSettings {
    private static final String DETAILED_VIEW_KEY = "detailedFoodEffectView";
    private static final Path SETTINGS_PATH = FMLPaths.CONFIGDIR.get().resolve("concoction-food-tooltips.json");

    private static boolean detailedView = false;

    private FoodTooltipClientSettings() {
    }

    public static boolean isDetailedView() {
        return detailedView;
    }

    public static void load() {
        if (!Files.exists(SETTINGS_PATH)) {
            save();
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(Files.readString(SETTINGS_PATH)).getAsJsonObject();
            if (json.has(DETAILED_VIEW_KEY)) {
                detailedView = json.get(DETAILED_VIEW_KEY).getAsBoolean();
            }
        } catch (IOException | RuntimeException exception) {
            ConcoctionMod.LOGGER.warn("Failed to load food tooltip client settings from {}", SETTINGS_PATH, exception);
        }
    }

    public static void toggleDetailedView() {
        detailedView = !detailedView;
        save();
    }

    private static void save() {
        JsonObject json = new JsonObject();
        json.addProperty(DETAILED_VIEW_KEY, detailedView);

        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            Files.writeString(SETTINGS_PATH, json.toString());
        } catch (IOException exception) {
            ConcoctionMod.LOGGER.warn("Failed to save food tooltip client settings to {}", SETTINGS_PATH, exception);
        }
    }
}
