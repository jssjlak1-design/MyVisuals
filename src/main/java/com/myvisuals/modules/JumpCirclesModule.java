package com.myvisuals.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JumpCirclesModule extends Module {
    private final List<Circle> circles = new ArrayList<>();
    private boolean wasOnGround = true;

    public JumpCirclesModule() {
        super("JumpCircles", "Неоновые круги при прыжке", Category.RENDER);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRender);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        // Если игрок оторвался от земли — создаём новый круг
        if (wasOnGround && !mc.player.isOnGround() && mc.player.getVelocity().y > 0) {
            circles.add(new Circle(mc.player.getPos()));
        }
        wasOnGround = mc.player.isOnGround();

        // Обновляем анимацию кругов и удаляем старые
        Iterator<Circle> it = circles.iterator();
        while (it.hasNext()) {
            Circle c = it.next();
            c.age++;
            if (c.age > 35) {
                it.remove();
            }
        }
    }

    private void onRender(WorldRenderContext context) {
        if (!isEnabled() || circles.isEmpty() || mc.player == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cam = context.camera().getPos();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (Circle circle : circles) {
            float progress = circle.age / 35.0f;
            float radius = 0.2f + progress * 1.4f;
            int alpha = (int) ((1.0f - progress) * 200);
            Color color = new Color(110, 85, 255, Math.max(0, alpha));

            matrices.push();
            matrices.translate(circle.pos.x - cam.x, circle.pos.y - cam.y + 0.02, circle.pos.z - cam.z);
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            int segments = 48;
            for (int i = 0; i <= segments; i++) {
                double angle = 2.0 * Math.PI * i / segments;
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);

                buffer.vertex(matrix, cos * (radius - 0.08f), 0, sin * (radius - 0.08f))
                        .color(color.getRed(), color.getGreen(), color.getBlue(), 0).next();
                buffer.vertex(matrix, cos * radius, 0, sin * radius)
                        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
            }
            tessellator.draw();
            matrices.pop();
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static class Circle {
        final Vec3d pos;
        int age = 0;

        Circle(Vec3d pos) {
            this.pos = pos;
        }
    }
}
