package net.mcreator.concoction.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Random;

/**
 * Credits to squeek502 (AppleSkin) for the implementation reference!
 * https://www.curseforge.com/minecraft/mc-mods/appleskin
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
public class PhotosynthesisHungerOverlay
{
    public static int foodIconsOffset;
    private static final ResourceLocation MOD_ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/photosynthesis_icons.png");

    public static void init() {
        NeoForge.EVENT_BUS.register(new PhotosynthesisHungerOverlay());
    }

//    static ResourceLocation FOOD_LEVEL_ELEMENT = ResourceLocation.withDefaultNamespace("food_level");

    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RenderGuiLayerEvent.Post event) {
        if (event.getName().toString().equals("minecraft:food_level")) {
            Minecraft mc = Minecraft.getInstance();
            Gui gui = mc.gui;
            boolean isMounted = mc.player != null && mc.player.getVehicle() instanceof LivingEntity;
            if (!isMounted && !mc.options.hideGui && (mc.player != null && !mc.player.isCreative())) {
                renderNourishmentOverlay(gui, event.getGuiGraphics());
            }
        }
    }

    public static void renderNourishmentOverlay(Gui gui, GuiGraphics graphics) {
        foodIconsOffset = gui.rightHeight;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        FoodData stats = player.getFoodData();
        int top = minecraft.getWindow().getGuiScaledHeight() - foodIconsOffset + 10;
        int left = minecraft.getWindow().getGuiScaledWidth() / 2 + 91;

        boolean isPlayerHealingWithSaturation =
                player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
                        && player.isHurt()
                        && stats.getFoodLevel() >= 18;

        if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
            int dayTime = Math.floorMod(player.level().dayTime(), 24000);
            if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000)) &&
                    player.level().canSeeSky(player.blockPosition().above())) {
                drawPhotosynthesisOverlay(stats, minecraft, graphics, left, top, isPlayerHealingWithSaturation);
            }
        }
    }

    public static void drawPhotosynthesisOverlay(FoodData stats, Minecraft mc, GuiGraphics graphics, int left, int top, boolean naturalHealing) {
        float saturation = stats.getSaturationLevel();
        int foodLevel = stats.getFoodLevel();
        int ticks = mc.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871);

//		RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);
        RenderSystem.enableBlend();

        for (int j = 0; j < 10; ++j) {
            int x = left - j * 8 - 9;
            int y = top;

            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            // Background texture
            graphics.blit(MOD_ICONS_TEXTURE, x, y, 0, 0, 9, 9);

            float effectiveHungerOfBar = (stats.getFoodLevel()) / 2.0F - j;
            int naturalHealingOffset = naturalHealing ? 18 : 0;

            // Gilded hunger icons
            if (effectiveHungerOfBar >= 1)
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 18 + naturalHealingOffset, 0, 9, 9);
            else if (effectiveHungerOfBar >= .5)
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 9 + naturalHealingOffset, 0, 9, 9);
        }

        RenderSystem.disableBlend();
//		RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);
    }
}