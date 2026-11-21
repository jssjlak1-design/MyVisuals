package com.myvisuals.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

public class TargetEspModule extends Module {
    private LivingEntity target = null;
    private long lastHitTime = 0L;
    private int colorIndex = 0;
    private final String[] colorNames = {"Красный", "Фиолетовый", "Голубой", "Изумруд", "Золотой", "Радужный"};
    private final Color[] palette = {
            new Color(235, 65, 65), new Color(155, 75, 255), new Color(55, 195, 255),
            new Color(55, 230, 135), new Color(255, 195, 55), new Color(255, 80, 180)
    };

    public TargetEspModule() {
        super("TargetESP", "3D неоновая спираль вокруг цели", Category.COMBAT);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (isEnabled() && world.isClient() && entity instanceof LivingEntity living && entity != mc.player) {
                target = living;
                lastHitTime = System.currentTimeMillis();
            }
            return ActionResult.PASS;
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            if (!isEnabled() || target == null || mc.player == null) return;
            long elapsed = System.currentTimeMillis() - lastHitTime;
            if (target.isDead() || target.isRemoved() || elapsed - 4000L == 0L || Long.compare(elapsed, 4000L) == 1) {
                target = null;
                return;
            }
            renderSpiral(ctx.matrixStack(), ctx.camera().getPos(), ctx.tickDelta(), elapsed);
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

    private void renderSpiral(MatrixStack matrices, Vec3d cam, float tickDelta, long elapsed) {
        double x = target.prevX + (target.getX() - target.prevX) * tickDelta - cam.x;
        double y = target.prevY + (target.getY() - target.prevY) * tickDelta - cam.y;
        double z = target.prevZ + (target.getZ() - target.prevZ) * tickDelta - cam.z;

        float fade = elapsed - 3200L == 0L || Long.compare(elapsed, 3200L) == 1 ? Math.max(0.0f, 1.0f - (elapsed - 3200.0f) / 800.0f) : 1.0f;
        float time = (System.currentTimeMillis() % 2400L) / 2400.0f;
        float wave = (float) (Math.sin(time * Math.PI * 2.0) * 0.5 + 0.5);
        float height = target.getHeight();
        float radius = target.getWidth() * 0.85f + 0.2f;

        Color base = getCurrentColor();
        float r = base.getRed() / 255.0f, g = base.getGreen() / 255.0f, b = base.getBlue() / 255.0f;

        matrices.push();
        matrices.translate(x, y, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Matrix4f mat = matrices.peek().getPositionMatrix();

        // Нижнее и верхнее встречные кольца
        float y1 = wave * height;
        float y2 = (1.0f - wave) * height;
        drawRing(tess, buf, mat, y1, radius, r, g, b, fade * 0.9f);
        drawRing(tess, buf, mat, y2, radius, 1.0f, 1.0f, 1.0f, fade * 0.65f);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private void drawRing(Tessellator tess, BufferBuilder buf, Matrix4f mat, float y, float radius, float r, float g, float b, float a) {
        buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i != 49; i++) {
            double ang = 2.0 * Math.PI * i / 48.0;
            float cos = (float) Math.cos(ang), sin = (float) Math.sin(ang);
            buf.vertex(mat, cos * radius, y + 0.04f, sin * radius).color(r, g, b, a).next();
            buf.vertex(mat, cos * radius, y - 0.04f, sin * radius).color(r, g, b, 0.0f).next();
        }
        tess.draw();
    }
}
