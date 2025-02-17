package net.mcreator.concoction.client.gui;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


/**
 * Credits to squeek502 (AppleSkin) for the implementation reference!
 * <a href="https://www.curseforge.com/minecraft/mc-mods/appleskin">...</a>
 */

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionHUDOverlays
{
    public static int healthIconsOffset;
    public static int foodIconsOffset;
    private static final ResourceLocation MOD_ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/concoction_gui_icons.png");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        ConcoctionHUDOverlays.register(event);
    }

//    @SubscribeEvent
//    public static void onRenderGuiOverlayPre(RenderGuiLayerEvent.Pre event) {
//        if (event.getName().toString().equals("appleskin:saturation_level")) {
//            Minecraft mc = Minecraft.getInstance();
//            if (mc.player != null && mc.player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
//                event.setCanceled(true);
//            }
//        }
//    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerBelow(
                VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "health_overlay"),
                (guiGraphics, deltaTracker) -> healthIconsOffset = Minecraft.getInstance().gui.leftHeight
        );
        event.registerBelow(
                VanillaGuiLayers.FOOD_LEVEL,
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "food_overlay"),
                (guiGraphics, deltaTracker) -> foodIconsOffset = Minecraft.getInstance().gui.rightHeight
        );

//        event.registerBelow(VanillaGuiLayers.FOOD_LEVEL,
//                ResourceLocation.fromNamespaceAndPath("appleskin", "saturation_level"),
//                (guiGraphics, deltaTracker) -> foodIconsOffset = Minecraft.getInstance().gui.rightHeight
//        );

        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, SpicyHeartsOverlay.ID, new SpicyHeartsOverlay());
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, PhotosynthesisOverlay.ID, new PhotosynthesisOverlay());
    }

    public static abstract class BaseOverlay implements LayeredDraw.Layer
    {
        public abstract void render(Minecraft mc, Player player, GuiGraphics guiGraphics, int left, int right, int top, int guiTicks);

        @Override
        public final void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || !shouldRenderOverlay(minecraft, minecraft.player, guiGraphics, minecraft.gui.getGuiTicks()))
                return;

            int top = guiGraphics.guiHeight();
            int left = guiGraphics.guiWidth() / 2 - 91; // left of health bar
            int right = guiGraphics.guiWidth() / 2 + 91; // right of food bar

            render(minecraft, minecraft.player, guiGraphics, left, right, top, minecraft.gui.getGuiTicks());
        }

        public boolean shouldRenderOverlay(Minecraft minecraft, Player player, GuiGraphics guiGraphics, int guiTicks) {
            return !minecraft.options.hideGui && minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer();
        }
    }

    public static class PhotosynthesisOverlay extends BaseOverlay
    {
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "photosynthesis");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics, int left, int right, int top, int guiTicks) {
            FoodData stats = player.getFoodData();

            boolean isPlayerHealingWithSaturation =
                    player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
                            && player.isHurt()
                            && stats.getFoodLevel() >= 18;

            if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                int dayTime = Math.floorMod(player.level().dayTime(), 24000);
                if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000)) &&
                        player.level().canSeeSky(player.blockPosition().above()))
                            drawPhotosynthesisOverlay(stats, minecraft, guiGraphics, right, top - foodIconsOffset, isPlayerHealingWithSaturation);
            }
        }

        @Override
        public boolean shouldRenderOverlay(Minecraft mc, Player player, GuiGraphics guiGraphics, int guiTicks) {
            if (!super.shouldRenderOverlay(mc, player, guiGraphics, guiTicks))
                return false;

            return true;
        }
    }

    public static class SpicyHeartsOverlay extends BaseOverlay
    {
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "spicy_hearts");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics, int left, int right, int top, int guiTicks) {
            if (player.hasEffect(ConcoctionModMobEffects.SPICY)) {

                drawSpicyHeartsOverlay(player, minecraft, guiGraphics, left, top - healthIconsOffset);
            }
        }

        @Override
        public boolean shouldRenderOverlay(Minecraft mc, Player player, GuiGraphics guiGraphics, int guiTicks) {
            if (!super.shouldRenderOverlay(mc, player, guiGraphics, guiTicks))
                return false;

            return true;
        }
    }

    public static void drawPhotosynthesisOverlay(FoodData foodData, Minecraft minecraft, GuiGraphics graphics, int right, int top, boolean naturalHealing) {
        float saturation = foodData.getSaturationLevel();
        int foodLevel = foodData.getFoodLevel();
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871);

        RenderSystem.enableBlend();

        for (int j = 0; j < 10; ++j) {
            int x = right - j * 8 - 9;
            int y = top;

            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            // Background texture
            graphics.blit(MOD_ICONS_TEXTURE, x, y, 0, 0, 9, 9);

            float effectiveHungerOfBar = (foodData.getFoodLevel()) / 2.0F - j;
//            int naturalHealingOffset = naturalHealing ? 18 : 0;

            // Gilded hunger icons
            if (effectiveHungerOfBar >= 1)
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 18, 0, 9, 9);
            else if (effectiveHungerOfBar >= .5)
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 9, 0, 9, 9);
        }

        RenderSystem.disableBlend();
    }

    public static void drawSpicyHeartsOverlay(Player player, Minecraft minecraft, GuiGraphics graphics, int xBasePos, int yBasePos) {
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed((long) (ticks * 312871));

        int health = Mth.ceil(player.getHealth());
        float absorption = Mth.ceil(player.getAbsorptionAmount());
        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float) attrMaxHealth.getValue();

        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) regen = ticks % 25;

        int healthRows = Mth.ceil((double) healthMax / 2.0);
        int absorptionRows = Mth.ceil((double) absorption / 2.0);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int totalRows = healthRows * 2;

        for (int i = healthRows + absorptionRows - 1; i >= 0; i--) {
            int row = i / 10;
            int col = i % 10;
            int x = xBasePos + col * 8;
            int y = yBasePos - row * rowHeight;
            if (health + absorption <= 4) {
                y += rand.nextInt(2);
            }

            if (i < healthRows && i == regen) {
                y -= 2;
            }
            boolean isBlinking = (player.invulnerableTime != 0) && ticks % 6 < 3;
            renderHeart(graphics, HeartType.CONTAINER, x, y, false, isBlinking);
            int heartIndex = i * 2;

            if (isBlinking && heartIndex < healthMax) {
                boolean isHalfHealth = heartIndex + 1 == healthMax;
                renderHeart(graphics, HeartType.NORMAL, x, y, isHalfHealth, true);
            }

            if (heartIndex < health) {
                boolean isHalfHealth = heartIndex + 1 == health;
                renderHeart(graphics, HeartType.NORMAL, x, y, isHalfHealth, false);
            }
        }
    }

    private static void renderHeart(
            GuiGraphics guiGraphics, HeartType heartType, int x, int y, boolean isHalf, boolean isBlinking
    ) {
        RenderSystem.enableBlend();
        guiGraphics.blitSprite(heartType.getSprite(isHalf, isBlinking), x, y, 9, 9);
        RenderSystem.disableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    public static enum HeartType implements net.neoforged.fml.common.asm.enumextension.IExtensibleEnum {
        CONTAINER(
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/container"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/container_blinking"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/container"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/container_blinking")
        ),
        NORMAL(
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/full"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/full_blinking"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/half"),
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "hud/spicyheart/half_blinking")
        );

        private final ResourceLocation full;
        private final ResourceLocation fullBlinking;
        private final ResourceLocation half;
        private final ResourceLocation halfBlinking;

        private HeartType(
                ResourceLocation full,
                ResourceLocation fullBlinking,
                ResourceLocation half,
                ResourceLocation halfBlinking
        ) {
            this.full = full;
            this.fullBlinking = fullBlinking;
            this.half = half;
            this.halfBlinking = halfBlinking;
        }

        public ResourceLocation getSprite(boolean isHalf, boolean isBlinking) {
                if (isHalf) {
                    return isBlinking ? this.halfBlinking : this.half;
                } else {
                    return isBlinking ? this.fullBlinking : this.full;
                }
            }
        }
    }

