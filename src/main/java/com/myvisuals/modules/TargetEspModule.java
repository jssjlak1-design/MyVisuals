package com.myvisuals.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

public class TargetEspModule extends Module {
    private LivingEntity target;
    private long lastAttackTime;

    public TargetEspModule() {
        super("TargetESP", "Неоновое кольцо вокруг цели в бою", Category.COMBAT);

        // Запоминаем цель, когда ты её бьёшь
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (isEnabled() && world.isClient() && entity instanceof LivingEntity living && entity != mc.player) {
                target = living;
                lastAttackTime = System.currentTimeMillis();
            }
            return ActionResult.PASS;
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRender);
    }

    @Override
    public void onTick() {
        // Сбрасываем цель, если она умерла или прошло 5 секунд без ударов
        if (target != null && (!target.isAlive() || System.currentTimeMillis() - lastAttackTime > 5000)) {
            target = null;
        }
    }

    private void onRender(WorldRenderContext context) {
        if (!isEnabled() || target == null || mc.player == null) return;

        float tickDelta = context.tickDelta();
        Vec3d cam = context.camera().getPos();

        double x = MathHelper.lerp(tickDelta, target.prevX, target.getX()) - cam.x;
        double y = MathHelper.lerp(tickDelta, target.prevY, target.getY()) - cam.y;
        double z = MathHelper.lerp(tickDelta, target.prevZ, target.getZ()) - cam.z;

        // Плавное движение кольца вверх-вниз по телу врага
        double wave = (Math.sin(System.currentTimeMillis() / 350.0) + 1.0) * 0.5;
        double ringY = y + wave * target.getHeight();
        float radius = target.getWidth() * 0.85f;

        MatrixStack matrices = context.matrixStack();
        matrices.push();
        matrices.translate(x, ringY, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Если цель покраснела от удара — подсвечиваем красным, иначе фиолетовым
        Color color = target.hurtTime > 0 ? new Color(255, 65, 85, 220) : new Color(110, 85, 255, 220);

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        int segments = 48;
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float cos = (float) Math.cos(angle) * radius;
            float sin = (float) Math.sin(angle) * radius;

            buffer.vertex(matrix, cos, 0.06f, sin).color(color.getRed(), color.getGreen(), color.getBlue(), 0).next();
            buffer.vertex(matrix, cos, 0.0f, sin).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        }
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }
}
