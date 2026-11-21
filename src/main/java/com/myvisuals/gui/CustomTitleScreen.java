package com.myvisuals.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

public class CustomTitleScreen extends Screen {
    // Цвета в формате ARGB (без java.awt!)
    private static final int COLOR_BG_TOP = 0xFF0C161A;
    private static final int COLOR_BG_BOTTOM = 0xFF182A2E;
    private static final int COLOR_CARD_BORDER = 0x37A0D7DC;
    private static final int COLOR_CARD_BG = 0xC3162024;
    private static final int COLOR_BUTTON_HOVER = 0x415FB4BE;
    private static final int COLOR_BUTTON_NORMAL = 0x0CFFFFFF;
    private static final int COLOR_ACCENT = 0xFF6EDCE6;
    
    private static final int CARD_WIDTH = 175;
    private static final int BUTTON_HEIGHT = 22;
    private static final int BUTTON_SPACING = 26;

    public CustomTitleScreen() {
        super(Text.literal("MyVisuals Main Menu"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Фон с градиентом
        ctx.fillGradient(0, 0, this.width, this.height, COLOR_BG_TOP, COLOR_BG_BOTTOM);
        
        // Анимированная волна
        float t = (System.currentTimeMillis() % 8000L) / 8000.0f;
        int waveY = (int) (this.height * 0.45f + Math.sin(t * Math.PI * 2.0) * 18);
        ctx.fillGradient(0, waveY, this.width, this.height, 0x2D2D5F5F, 0xA00A1014);

        // Карточка
        int cardHeight = Math.min(220, this.height - 28);
        int cardX = (this.width - CARD_WIDTH) / 2;
        int cardY = (this.height - cardHeight) / 2;

        drawRoundedRect(ctx, cardX - 1, cardY - 1, CARD_WIDTH + 2, cardHeight + 2, 11, COLOR_CARD_BORDER);
        drawRoundedRect(ctx, cardX, cardY, CARD_WIDTH, cardHeight, 11, COLOR_CARD_BG);

        // Логотип
        drawRoundedRect(ctx, cardX + 12, cardY + 12, 20, 20, 6, 0xFF4BA5AF);
        ctx.drawTextWithShadow(this.textRenderer, "§lM", cardX + 19, cardY + 18, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§lMyVisuals", cardX + 38, cardY + 14, 0xFFF0F8FA);
        ctx.drawTextWithShadow(this.textRenderer, "§71.20.4 Client", cardX + 38, cardY + 24, 0xFF8FB0B5);

        // Секция PLAY
        ctx.drawTextWithShadow(this.textRenderer, "§8PLAY", cardX + 14, cardY + 42, 0xFF6E8B90);
        
        // Кнопки
        int startY = cardY + 54;
        String[] items = {"Одиночная игра", "Сетевая игра", "Настройки", "Выход"};
        String[] icons = {">", "M", "O", "X"}; // ASCII вместо Unicode

        for (int i = 0; i < 4; i++) {
            int buttonY = startY + i * BUTTON_SPACING;
            
            // Добавляем отступ перед "Настройки" и "Выход"
            if (i == 2) {
                ctx.drawTextWithShadow(this.textRenderer, "§8CLIENT", cardX + 14, buttonY - 2, 0xFF6E8B90);
                buttonY += 10;
            } else if (i == 3) {
                buttonY += 10;
            }

            int buttonX = cardX + 10;
            int buttonWidth = CARD_WIDTH - 20;
            boolean hovered = isMouseOver(mouseX, mouseY, buttonX, buttonY, buttonWidth, BUTTON_HEIGHT);

            if (hovered) {
                drawRoundedRect(ctx, buttonX, buttonY, buttonWidth, BUTTON_HEIGHT, 6, COLOR_BUTTON_HOVER);
                ctx.fill(buttonX, buttonY + 4, buttonX + 2, buttonY + 18, COLOR_ACCENT);
            } else {
                drawRoundedRect(ctx, buttonX, buttonY, buttonWidth, BUTTON_HEIGHT, 6, COLOR_BUTTON_NORMAL);
            }

            ctx.drawTextWithShadow(this.textRenderer, icons[i], buttonX + 8, buttonY + 7, 
                hovered ? 0xFF88F0FA : 0xFF8CA8AD);
            ctx.drawTextWithShadow(this.textRenderer, items[i], buttonX + 22, buttonY + 7, 
                hovered ? 0xFFFFFFFF : 0xFFC5D8DC);
        }

        // Профиль игрока
        int profileY = cardY + cardHeight - 32;
        drawRoundedRect(ctx, cardX + 10, profileY, CARD_WIDTH - 20, 22, 6, 0x12FFFFFF);
        drawRoundedRect(ctx, cardX + 14, profileY + 4, 14, 14, 4, 0xFF55B9C3);
        
        String nickname = (this.client != null && this.client.getSession() != null) 
            ? this.client.getSession().getUsername() 
            : "Player";
        if (nickname.length() > 12) nickname = nickname.substring(0, 12);
        
        ctx.drawTextWithShadow(this.textRenderer, nickname, cardX + 32, profileY + 4, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§a● Online", cardX + 32, profileY + 13, 0xFF88E0B0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || this.client == null) return super.mouseClicked(mouseX, mouseY, button);

        int cardHeight = Math.min(220, this.height - 28);
        int cardX = (this.width - CARD_WIDTH) / 2;
        int cardY = (this.height - cardHeight) / 2;
        int startY = cardY + 54;

        for (int i = 0; i < 4; i++) {
            int buttonY = startY + i * BUTTON_SPACING;
            if (i == 2 || i == 3) buttonY += 10;

            int buttonX = cardX + 10;
            int buttonWidth = CARD_WIDTH - 20;

            if (isMouseOver(mouseX, mouseY, buttonX, buttonY, buttonWidth, BUTTON_HEIGHT)) {
                switch (i) {
                    case 0 -> this.client.setScreen(new SelectWorldScreen(this));
                    case 1 -> this.client.setScreen(new MultiplayerScreen(this));
                    case 2 -> this.client.setScreen(new OptionsScreen(this, this.client.options));
                    case 3 -> this.client.scheduleStop();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void drawRoundedRect(DrawContext ctx, int x, int y, int width, int height, int radius, int color) {
        if (width < 1 || height < 1) return;
        int r = Math.min(radius, Math.min(width / 2, height / 2));
        
        // Центральная часть
        ctx.fill(x + r, y, x + width - r, y + height, color);
        ctx.fill(x, y + r, x + r, y + height - r, color);
        ctx.fill(x + width - r, y + r, x + width, y + height - r, color);
        
        // Скругления
        for (int i = 0; i < r; i++) {
            int offset = r - (int) Math.round(Math.sqrt(r * r - (r - i) * (r - i)));
            ctx.fill(x + offset, y + i, x + r, y + i + 1, color);
            ctx.fill(x + width - r, y + i, x + width - offset, y + i + 1, color);
            ctx.fill(x + offset, y + height - i - 1, x + r, y + height - i, color);
            ctx.fill(x + width - r, y + height - i - 1, x + width - offset, y + height - i, color);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Нельзя закрыть главное меню по ESC
    }
}
