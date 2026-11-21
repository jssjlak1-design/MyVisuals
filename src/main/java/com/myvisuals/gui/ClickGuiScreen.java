package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;
    private int themeIndex = 0;
    private final String[] themes = {"Тёмная", "Фиолетовая", "Рубин"};

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 115).getRGB());

        int guiW = 470;
        int guiH = 285;
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;

        // Цвета темы
        Color accent = themeIndex == 0
                ? new Color(225, 65, 65, 255)
                : (themeIndex == 1 ? new Color(145, 80, 255, 255) : new Color(235, 75, 135, 255));

        // Внешняя тонкая обводка и основное скруглённое матовое окно
        drawRoundedRect(context, x - 1, y - 1, guiW + 2, guiH + 2, 10, new Color(255, 255, 255, 38).getRGB());
        drawRoundedRect(context, x, y, guiW, guiH, 10, new Color(26, 27, 33, 228).getRGB());

        // Левая боковая панель
        int sideW = 108;
        drawRoundedRect(context, x + 4, y + 4, sideW - 4, guiH - 8, 8, new Color(20, 21, 26, 215).getRGB());

        // Логотип слева сверху
        context.drawTextWithShadow(this.textRenderer, "§lMy Visuals", x + 16, y + 15, 0xFFF2F2F7);
        context.fill(x + 14, y + 29, x + sideW - 10, y + 30, new Color(255, 255, 255, 22).getRGB());

        // Секция категорий
        context.drawTextWithShadow(this.textRenderer, "Модули", x + 16, y + 38, 0xFF7A7D8C);

        int catY = y + 52;
        for (Category category : Category.values()) {
            boolean selected = category == selectedCategory;
            if (selected) {
                drawRoundedRect(context, x + 10, catY, sideW - 16, 22, 6, new Color(255, 255, 255, 24).getRGB());
                drawRoundedRect(context, x + 10, catY + 4, 3, 14, 2, accent.getRGB());
            }
            context.drawTextWithShadow(this.textRenderer, category.getName(), x + 20, catY + 7, selected ? 0xFFFFFFFF : 0xFF9EA2B2);
            catY += 26;
        }

        // Карточка профиля игрока внизу левой панели (как на скриншоте)
        int profY = y + guiH - 36;
        drawRoundedRect(context, x + 10, profY, sideW - 16, 26, 6, new Color(255, 255, 255, 14).getRGB());
        drawRoundedRect(context, x + 14, profY + 5, 16, 16, 4, accent.getRGB());
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        if (nick.length() > 10) nick = nick.substring(0, 10);
        context.drawTextWithShadow(this.textRenderer, nick, x + 34, profY + 5, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "User", x + 34, profY + 14, 0xFF7E8292);

        // Верхняя панель пути ("Модули > Render") и кнопка смены темы
        int contentX = x + sideW + 10;
        int contentW = guiW - sideW - 18;
        drawRoundedRect(context, contentX, y + 8, contentW, 24, 6, new Color(255, 255, 255, 12).getRGB());
        context.drawTextWithShadow(this.textRenderer, "§7Модули  ›  §f" + selectedCategory.getName(), contentX + 10, y + 16, 0xFFFFFFFF);

        // Кнопка выбора темы справа в шапке
        String themeLabel = "Тема: " + themes[themeIndex];
        int tBtnW = this.textRenderer.getWidth(themeLabel) + 14;
        int tBtnX = contentX + contentW - tBtnW - 6;
        drawRoundedRect(context, tBtnX, y + 11, tBtnW, 18, 5, new Color(255, 255, 255, 20).getRGB());
        context.drawTextWithShadow(this.textRenderer, themeLabel, tBtnX + 7, y + 16, 0xFFDCDCE6);

        // Двухколоночная сетка карточек модулей
        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int colW = (contentW - 8) / 2;
        int leftY = y + 40;
        int rightY = y + 40;

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            boolean isLeft = (i % 2 == 0);
            int cardX = isLeft ? contentX : contentX + colW + 8;
            int cardY = isLeft ? leftY : rightY;
            boolean isExpanded = (expandedModule == mod);
            int cardH = isExpanded ? 54 : 28;

            boolean hovered = mouseX >= cardX && mouseX <= cardX + colW && mouseY >= cardY && mouseY <= cardY + cardH;
            int bg = mod.isEnabled()
                    ? new Color(42, 43, 54, 235).getRGB()
                    : (hovered ? new Color(36, 37, 46, 225).getRGB() : new Color(30, 31, 39, 215).getRGB());

            drawRoundedRect(context, cardX, cardY, colW, cardH, 6, bg);
            if (mod.isEnabled()) {
                drawRoundedRect(context, cardX, cardY + 6, 2, 16, 1, accent.getRGB());
            }

            // Название модуля
            context.drawTextWithShadow(this.textRenderer, mod.getName(), cardX + 9, cardY + 10, mod.isEnabled() ? 0xFFFFFFFF : 0xFFB8BCCB);

            // Тумблер-переключатель справа (как в Fixxed Visuals)
            int swW = 24;
            int swH = 12;
            int swX = cardX + colW - swW - 8;
            int swY = cardY + 8;
            int swBg = mod.isEnabled() ? accent.getRGB() : new Color(55, 57, 68, 255).getRGB();
            drawRoundedRect(context, swX, swY, swW, swH, 6, swBg);

            int knobX = mod.isEnabled() ? (swX + swW - 10) : (swX + 2);
            drawRoundedRect(context, knobX, swY + 2, 8, 8, 4, 0xFFFFFFFF);

            // Раскрывающееся описание/настройка (при нажатии правой кнопкой или по нижней части)
            if (isExpanded) {
                context.fill(cardX + 8, cardY + 27, cardX + colW - 8, cardY + 28, new Color(255, 255, 255, 18).getRGB());
                String desc = mod.getDescription();
                if (desc.length() > 26) desc = desc.substring(0, 26) + "...";
                context.drawTextWithShadow(this.textRenderer, desc, cardX + 9, cardY + 35, 0xFF8C90A0);
            }

            if (isLeft) leftY += cardH + 6;
            else rightY += cardH + 6;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = 470;
        int guiH = 285;
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;
        int sideW = 108;

        // Клик по категориям
        int catY = y + 52;
        for (Category category : Category.values()) {
            if (mouseX >= x + 10 && mouseX <= x + sideW - 6 && mouseY >= catY && mouseY <= catY + 22) {
                selectedCategory = category;
                expandedModule = null;
                return true;
            }
            catY += 26;
        }

        int contentX = x + sideW + 10;
        int contentW = guiW - sideW - 18;

        // Клик по кнопке смены темы
        String themeLabel = "Тема: " + themes[themeIndex];
        int tBtnW = this.textRenderer.getWidth(themeLabel) + 14;
        int tBtnX = contentX + contentW - tBtnW - 6;
        if (mouseX >= tBtnX && mouseX <= tBtnX + tBtnW && mouseY >= y + 11 && mouseY <= y + 29) {
            themeIndex = (themeIndex + 1) % themes.length;
            return true;
        }

        // Клик по карточкам модулей
        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int colW = (contentW - 8) / 2;
        int leftY = y + 40;
        int rightY = y + 40;

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            boolean isLeft = (i % 2 == 0);
            int cardX = isLeft ? contentX : contentX + colW + 8;
            int cardY = isLeft ? leftY : rightY;
            boolean isExpanded = (expandedModule == mod);
            int cardH = isExpanded ? 54 : 28;

            if (mouseX >= cardX && mouseX <= cardX + colW && mouseY >= cardY && mouseY <= cardY + cardH) {
                if (button == 0) {
                    mod.toggle();
                } else if (button == 1) {
                    expandedModule = isExpanded ? null : mod;
                }
                return true;
            }

            if (isLeft) leftY += cardH + 6;
            else rightY += cardH + 6;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // Отрисовка аккуратного скруглённого прямоугольника
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
    public boolean shouldPause() {
        return false;
    }
}
