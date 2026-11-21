package com.myvisuals.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomTitleScreen extends Screen {
    private final List<float[]> stars = new ArrayList<>();
    private final Random random = new Random();

    public CustomTitleScreen() {
        super(Text.literal("MyVisuals Main Menu"));
    }

    @Override
    protected void init() {
        stars.clear();
        for (int i = 0; i < 65; i++) {
            stars.add(new float[]{
                    random.nextFloat() * this.width,
                    random.nextFloat() * this.height,
                    0.4f + random.nextFloat() * 1.4f,
                    1.0f + random.nextFloat() * 2.0f
            });
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Глубокий тёмно-фиолетовый градиентный фон
        context.fillGradient(0, 0, this.width, this.height,
                new Color(10, 8, 24, 255).getRGB(),
                new Color(28, 12, 52, 255).getRGB());

        // Анимированные светящиеся частицы на фоне
        for (float[] star : stars) {
            star[1] -= star[2] * 0.4f;
            if (star[1] + 5.0f == 0.0f || Float.compare(star[1], 0.0f) == -1) {
                star[1] = this.height;
                star[0] = random.nextFloat() * this.width;
            }
            int sx = (int) star[0];
            int sy = (int) star[1];
            int size = (int) star[3];
            context.fill(sx, sy, sx + size, sy + size, new Color(185, 130, 255, 140).getRGB());
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Центральная полупрозрачная карточка меню
        int cardW = 220;
        int cardH = 195;
        int cardX = centerX - cardW / 2;
        int cardY = centerY - 85;

        context.fill(cardX - 1, cardY - 1, cardX + cardW + 1, cardY + cardH + 1, new Color(145, 75, 255, 210).getRGB());
        context.fill(cardX, cardY, cardX + cardW, cardY + cardH, new Color(16, 15, 28, 235).getRGB());
        context.fillGradient(cardX, cardY, cardX + cardW, cardY + 3,
                new Color(135, 65, 255, 255).getRGB(),
                new Color(240, 80, 190, 255).getRGB());

        // Крупный логотип по центру
        context.getMatrices().push();
        context.getMatrices().translate(centerX, cardY + 18, 0);
        context.getMatrices().scale(1.6f, 1.6f, 1.0f);
        String title = "§lMY VISUALS";
        int titleW = this.textRenderer.getWidth(title);
        context.drawTextWithShadow(this.textRenderer, title, -titleW / 2, 0, 0xFFFFFF);
        context.getMatrices().pop();

        String sub = "§d1.20.4 CLIENT EDITION";
        context.drawTextWithShadow(this.textRenderer, sub, centerX - this.textRenderer.getWidth(sub) / 2, cardY + 38, 0xCC99FF);

        // 4 стильные кнопки
        String[] labels = {"Одиночная игра", "Сетевая игра", "Настройки", "Выйти из игры"};
        int btnW = 180;
        int btnH = 26;
        int startY = cardY + 58;

        for (int i = 0; i < labels.length; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * 32;
            boolean hovered = mouseX >= bx && mouseX <= bx + btnW && mouseY >= by && mouseY <= by + btnH;

            if (hovered) {
                context.fillGradient(bx, by, bx + btnW, by + btnH,
                        new Color(130, 65, 255, 245).getRGB(),
                        new Color(205, 75, 215, 245).getRGB());
                context.fill(bx, by, bx + 3, by + btnH, 0xFFFFFFFF);
            } else {
                context.fill(bx, by, bx + btnW, by + btnH, new Color(28, 26, 46, 230).getRGB());
                context.fill(bx, by, bx + 2, by + btnH, new Color(135, 70, 255, 200).getRGB());
            }

            int textW = this.textRenderer.getWidth(labels[i]);
            context.drawTextWithShadow(this.textRenderer, labels[i], centerX - textW / 2, by + 9, hovered ? 0xFFFFFF : 0xCCCCEE);
        }

        // Подпись снизу
        String footer = "MyVisuals 1.20.4 • Fabric";
        context.drawTextWithShadow(this.textRenderer, footer, 8, this.height - 14, 0x777799);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || this.client == null) return super.mouseClicked(mouseX, mouseY, button);

        int centerX = this.width / 2;
        int cardY = (this.height / 2) - 85;
        int btnW = 180;
        int btnH = 26;
        int startY = cardY + 58;

        for (int i = 0; i < 4; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * 32;
            if (mouseX >= bx && mouseX <= bx + btnW && mouseY >= by && mouseY <= by + btnH) {
                if (i == 0) this.client.setScreen(new SelectWorldScreen(this));
                if (i == 1) this.client.setScreen(new MultiplayerScreen(this));
                if (i == 2) this.client.setScreen(new OptionsScreen(this, this.client.options));
                if (i == 3) this.client.scheduleStop();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
