package com.myvisuals.modules;

import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import java.awt.Color;

public class WatermarkHudModule /* extends Module */ {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Вызывай этот метод из HudRenderCallback.EVENT
    public void onRenderHud(MatrixStack matrices) {
        if (mc.player == null || mc.options.debugEnabled) return;

        // 1. Собираем актуальные данные
        String clientName = "MyVisuals";
        String username = mc.getSession().getUsername();
        int fps = mc.getCurrentFps();
        String coords = String.format("XYZ: %d, %d, %d", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());

        String text = String.format("%s  |  %s  |  %d FPS  |  %s", clientName, username, fps, coords);

        // 2. Рассчитываем размеры плашки
        float x = 10f;
        float y = 10f;
        float paddingX = 8f;
        float height = 20f;
        float textWidth = mc.textRenderer.getWidth(text);
        float totalWidth = textWidth + (paddingX * 2);

        // 3. Цвета в стиле Modern Dark
        Color bgColor = new Color(18, 18, 24, 210);       // Темный полупрозрачный фон
        Color accent1 = new Color(138, 43, 226, 255);     // Фиолетовый неон
        Color accent2 = new Color(0, 191, 255, 255);      // Голубой неон

        // 4. Отрисовка через наш кастомный рендер
        RenderUtils.drawRoundedRect(matrices, x, y, totalWidth, height, 5f, bgColor);
        RenderUtils.drawGradientLine(matrices, x + 4, y, totalWidth - 8, 1.5f, accent1, accent2);

        // 5. Отрисовка текста (если нужен полный отказ от FontRenderer MC — потребуется кастомный Glyph-рендер)
        mc.textRenderer.draw(
            text,
            x + paddingX,
            y + (height - mc.textRenderer.fontHeight) / 2f + 1,
            Color.WHITE.getRGB(),
            true,
            matrices.peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL,
            0,
            15728880
        
        );
    }
}
