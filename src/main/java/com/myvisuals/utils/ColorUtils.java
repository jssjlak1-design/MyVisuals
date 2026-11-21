package com.myvisuals.utils;

import java.awt.Color;

public class ColorUtils {
    
    public static int getRainbow(float speed, float offset, float saturation, float brightness) {
        float hue = (float) ((System.currentTimeMillis() * speed + offset) % 10000L) / 10000.0f;
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return rgb | 0xFF000000; // Добавляем альфу
    }

    public static int fade(int color, float alpha) {
        int a = (int) (((color >> 24) & 0xFF) * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    public static int blend(int color1, int color2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        float r1 = (color1 >> 16) & 0xFF;
        float g1 = (color1 >> 8) & 0xFF;
        float b1 = color1 & 0xFF;
        float a1 = (color1 >> 24) & 0xFF;

        float r2 = (color2 >> 16) & 0xFF;
        float g2 = (color2 >> 8) & 0xFF;
        float b2 = color2 & 0xFF;
        float a2 = (color2 >> 24) & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        int a = (int) (a1 + (a2 - a1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
}
