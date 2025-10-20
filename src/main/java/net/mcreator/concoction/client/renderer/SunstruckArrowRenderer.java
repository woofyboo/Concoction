package net.mcreator.concoction.client.renderer;

import net.mcreator.concoction.entity.SunstruckArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SunstruckArrowRenderer extends ArrowRenderer<SunstruckArrowEntity> {

    // Путь держи в стиле vanilla: textures/entity/projectiles/...
    private static final ResourceLocation TEXTURE =
            ResourceLocation.parse("concoction:textures/entities/sunstruck_arrow.png");

    public SunstruckArrowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx); // всё остальное — ровно как у ванили (модель/позы/шейпы)
    }

    @Override
    public ResourceLocation getTextureLocation(SunstruckArrowEntity entity) {
        return TEXTURE;
    }
}
