package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class WatermarkHudModule extends Module {
    public WatermarkHudModule() {
        super("WatermarkHUD", "Красивая плашка с FPS и ником в углу", Category.MISC);
        setEnabled(true); // Включена по умолчанию

        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(DrawContext context, float tickDelta) {
        if (!isEnabled() || mc.player == null) return;

        String text = "MY VISUALS | " + mc.player.getName().getString() + " | " + mc.getCurrentFps() + " FPS";
        int width = mc.textRenderer.getWidth(text) + 12;

        // Тёмный фон и фиолетовая неоновая полоска сверху
        context.fill(8, 8, 8 + width, 24, new Color(18, 18, 24, 210).getRGB());
        context.fill(8, 8, 8 + width, 10, new Color(110, 85, 255, 255).getRGB());
        context.drawTextWithShadow(mc.textRenderer, text, 14, 13, 0xFFFFFF);
    }
}
