package net.mcreator.concoction.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.entity.CinnamonChestBoatEntity;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class CinnamonChestBoatRenderer extends EntityRenderer<CinnamonChestBoatEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/entities/cinnamon_chest_boat.png");
	private final ChestBoatModel model;

	public CinnamonChestBoatRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.8F;
		this.model = new ChestBoatModel(context.bakeLayer(ModelLayers.createChestBoatModelName(Boat.Type.CHERRY)));
	}

	@Override
	public void render(CinnamonChestBoatEntity boat, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.375F, 0.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
		float hurtTime = (float) boat.getHurtTime() - partialTick;
		float damage = boat.getDamage() - partialTick;
		if (damage < 0.0F) {
			damage = 0.0F;
		}

		if (hurtTime > 0.0F) {
			poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) boat.getHurtDir()));
		}

		float bubbleAngle = boat.getBubbleAngle(partialTick);
		if (!Mth.equal(bubbleAngle, 0.0F)) {
			poseStack.mulPose(new Quaternionf().setAngleAxis(bubbleAngle * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
		}

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
		this.model.setupAnim(boat, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
		if (!boat.isUnderWater()) {
			VertexConsumer waterMask = buffer.getBuffer(RenderType.waterMask());
			this.model.waterPatch().render(poseStack, waterMask, packedLight, OverlayTexture.NO_OVERLAY);
		}

		poseStack.popPose();
		super.render(boat, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(CinnamonChestBoatEntity boat) {
		return TEXTURE;
	}
}
