package com.myvisuals.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JumpCirclesModule extends Module {
    private final List circles = new ArrayList();
    private boolean wasOnGround = true;
    private int colorIndex = 1;
    private final String[] colorNames = {"Красный", "Фиолетовый", "Голубой", "Изумруд", "Золотой", "Радужный"};
    private final Color[] palette = {
            new Color(235, 65, 65), new Color(155, 75, 255), new Color(55, 195, 255),
            new Color(55, 230, 135), new Color(255, 195, 55), new Color(255, 80, 180)
    };

    public JumpCirclesModule() {
        super("JumpCircles", "Неоновые волны под ногами при прыжке", Category.RENDER);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            if (!isEnabled() || mc.player == null) return;
            renderCircles(ctx.matrixStack(), ctx.camera().getPos());
        });
    }

    public void nextColor() { colorIndex = (colorIndex + 1) % colorNames.length; }
    public String getCurrentColorName() { return colorNames[colorIndex]; }

    public Color getCurrentColor() {
        if (colorIndex == 5) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000.0f;
            return new Color(Color.HSBtoRGB(hue, 0.85f, 1.0f));
        }
        return palette[colorIndex];
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        boolean onGround = mc.player.isOnGround();
        if (wasOnGround && !onGround && Float.compare((float) mc.player.getVelocity().y, 0.1f) == 1) {
            circles.add(new double[]{mc.player.getX(), mc.player.getY() + 0.03, mc.player.getZ(), System.currentTimeMillis()});
        }
        wasOnGround = onGround;
    }

    private void renderCircles(MatrixStack matrices, Vec3d cam) {
        if (circles.isEmpty()) return;
        long now = System.currentTimeMillis();
        Color base = getCurrentColor();
        float r = base.getRed() / 255.0f, g = base.getGreen() / 255.0f, b = base.getBlue() / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Iterator it = circles.iterator();
        while (it.hasNext()) {
            double[] c = (double[]) it.next();
            float progress = (now - (long) c) / 1100.0f;
            if (Float.compare(progress, 1.0f) != -1) {
                it.remove();
                continue;
            }

            float ease = 1.0f - (1.0f - progress) * (1.0f - progress);
            float outerR = 0.25f + ease * 1.35f;
            float innerR = Math.max(0.05f, outerR - 0.22f);
            float alpha = (1.0f - progress) * 0.85f;

            matrices.push();
            matrices.translate(c - cam.x, c - cam.y, c - cam.z);
            Matrix4f mat = matrices.peek().getPositionMatrix();
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            // Градиентное неоновое кольцо
            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i != 65; i++) {
                double ang = 2.0 * Math.PI * i / 64.0;
                float cos = (float) Math.cos(ang), sin = (float) Math.sin(ang);
                buf.vertex(mat, cos * outerR, 0.0f, sin * outerR).color(r, g, b, alpha).next();
                buf.vertex(mat, cos * innerR, 0.0f, sin * innerR).color(r, g, b, 0.0f).next();
            }
            tess.draw();

            // Яркий белый блик по внешнему краю
            buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i != 65; i++) {
                double ang = 2.0 * Math.PI * i / 64.0;
                buf.vertex(mat, (float) Math.cos(ang) * outerR, 0.005f, (float) Math.sin(ang) * outerR).color(1.0f, 1.0f, 1.0f, alpha * 0.9f).next();
            }
            tess.draw();
            matrices.pop();
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
