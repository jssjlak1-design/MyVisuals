package com.myvisuals.modules;

import com.myvisuals.utils.Render2DUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;
import java.util.List;

public class WatermarkHudModule {

    private static boolean enabled = true;

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!enabled) return;
            renderHud(drawContext);
        });
    }

    private static void renderHud(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        MatrixStack matrices = context.getMatrices();

        // Данные для Watermark
        String title = "MyVisuals";
        String info = " | " + mc.getCurrentFps() + " FPS | " +
                (int) mc.player.getX() + ", " + (int) mc.player.getY() + ", " + (int) mc.player.getZ();

        int titleWidth = mc.textRenderer.getWidth(title);
        int infoWidth = mc.textRenderer.getWidth(info);
        float boxWidth = titleWidth + infoWidth + 16;
        float boxHeight = 20;

        // Полупрозрачный тёмный фон (эффект матового стекла) + акцентная обводка
        Color bg = new Color(15, 17, 26, 165);
        Color accent = new Color(115, 103, 240, 220);

        Render2DUtil.drawRoundedRect(matrices, 8, 8, boxWidth, boxHeight, 6f, bg);
        Render2DUtil.drawRoundedOutline(matrices, 8, 8, boxWidth, boxHeight, 6f, 1.5f, accent);

        // Текст поверх кастомной плашки
        context.drawText(mc.textRenderer, title, 16, 14, new Color(140, 130, 255).getRGB(), false);
        context.drawText(mc.textRenderer, info, 16 + titleWidth, 14, new Color(225, 225, 235).getRGB(), false);

        // Список активных модулей (ArrayList) в правом верхнем углу
        renderActiveModules(context, mc, matrices);
    }

    private static void renderActiveModules(DrawContext context, MinecraftClient mc, MatrixStack matrices) {
        // Подключите сюда ваш менеджер модулей (например, ModuleManager.getEnabledModules())
        List<String> active = List.of("TargetEsp", "JumpCircles", "ChinaHat", "HitParticles", "CustomSky");

        int screenWidth = mc.getWindow().getScaledWidth();
        float yOffset = 8;

        for (String mod : active) {
            int w = mc.textRenderer.getWidth(mod);
            float x = screenWidth - w - 18;

            Render2DUtil.drawRoundedRect(matrices, x, yOffset, w + 10, 15, 4f, new Color(15, 17, 26, 150));
            // Неоновая полоска индикатора справа
            Render2DUtil.drawRoundedRect(matrices, screenWidth - 9, yOffset + 2, 2, 11, 1f, new Color(115, 103, 240, 255));
            context.drawText(mc.textRenderer, mod, (int) x + 5, (int) yOffset + 4, Color.WHITE.getRGB(), false);

            yOffset += 17;
        }
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
