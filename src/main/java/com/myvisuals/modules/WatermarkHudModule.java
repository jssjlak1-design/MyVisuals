package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WatermarkHudModule extends Module {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public WatermarkHudModule() {
        super("WatermarkHUD", "Стильная верхняя капсула со временем и FPS", Category.RENDER);
        setEnabled(true);

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (!isEnabled() || mc.player == null || mc.options.hudHidden) return;
            renderWatermark(context);
        });
    }

    private void renderWatermark(DrawContext context) {
        String time = LocalTime.now().format(TIME_FMT);
        int fps = mc.getCurrentFps();
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) {
                ping = entry.getLatency();
            }
        }

        String partTime = "§7" + time;
        String partName = "§f§lMy Visuals";
        String partStats = "§7" + fps + " fps §8• §7" + ping + " ms";
        String sep = "  §8|  ";

        String fullText = partTime + sep + partName + sep + partStats;

        int textW = mc.textRenderer.getWidth(fullText);
        int boxW = textW + 18;
        int boxH = 18;
        int screenW = mc.getWindow().getScaledWidth();
        int x = (screenW - boxW) / 2;
        int y = 6;

        // Тонкая внешняя обводка капсулы
        drawRoundedRect(context, x - 1, y - 1, boxW + 2, boxH + 2, 7, new Color(255, 255, 255, 36).getRGB());
        // Основной матовый фон капсулы
        drawRoundedRect(context, x, y, boxW, boxH, 7, new Color(24, 25, 31, 220).getRGB());
        // Акцентная полоска снизу по центру
        drawRoundedRect(context, x + boxW / 2 - 14, y + boxH - 2, 28, 2, 1, new Color(225, 65, 65, 230).getRGB());

        // Текст внутри капсулы
        context.drawTextWithShadow(mc.textRenderer, fullText, x + 9, y + 5, 0xFFFFFFFF);
    }

    private void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        int radius = Math.min(r, Math.min(w / 2, h / 2));
        ctx.fill(x + radius, y, x + w - radius, y + h, color);
        ctx.fill(x, y + radius, x + radius, y + h - radius, color);
        ctx.fill(x + w - radius, y + radius, x + w, y + h - radius, color);

        for (int i = 0; i < radius; i++) {
            int offset = radius - (int) Math.round(Math.sqrt(radius * radius - (radius - i) * (radius - i)));
            ctx.fill(x + offset, y + i, x + radius, y + i + 1, color);
            ctx.fill(x + w - radius, y + i, x + w - offset, y + i + 1, color);
            ctx.fill(x + offset, y + h - i - 1, x + radius, y + h - i, color);
            ctx.fill(x + w - radius, y + h - i - 1, x + w - offset, y + h - i, color);
        }
    }
}
