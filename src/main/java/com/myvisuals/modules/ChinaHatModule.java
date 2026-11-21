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

public class ChinaHatModule extends Module {
    private int colorIndex = 0;
    private final String[] colorNames = {"Красный", "Фиолетовый", "Голубой", "Изумруд", "Золотой", "Радужный"};
    private final Color[] palette = {
            new Color(235, 65, 65, 190),
            new Color(155, 75, 255, 190),
            new Color(55, 195, 255, 190),
            new Color(55, 230, 135, 190),
            new Color(255, 195, 55, 190),
            new Color(255, 80, 180, 190)
    };

    public ChinaHatModule() {
        super("ChinaHat", "Неоновая конусная шляпа над головой", Category.RENDER);

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (!isEnabled() || mc.player == null) return;
            if (mc.options.getPerspective().isFirstPerson()) return;
            renderHat(context.matrixStack(), context.camera().getPos(), context.tickDelta());
        });
    }

    public void nextColor() {
        colorIndex = (colorIndex + 1) % colorNames.length;
    }

    public String getCurrentColorName() {
        return colorNames[colorIndex];
    }

    public Color getCurrentColor() {
        if (colorIndex == 5) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000.0f;
            int rgb = Color.HSBtoRGB(hue, 0.85f, 1.0f);
            Color base = new Color(rgb);
            return new Color(base.getRed(), base.getGreen(), base.getBlue(), 195);
        }
        return palette[colorIndex];
    }

    private void renderHat(MatrixStack matrices, Vec3d camPos, float tickDelta) {
        if (mc.player == null) return;

        double x = mc.player.prevX + (mc.player.getX() - mc.player.prevX) * tickDelta - camPos.x;
        double y = mc.player.prevY + (mc.player.getY() - mc.player.prevY) * tickDelta - camPos.y + mc.player.getHeight() + 0.05;
        double z = mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * tickDelta - camPos.z;

        if (mc.player.isSneaking()) {
            y -= 0.15;
        }

        Color c = getCurrentColor();
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;
        float a = c.getAlpha() / 255.0f;

        matrices.push();
        matrices.translate(x, y, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f mat = matrices.peek().getPositionMatrix();

        float radius = 0.62f;
        float height = 0.34f;
        int segments = 48;

        // Конус шляпы
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(mat, 0.0f, height, 0.0f).color(r, g, b, Math.min(1.0f, a + 0.2f)).next();
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float px = (float) (Math.cos(angle) * radius);
            float pz = (float) (Math.sin(angle) * radius);
            buffer.vertex(mat, px, 0.0f, pz).color(r, g, b, a * 0.65f).next();
        }
        tessellator.draw();

        // Яркий светящийся ободок по краю шляпы
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float px = (float) (Math.cos(angle) * radius);
            float pz = (float) (Math.sin(angle) * radius);
            buffer.vertex(mat, px, 0.0f, pz).color(1.0f, 1.0f, 1.0f, 0.9f).next();
        }
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }
}
