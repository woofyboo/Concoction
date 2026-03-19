package net.mcreator.concoction.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.utils.ColoredVertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(value = Dist.CLIENT)
public class PlayerClientHandler {
    private static final int RENDER_DELAY_TICKS = 15;

    // Avoid abrupt spicy overlay flicker when the player exits swimming or flight poses.
    private static final Map<UUID, Long> playerSpecialStateExitTimes = new HashMap<>();
    private static final Map<UUID, Boolean> shouldRenderSpicyEffect = new HashMap<>();

    @SubscribeEvent
    public static void playerTickClient(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        boolean currentlyInSpecialState = player.isFallFlying()
                || player.isSwimming()
                || player.getPose() == Pose.SWIMMING;
        boolean wasInSpecial = player.getPersistentData().getBoolean("was_in_special_state_client");

        if (!currentlyInSpecialState && wasInSpecial) {
            playerSpecialStateExitTimes.put(playerUUID, player.level().getGameTime());
        }

        player.getPersistentData().putBoolean("was_in_special_state_client", currentlyInSpecialState);

        if (player.hasEffect(ConcoctionModMobEffects.SPICY)) {
            shouldRenderSpicyEffect.put(playerUUID, !isPlayerInSpecialStateWithDelay(player));
        } else {
            shouldRenderSpicyEffect.remove(playerUUID);
        }
    }

    private static boolean isPlayerInSpecialStateWithDelay(Player player) {
        UUID playerUUID = player.getUUID();
        boolean currently = player.isFallFlying()
                || player.isSwimming()
                || player.getPose() == Pose.SWIMMING;

        if (!currently) {
            Long exitTime = playerSpecialStateExitTimes.get(playerUUID);
            if (exitTime != null) {
                long currentTime = player.level().getGameTime();
                if (currentTime - exitTime < RENDER_DELAY_TICKS) {
                    return true;
                }
                playerSpecialStateExitTimes.remove(playerUUID);
            }
        }

        return currently;
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        if (player.hasEffect(ConcoctionModMobEffects.SPICY)
                && shouldRenderSpicyEffect.getOrDefault(playerUUID, false)) {

            PlayerRenderer playerRenderer = event.getRenderer();
            @SuppressWarnings("unchecked")
            PlayerModel<AbstractClientPlayer> model =
                    (PlayerModel<AbstractClientPlayer>) playerRenderer.getModel();

            boolean oldHeadVisible = model.head.visible;
            boolean oldHatVisible = model.hat.visible;

            model.head.visible = false;
            model.hat.visible = false;

            player.getPersistentData().putBoolean("spicy_head_visible", oldHeadVisible);
            player.getPersistentData().putBoolean("spicy_hat_visible", oldHatVisible);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        if (!player.hasEffect(ConcoctionModMobEffects.SPICY)
                || !shouldRenderSpicyEffect.getOrDefault(playerUUID, false)) {
            return;
        }

        PlayerRenderer playerRenderer = event.getRenderer();
        @SuppressWarnings("unchecked")
        PlayerModel<AbstractClientPlayer> model =
                (PlayerModel<AbstractClientPlayer>) playerRenderer.getModel();

        boolean oldHeadVisible = player.getPersistentData().getBoolean("spicy_head_visible");
        boolean oldHatVisible = player.getPersistentData().getBoolean("spicy_hat_visible");

        model.head.visible = oldHeadVisible;
        model.hat.visible = oldHatVisible;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();

        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0, -1.4, 0);

        AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
        var vertexConsumer = bufferSource.getBuffer(
                RenderType.entityTranslucent(playerRenderer.getTextureLocation(clientPlayer))
        );

        float partialTicks = event.getPartialTick();
        float netHeadYaw = partialTicks == 0
                ? player.yHeadRot
                : Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        float headPitch = partialTicks == 0
                ? player.getXRot()
                : Mth.lerp(partialTicks, player.xRotO, player.getXRot());

        model.setupAnim(clientPlayer, 0, 0, partialTicks, netHeadYaw, headPitch);

        float time = (player.level().getGameTime() + player.getId()) % 50;
        float pulse = 0.3f + 0.4f *
                (0.5f * (1.0f + Mth.sin((float) (time / 50.0f * 2 * Math.PI))));

        var coloredConsumer = new ColoredVertexConsumer(vertexConsumer, 1.0f, pulse, pulse);

        model.head.render(poseStack, coloredConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        model.hat.render(poseStack, coloredConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
