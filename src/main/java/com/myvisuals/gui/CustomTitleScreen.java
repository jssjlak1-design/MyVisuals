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
    private final List<float[]> particles = new ArrayList<>();
    private final Random random = new Random();

    public CustomTitleScreen() {
        super(Text.literal("MyVisuals Main Menu"));
    }

    @Override
    protected void init() {
        particles.clear();
        for (int i = 0; i < 55; i++) {
            particles.add(new float[]{
                    random.nextFloat() * this.width,
                    random.nextFloat() * this.height,
                    0.3f + random.nextFloat() * 0.9f,
                    1.0f + random.nextFloat() * 1.5f
            });
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Глубокий матовый графитовый фон в стиле Fixxed Visuals
        context.fillGradient(0, 0, this.width, this.height,
                new Color(15, 16, 20, 255).getRGB(),
                new Color(24, 25, 32, 255).getRGB());

        // Лёгкие плавающие частицы на фоне
        for (float[] p : particles) {
            p[1] -= p[2] * 0.35f;
            if (Float.compare(p[1], -4.0f) == -1) {
                p[1] = this.height + 4.0f;
                p[0] = random.nextFloat() * this.width;
            }
            int px = (int) p[0];
            int py = (int) p[1];
            int sz = (int) p[3];
            context.fill(px, py, px + sz, py + sz, new Color(225, 70, 70, 85).getRGB());
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Центральная скруглённая матовая панель
        int cardW = 230;
        int cardH = 205;
        int cardX = centerX - cardW / 2;
        int cardY = centerY - 98;

        Color accent = new Color(225, 65, 65, 255);

        // Тонкая обводка и тело карточки
        drawRoundedRect(context, cardX - 1, cardY - 1, cardW + 2, cardH + 2, 10, new Color(255, 255, 255, 36).getRGB());
        drawRoundedRect(context, cardX, cardY, cardW, cardH, 10, new Color(26, 27, 33, 238).getRGB());

        // Внутренняя шапка карточки
        drawRoundedRect(context, cardX + 8, cardY + 8, cardW - 16, 36, 7, new Color(20, 21, 26, 225).getRGB());
        drawRoundedRect(context, centerX - 18, cardY + 42, 36, 2, 1, accent.getRGB());

        // Логотип по центру шапки
        context.getMatrices().push();
        context.getMatrices().translate(centerX, cardY + 15, 0);
        context.getMatrices().scale(1.35f, 1.35f, 1.0f);
        String title = "§lMy Visuals";
        int titleW = this.textRenderer.getWidth(title);
        context.drawTextWithShadow(this.textRenderer, title, -titleW / 2, 0, 0xFFF2F2F7);
        context.getMatrices().pop();

        String sub = "1.20.4  •  Fabric Edition";
        context.drawTextWithShadow(this.textRenderer, sub, centerX - this.textRenderer.getWidth(sub) / 2, cardY + 30, 0xFF8A8E9E);

        // 4 скруглённые кнопки в едином стиле с ClickGUI
        String[] labels = {"Одиночная игра", "Сетевая игра", "Настройки", "Выйти из игры"};
        int btnW = 196;
        int btnH = 28;
        int startY = cardY + 54;

        for (int i = 0; i < labels.length; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * 34;
            boolean hovered = mouseX >= bx && mouseX <= bx + btnW && mouseY >= by && mouseY <= by + btnH;

            int btnBg = hovered ? new Color(42, 43, 54, 245).getRGB() : new Color(32, 33, 42, 225).getRGB();
            if (hovered) {
                drawRoundedRect(context, bx - 1, by - 1, btnW + 2, btnH + 2, 7, new Color(225, 65, 65, 160).getRGB());
            }
            drawRoundedRect(context, bx, by, btnW, btnH, 7, btnBg);

            // Красный индикатор слева при наведении
            if (hovered) {
                drawRoundedRect(context, bx, by + 6, 3, 16, 1, accent.getRGB());
            }

            int textW = this.textRenderer.getWidth(labels[i]);
            context.drawTextWithShadow(this.textRenderer, labels[i], centerX - textW / 2, by + 10, hovered ? 0xFFFFFFFF : 0xFFB8BCCB);
        }

        // Нижняя капсула со статусом справа внизу (чтобы не перекрывалась кнопками управления слева)
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        String badge = "§7User: §f" + nick + "  §8|  §cMy Visuals 1.20.4";
        int badgeW = this.textRenderer.getWidth(badge) + 16;
        int badgeX = this.width - badgeW - 8;
        int badgeY = this.height - 24;
        drawRoundedRect(context, badgeX, badgeY, badgeW, 18, 6, new Color(24, 25, 31, 220).getRGB());
        context.drawTextWithShadow(this.textRenderer, badge, badgeX + 8, badgeY + 5, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || this.client == null) return super.mouseClicked(mouseX, mouseY, button);

        int centerX = this.width / 2;
        int cardY = (this.height / 2) - 98;
        int btnW = 196;
        int btnH = 28;
        int startY = cardY + 54;

        for (int i = 0; i < 4; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * 34;
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

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
