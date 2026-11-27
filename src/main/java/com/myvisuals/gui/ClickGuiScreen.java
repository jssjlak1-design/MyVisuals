package com.myvisuals.gui;

import com.myvisuals.utils.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClickGuiScreen extends Screen {

    private float panelX = 120;
    private float panelY = 70;
    private final float panelWidth = 240;
    private float panelHeight = 260;

    private boolean dragging = false;
    private float dragOffsetX, dragOffsetY;
    private float openAnimation = 0f;

    // Временная карта модулей (свяжите с вашими реальными классами модулей)
    private final Map<String, Boolean> modules = new LinkedHashMap<>() {{
        put("AutoSprint", true);
        put("ChinaHat", true);
        put("CustomSky", false);
        put("FullBright", true);
        put("GuiSounds", true);
        put("HitParticles", true);
        put("JumpCircles", false);
        put("KillEffects", false);
        put("TargetEsp", true);
        put("WatermarkHud", true);
    }};

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Плавная анимация появления (lerp)
        openAnimation += (1f - openAnimation) * 0.18f;

        if (dragging) {
            panelX = mouseX - dragOffsetX;
            panelY = mouseY - dragOffsetY;
        }

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        // Масштабирование от центра панели при открытии
        float centerX = panelX + panelWidth / 2f;
        float centerY = panelY + panelHeight / 2f;
        matrices.translate(centerX, centerY, 0);
        matrices.scale(0.85f + 0.15f * openAnimation, 0.85f + 0.15f * openAnimation, 1f);
        matrices.translate(-centerX, -centerY, 0);

        int alpha = (int) (185 * openAnimation);
        Color glassBg = new Color(14, 16, 24, alpha);
        Color headerBg = new Color(22, 25, 38, (int) (210 * openAnimation));
        Color accent = new Color(115, 103, 240, (int) (240 * openAnimation));

        // Основная стеклянная подложка
        Render2DUtil.drawRoundedRect(matrices, panelX, panelY, panelWidth, panelHeight, 10f, glassBg);
        Render2DUtil.drawRoundedOutline(matrices, panelX, panelY, panelWidth, panelHeight, 10f, 1.5f, accent);

        // Шапка окна
        Render2DUtil.drawRoundedRect(matrices, panelX + 4, panelY + 4, panelWidth - 8, 26, 7f, headerBg);
        context.drawText(textRenderer, "MyVisuals // Client", (int) panelX + 14, (int) panelY + 13, Color.WHITE.getRGB(), false);

        // Отрисовка списка модулей
        float itemY = panelY + 38;
        for (Map.Entry<String, Boolean> entry : modules.entrySet()) {
            String name = entry.getKey();
            boolean state = entry.getValue();
            boolean hovered = Render2DUtil.isHovered(mouseX, mouseY, panelX + 8, itemY, panelWidth - 16, 20);

            // Подсветка при наведении
            Color rowColor = hovered ? new Color(255, 255, 255, 22) : new Color(255, 255, 255, 8);
            Render2DUtil.drawRoundedRect(matrices, panelX + 8, itemY, panelWidth - 16, 20, 5f, rowColor);

            // Название модуля
            int textColor = state ? Color.WHITE.getRGB() : new Color(160, 165, 180).getRGB();
            context.drawText(textRenderer, name, (int) panelX + 16, (int) itemY + 6, textColor, false);

            // Современный Toggle-переключатель справа
            float switchX = panelX + panelWidth - 36;
            float switchY = itemY + 5;
            Color switchBg = state ? accent : new Color(45, 48, 65, 200);
            Render2DUtil.drawRoundedRect(matrices, switchX, switchY, 20, 10, 5f, switchBg);

            float knobX = state ? switchX + 11 : switchX + 2;
            Render2DUtil.drawRoundedRect(matrices, knobX, switchY + 1.5f, 7, 7, 3.5f, Color.WHITE);

            itemY += 22;
        }

        panelHeight = (itemY - panelY) + 6;
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Перетаскивание за шапку
            if (Render2DUtil.isHovered(mouseX, mouseY, panelX, panelY, panelWidth, 30)) {
                dragging = true;
                dragOffsetX = (float) mouseX - panelX;
                dragOffsetY = (float) mouseY - panelY;
                return true;
            }

            // Клик по модулям
            float itemY = panelY + 38;
            for (String name : modules.keySet()) {
                if (Render2DUtil.isHovered(mouseX, mouseY, panelX + 8, itemY, panelWidth - 16, 20)) {
                    modules.put(name, !modules.get(name));
                    // Здесь вызовите метод переключения реального модуля и звук GuiSoundsModule
                    return true;
                }
                itemY += 22;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // Отключаем стандартное серое затемнение фона Minecraft
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
