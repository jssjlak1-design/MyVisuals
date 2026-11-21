package com.myvisuals.utils;

import net.minecraft.client.gui.DrawContext;

public class RenderUtils {

    public static int getColor(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawRoundedRect(DrawContext ctx, int x, int y, int width, int height, int radius, int color) {
        if (width < 1 || height < 1) return;
        int r = Math.min(radius, Math.min(width / 2, height / 2));
        
        ctx.fill(x + r, y, x + width - r, y + height, color);
        ctx.fill(x, y + r, x + r, y + height - r, color);
        ctx.fill(x + width - r, y + r, x + width, y + height - r, color);
        
        for (int i = 0; i < r; i++) {
            int offset = r - (int) Math.round(Math.sqrt(r * r - (r - i) * (r - i)));
            ctx.fill(x + offset, y + i, x + r, y + i + 1, color);
            ctx.fill(x + width - r, y + i, x + width - offset, y + i + 1, color);
            ctx.fill(x + offset, y + height - i - 1, x + r, y + height - i, color);
            ctx.fill(x + width - r, y + height - i - 1, x + width - offset, y + height - i, color);
        }
    }
}
