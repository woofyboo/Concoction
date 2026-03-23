package net.mcreator.concoction.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class AlphaVertexConsumer implements VertexConsumer {
    private final VertexConsumer parent;
    private final float alphaMultiplier;

    public AlphaVertexConsumer(VertexConsumer parent, float alphaMultiplier) {
        this.parent = parent;
        this.alphaMultiplier = alphaMultiplier;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        int newAlpha = Math.min(255, Math.max(0, (int) (a * this.alphaMultiplier)));
        return parent.setColor(r, g, b, newAlpha);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        parent.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        parent.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        parent.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        parent.setNormal(x, y, z);
        return this;
    }
}
