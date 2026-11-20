package com.myvisuals.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

public class ChinaHatModule extends Module {
    public ChinaHatModule() {
        super("ChinaHat", "Неоновая шляпа над головой", Category.RENDER);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRender);
    }

    private void onRender(WorldRenderContext context) {
        if (!isEnabled() || mc.player == null) return;
        // Показываем шляпу от 3-го лица (F5)
        if (mc.options.getPerspective().isFirstPerson()) return;

        float tickDelta = context.tickDelta();
        Vec3d cam = context.camera().getPos();

        double x = MathHelper.lerp(tickDelta, mc.player.prevX, mc.player.getX()) - cam.x;
        double y = MathHelper.lerp(tickDelta, mc.player.prevY, mc.player.getY()) - cam.y + mc.player.getHeight() + 0.05;
        double z = MathHelper.lerp(tickDelta, mc.player.prevZ, mc.player.getZ()) - cam.z;

        if (mc.player.isInSneakingPose()) {
            y -= 0.2;
        }

        MatrixStack matrices = context.matrixStack();
        matrices.push();
        matrices.translate(x, y, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float radius = 0.55f;
        float height = 0.28f;
        int segments = 48;

        // Рисуем сам конус шляпы с переливом цвета
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Color topColor = getRainbowColor(0);
        buffer.vertex(matrix, 0, height, 0)
                .color(topColor.getRed(), topColor.getGreen(), topColor.getBlue(), 190).next();

        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float cos = (float) Math.cos(angle) * radius;
            float sin = (float) Math.sin(angle) * radius;
            Color edgeColor = getRainbowColor(i * 15);

            buffer.vertex(matrix, cos, 0, sin)
                    .color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 150).next();
        }
        tessellator.draw();

        // Светящийся ободок по краю шляпы
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float cos = (float) Math.cos(angle) * radius;
            float sin = (float) Math.sin(angle) * radius;
            Color lineColor = getRainbowColor(i * 15);

            buffer.vertex(matrix, cos, 0, sin)
                    .color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 255).next();
        }
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private Color getRainbowColor(int offset) {
        float hue = ((System.currentTimeMillis() + offset) % 3000L) / 3000.0f;
        return Color.getHSBColor(hue, 0.75f, 1.0f);
    }
}
