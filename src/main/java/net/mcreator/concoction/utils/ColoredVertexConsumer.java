package net.mcreator.concoction.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class ColoredVertexConsumer implements VertexConsumer {
    private final VertexConsumer parent;
    private final float r, g, b;

    public ColoredVertexConsumer(VertexConsumer parent, float r, float g, float b) {
        this.parent = parent;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        // Передаем координаты без изменений
        parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        // Применяем красный тон к исходному цвету
        int newR = (int) (r * this.r);
        int newG = (int) (g * this.g);
        int newB = (int) (b * this.b);

        // Ограничиваем значения 0-255
        newR = Math.min(255, Math.max(0, newR));
        newG = Math.min(255, Math.max(0, newG));
        newB = Math.min(255, Math.max(0, newB));

        return parent.setColor(newR, newG, newB, a);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        // Передаем UV-координаты без изменений
        parent.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        // Передаем UV1 без изменений
        parent.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        // Передаем UV2 без изменений
        parent.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        // Передаем нормали без изменений
        parent.setNormal(x, y, z);
        return this;
    }
}