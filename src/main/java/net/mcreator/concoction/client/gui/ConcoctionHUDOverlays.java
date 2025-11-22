package net.mcreator.concoction.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionHUDOverlays {
    public static int healthIconsOffset;
    public static int foodIconsOffset;
    private static final ResourceLocation MOD_ICONS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/concoction_gui_icons.png");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        ConcoctionHUDOverlays.register(event);
    }

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

        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, SpicyHeartsOverlay.ID, new SpicyHeartsOverlay());
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, PhotosynthesisOverlay.ID, new PhotosynthesisOverlay());
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, SunstruckHeartsOverlay.ID, new SunstruckHeartsOverlay());
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, WeepingHeartsOverlay.ID, new WeepingHeartsOverlay());
    }

    public static abstract class BaseOverlay implements LayeredDraw.Layer {
        public abstract void render(Minecraft mc, Player player, GuiGraphics guiGraphics,
                                    int left, int right, int top, int guiTicks);

        @Override
        public final void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null
                    || !shouldRenderOverlay(minecraft, minecraft.player, guiGraphics, minecraft.gui.getGuiTicks())) {
                return;
            }

            int top = guiGraphics.guiHeight();
            int left = guiGraphics.guiWidth() / 2 - 91;
            int right = guiGraphics.guiWidth() / 2 + 91;

            render(minecraft, minecraft.player, guiGraphics, left, right, top, minecraft.gui.getGuiTicks());
        }

        public boolean shouldRenderOverlay(Minecraft minecraft, Player player,
                                           GuiGraphics guiGraphics, int guiTicks) {
            return !minecraft.options.hideGui
                    && minecraft.gameMode != null
                    && minecraft.gameMode.canHurtPlayer();
        }
    }

    public static class PhotosynthesisOverlay extends BaseOverlay {
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "photosynthesis");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics,
                           int left, int right, int top, int guiTicks) {
            FoodData stats = player.getFoodData();

            boolean isPlayerHealingWithSaturation =
                    player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
                            && player.isHurt()
                            && stats.getFoodLevel() >= 18;

            if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                int dayTime = Math.floorMod(player.level().dayTime(), 24000);
                if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000))
                        && player.level().canSeeSky(player.blockPosition().above())) {
                    drawPhotosynthesisOverlay(
                            stats,
                            minecraft,
                            guiGraphics,
                            right,
                            top - foodIconsOffset,
                            isPlayerHealingWithSaturation
                    );
                }
            }
        }
    }

    public static class SpicyHeartsOverlay extends BaseOverlay {
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "spicy_hearts");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics,
                           int left, int right, int top, int guiTicks) {
            if (player.hasEffect(ConcoctionModMobEffects.SPICY)) {
                drawCustomHeartsOverlay(
                        player,
                        minecraft,
                        guiGraphics,
                        left,
                        top - healthIconsOffset,
                        HeartType.CONTAINER_SPICY,
                        HeartType.SPICY,
                        HeartType.SPICY_ABSORB
                );
            }
        }
    }

    public static class SunstruckHeartsOverlay extends BaseOverlay {
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_hearts");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics,
                           int left, int right, int top, int guiTicks) {
            if (player.hasEffect(ConcoctionModMobEffects.SUNSTRUCK_EFFECT)) {
                drawCustomHeartsOverlay(
                        player,
                        minecraft,
                        guiGraphics,
                        left,
                        top - healthIconsOffset,
                        HeartType.CONTAINER_SUNSTRUCK,
                        HeartType.SUNSTRUCK,
                        HeartType.SUNSTRUCK_ABSORB
                );
            }
        }
    }

    public static class WeepingHeartsOverlay extends BaseOverlay {
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "weeping_hearts");

        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics,
                           int left, int right, int top, int guiTicks) {
            if (player.hasEffect(ConcoctionModMobEffects.WEEPING)) {
                // без контейнера, только цветные сердца
                drawCustomHeartsOverlay(
                        player,
                        minecraft,
                        guiGraphics,
                        left,
                        top - healthIconsOffset,
                        null,
                        HeartType.WEEPING,
                        HeartType.WEEPING_ABSORB
                );
            }
        }
    }

    public static void drawPhotosynthesisOverlay(FoodData foodData, Minecraft minecraft,
                                                 GuiGraphics graphics, int right, int top,
                                                 boolean naturalHealing) {
        float saturation = foodData.getSaturationLevel();
        int foodLevel = foodData.getFoodLevel();
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random(ticks * 312871);

        RenderSystem.enableBlend();

        for (int j = 0; j < 10; ++j) {
            int x = right - j * 8 - 9;
            int y = top;

            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            graphics.blit(MOD_ICONS_TEXTURE, x, y, 0, 0, 9, 9);

            float effectiveHungerOfBar = (foodLevel / 2.0F) - j;
            if (effectiveHungerOfBar >= 1) {
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 18, 0, 9, 9);
            } else if (effectiveHungerOfBar >= .5F) {
                graphics.blit(MOD_ICONS_TEXTURE, x, y, 9, 0, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }

    public static void drawCustomHeartsOverlay(Player player, Minecraft minecraft, GuiGraphics graphics,
                                               int xBasePos, int yBasePos,
                                               HeartType container,
                                               HeartType regularFill,
                                               HeartType absorptionFill) {
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random(ticks * 312871L);

        int health = Mth.ceil(player.getHealth());
        float absorption = player.getAbsorptionAmount();
        float healthMax = (float) player
                .getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
                .getValue();

        int regenIndex = player.hasEffect(MobEffects.REGENERATION)
                ? ticks % Mth.ceil(healthMax)
                : -1;

        int totalHearts = Mth.ceil(healthMax / 2.0F);
        int absorptionHearts = Mth.ceil(absorption / 2.0F);
        int rowCount = Mth.ceil((totalHearts + absorptionHearts) / 10.0F);
        int rowHeight = Math.max(10 - (rowCount - 2), 3);

        // флаг "игрок недавно получил урон"
        boolean isDamaged = player.invulnerableTime > 0 && (ticks % 6 < 3);

        for (int i = 0; i < totalHearts + absorptionHearts; ++i) {
            int row = i / 10;
            int col = i % 10;
            int x = xBasePos + col * 8;
            int y = yBasePos - row * rowHeight;

            if ((health + absorption) <= 4 && row == 0) {
                y += rand.nextInt(2);
            }

            boolean isAbsorption = i >= totalHearts;
            int heartIndex = i * 2;

            float healthLeft = 0.0F;
            float absorptionLeft = 0.0F;

            if (isAbsorption) {
                absorptionLeft = absorption - (i - totalHearts) * 2;
            } else {
                healthLeft = health - heartIndex;
            }

            // сколько "реального" хп в этом слоте (учитываем и абсорб)
            float effectiveLeft = isAbsorption ? absorptionLeft : healthLeft;

            // надо ли МИГАТЬ этим сердцем: урон недавно был + в этом слоте вообще есть хп
            boolean blinkThisHeart = isDamaged && effectiveLeft > 0.0F;

            // контейнер (фоновый контур)
            if (container != null) {
                boolean hasContent = effectiveLeft > 0.0F;
                boolean blinkContainer = blinkThisHeart && hasContent;
                renderHeart(graphics, container, x, y, false, blinkContainer);
            }

            // подъём сердечка при регенерации
            if (!isAbsorption && heartIndex / 2 == regenIndex) {
                y -= 2;
            }

            // выбор спрайта ДЛЯ ЭТОГО сердца: либо мигающий, либо обычный
            if (isAbsorption) {
                if (absorptionLeft >= 2.0F) {
                    // полное сердце абсорба
                    renderHeart(graphics, absorptionFill, x, y, false, blinkThisHeart);
                } else if (absorptionLeft > 0.0F) {
                    // половинка абсорба
                    renderHeart(graphics, absorptionFill, x, y, true, blinkThisHeart);
                }
            } else {
                if (healthLeft >= 2.0F) {
                    // полное сердце хп
                    renderHeart(graphics, regularFill, x, y, false, blinkThisHeart);
                } else if (healthLeft == 1.0F) {
                    // половинка хп
                    renderHeart(graphics, regularFill, x, y, true, blinkThisHeart);
                }
            }
        }
    }





    private static void renderHeart(GuiGraphics guiGraphics, HeartType heartType,
                                    int x, int y, boolean isHalf, boolean isBlinking) {
        RenderSystem.enableBlend();
        guiGraphics.blitSprite(heartType.getSprite(isHalf, isBlinking), x, y, 9, 9);
        RenderSystem.disableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    public enum HeartType implements net.neoforged.fml.common.asm.enumextension.IExtensibleEnum {
        CONTAINER_SPICY(
                sprite("hud/spicyheart/container"),
                sprite("hud/spicyheart/container_blinking"),
                sprite("hud/spicyheart/container"),
                sprite("hud/spicyheart/container_blinking")
        ),
        SPICY(
                sprite("hud/spicyheart/full"),
                sprite("hud/spicyheart/full_blinking"),
                sprite("hud/spicyheart/half"),
                sprite("hud/spicyheart/half_blinking")
        ),
        SPICY_ABSORB(
                sprite("hud/spicyheart/absorb_full"),
                sprite("hud/spicyheart/absorb_full"),
                sprite("hud/spicyheart/absorb_half"),
                sprite("hud/spicyheart/absorb_half")
        ),
        CONTAINER_SUNSTRUCK(
                sprite("hud/sunstruckheart/container"),
                sprite("hud/sunstruckheart/container_blinking"),
                sprite("hud/sunstruckheart/container"),
                sprite("hud/sunstruckheart/container_blinking")
        ),
        SUNSTRUCK(
                sprite("hud/sunstruckheart/full"),
                sprite("hud/sunstruckheart/full_blinking"),
                sprite("hud/sunstruckheart/half"),
                sprite("hud/sunstruckheart/half_blinking")
        ),
        SUNSTRUCK_ABSORB(
                sprite("hud/sunstruckheart/absorb_full"),
                sprite("hud/sunstruckheart/absorb_full"),
                sprite("hud/sunstruckheart/absorb_half"),
                sprite("hud/sunstruckheart/absorb_half")
        ),
        CONTAINER_WEEPING(
                sprite("hud/weepingheart/full"),
                sprite("hud/weepingheart/full_blinking"),
                sprite("hud/weepingheart/half"),
                sprite("hud/weepingheart/half_blinking")
        ),
        WEEPING(
                sprite("hud/weepingheart/full"),
                sprite("hud/weepingheart/full_blinking"),
                sprite("hud/weepingheart/half"),
                sprite("hud/weepingheart/half_blinking")
        ),
        WEEPING_ABSORB(
                sprite("hud/weepingheart/absorb_full"),
                sprite("hud/weepingheart/absorb_full"),
                sprite("hud/weepingheart/absorb_half"),
                sprite("hud/weepingheart/absorb_half")
        );

        private final ResourceLocation full;
        private final ResourceLocation fullBlinking;
        private final ResourceLocation half;
        private final ResourceLocation halfBlinking;

        HeartType(ResourceLocation full, ResourceLocation fullBlinking,
                  ResourceLocation half, ResourceLocation halfBlinking) {
            this.full = full;
            this.fullBlinking = fullBlinking;
            this.half = half;
            this.halfBlinking = halfBlinking;
        }

        public ResourceLocation getSprite(boolean isHalf, boolean isBlinking) {
            return isHalf
                    ? (isBlinking ? halfBlinking : half)
                    : (isBlinking ? fullBlinking : full);
        }

        private static ResourceLocation sprite(String path) {
            return ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, path);
        }
    }
}
