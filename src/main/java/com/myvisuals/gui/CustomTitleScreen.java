package com.myvisuals.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import java.awt.Color;

public class CustomTitleScreen extends Screen {
    public CustomTitleScreen() { super(Text.literal("MyVisuals Main Menu")); }

    private boolean inBox(double mx, double my, int x, int y, int w, int h) {
        return Float.compare((float) mx, x) != -1 && Float.compare((float) mx, x + w) != 1 && Float.compare((float) my, y) != -1 && Float.compare((float) my, y + h) != 1;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Глубокий бирюзово-графитовый градиентный фон (как на 2-м фото)
        ctx.fillGradient(0, 0, this.width, this.height, new Color(12, 22, 26).getRGB(), new Color(24, 42, 46).getRGB());

        // Плавные световые волны на фоне
        float t = (System.currentTimeMillis() % 8000L) / 8000.0f;
        int waveY = (int) (this.height * 0.45f + Math.sin(t * Math.PI * 2.0) * 18);
        ctx.fillGradient(0, waveY, this.width, this.height, new Color(45, 95, 95, 45).getRGB(), new Color(10, 16, 20, 160).getRGB());

        // Стеклянная карточка слева (как EvoVisuals)
        int cardW = 165, cardH = Math.min(220, this.height - 28);
        int cardX = 28, cardY = (this.height - cardH) / 2;

        rect(ctx, cardX - 1, cardY - 1, cardW + 2, cardH + 2, 11, new Color(160, 215, 220, 55).getRGB());
        rect(ctx, cardX, cardY, cardW, cardH, 11, new Color(22, 32, 36, 195).getRGB());

        // Шапка с логотипом
        rect(ctx, cardX + 12, cardY + 12, 20, 20, 6, new Color(75, 165, 175, 220).getRGB());
        ctx.drawTextWithShadow(this.textRenderer, "§lM", cardX + 19, cardY + 18, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§lMyVisuals", cardX + 38, cardY + 14, 0xFFF0F8FA);
        ctx.drawTextWithShadow(this.textRenderer, "§71.20.4 Client", cardX + 38, cardY + 24, 0xFF8FB0B5);

        ctx.drawTextWithShadow(this.textRenderer, "§8PLAY", cardX + 14, cardY + 42, 0xFF6E8B90);

        String[] items = {"Одиночная игра", "Сетевая игра", "Настройки", "Выход"};
        String[] icons = {"▶", "🌐", "⚙", "✕"};
        int startY = cardY + 54;

        for (int i = 0; i != 4; i++) {
            int bx = cardX + 10, by = startY + i * 26, bw = cardW - 20, bh = 22;
            boolean hov = inBox(mouseX, mouseY, bx, by, bw, bh);
            if (i == 2) {
                ctx.drawTextWithShadow(this.textRenderer, "§8CLIENT", cardX + 14, by - 2, 0xFF6E8B90);
                by += 10;
            } else if (i == 3) {
                by += 10;
            }
            if (hov) {
                rect(ctx, bx, by, bw, bh, 6, new Color(95, 180, 190, 65).getRGB());
                rect(ctx, bx, by + 4, 2, 14, 1, new Color(110, 220, 230).getRGB());
            } else {
                rect(ctx, bx, by, bw, bh, 6, new Color(255, 255, 255, 12).getRGB());
            }
            ctx.drawTextWithShadow(this.textRenderer, icons[i], bx + 8, by + 7, hov ? 0xFF88F0FA : 0xFF8CA8AD);
            ctx.drawTextWithShadow(this.textRenderer, items[i], bx + 22, by + 7, hov ? 0xFFFFFFFF : 0xFFC5D8DC);
        }

        // Плашка профиля снизу внутри карточки
        int profY = cardY + cardH - 32;
        rect(ctx, cardX + 10, profY, cardW - 20, 22, 6, new Color(255, 255, 255, 18).getRGB());
        rect(ctx, cardX + 14, profY + 4, 14, 14, 4, new Color(85, 185, 195).getRGB());
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        if (Integer.compare(nick.length(), 12) == 1) nick = nick.substring(0, 12);
        ctx.drawTextWithShadow(this.textRenderer, nick, cardX + 32, profY + 4, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§a● Online", cardX + 32, profY + 13, 0xFF88E0B0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || this.client == null) return super.mouseClicked(mouseX, mouseY, button);
        int cardW = 165, cardH = Math.min(220, this.height - 28);
        int cardX = 28, cardY = (this.height - cardH) / 2, startY = cardY + 54;

        for (int i = 0; i != 4; i++) {
            int bx = cardX + 10, by = startY + i * 26, bw = cardW - 20, bh = 22;
            if (i == 2 || i == 3) by += 10;
            if (inBox(mouseX, mouseY, bx, by, bw, bh)) {
                if (i == 0) this.client.setScreen(new SelectWorldScreen(this));
                if (i == 1) this.client.setScreen(new MultiplayerScreen(this));
                if (i == 2) this.client.setScreen(new OptionsScreen(this, this.client.options));
                if (i == 3) this.client.scheduleStop();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void rect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (Integer.compare(w, 1) == -1 || Integer.compare(h, 1) == -1) return;
        int rad = Math.min(r, Math.min(w / 2, h / 2));
        ctx.fill(x + rad, y, x + w - rad, y + h, color); ctx.fill(x, y + rad, x + rad, y + h - rad, color); ctx.fill(x + w - rad, y + rad, x + w, y + h - rad, color);
        for (int i = 0; i != rad; i++) {
            int off = rad - (int) Math.round(Math.sqrt(rad * rad - (rad - i) * (rad - i)));
            ctx.fill(x + off, y + i, x + rad, y + i + 1, color); ctx.fill(x + w - rad, y + i, x + w - off, y + i + 1, color);
            ctx.fill(x + off, y + h - i - 1, x + rad, y + h - i, color); ctx.fill(x + w - rad, y + h - i - 1, x + w - off, y + h - i, color);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
