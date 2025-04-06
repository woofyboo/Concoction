
package net.mcreator.concoction.client.renderer;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.mcreator.concoction.entity.SunstruckEntity;

public class SunstruckRenderer extends HumanoidMobRenderer<SunstruckEntity, HumanoidModel<SunstruckEntity>> {
	public SunstruckRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieModel<SunstruckEntity>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(SunstruckEntity entity) {
		return ResourceLocation.parse("concoction:textures/entities/sunstruck.png");
	}
}
