package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.ChinaHatModule;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.modules.JumpCirclesModule;
import com.myvisuals.modules.TargetEspModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;

    // Константы UI
    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 240;
    private static final int SIDE_PANEL_WIDTH = 88;
    private static final int MODULE_ITEM_HEIGHT_CLOSED = 26;
    private static final int MODULE_ITEM_HEIGHT_OPEN = 48;

    // Скролл
    private double scrollOffset = 0;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
    }

    // Проверка попадания в прямоугольник
    private boolean inBox(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        ctx.fill(0, 0, this.width, this.height, new Color(8, 12, 18, 180).getRGB());

        int guiW = Math.min(GUI_WIDTH, this.width - 24);
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;

        Color accent = new Color(115, 175, 255);

        // --- Рисуем основу GUI ---
        // Тень/Обводка
        rect(ctx, x - 1, y - 1, guiW + 2, guiH + 2, 10, new Color(170, 205, 240, 55).getRGB());
        // Основной фон
        rect(ctx, x, y, guiW, guiH, 10, new Color(24, 30, 40, 210).getRGB());
        // Левая панель
        rect(ctx, x + 4, y + 4, SIDE_PANEL_WIDTH - 4, guiH - 8, 8, new Color(18, 22, 30, 200).getRGB());

        // --- Логотип и Никнейм ---
        drawHeader(ctx, x, y);
        drawProfile(ctx, x, y, guiH);

        // --- Категории (Левая панель) ---
        drawCategories(ctx, x, y);

        // --- Правая панель (Модули) ---
        int contentX = x + SIDE_PANEL_WIDTH + 8;
        int contentW = guiW - SIDE_PANEL_WIDTH - 14;
        int contentYStart = y + 34;
        int contentH = guiH - 40; // Доступная высота для контента

        // Заголовок категории
        ctx.drawTextWithShadow(this.textRenderer, "§l" + selectedCategory.getName(), contentX + 4, y + 10, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§7Настрой визуалы", contentX + 4, y + 20, 0xFF8899B0);

        // Обрезаем область рисования модулей (Scissor), чтобы они не вылезали за пределы
        ctx.enableScissor(contentX, contentYStart, contentX + contentW, contentYStart + contentH);

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int currentY = contentYStart - (int) scrollOffset;

        for (Module mod : modules) {
            boolean exp = (expandedModule == mod);
            int mH = exp ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED;

            // Если элемент полностью выше видимой области - пропускаем отрисовку, но считаем позицию
            if (currentY + mH < contentYStart) {
                currentY += mH + 4;
                continue;
            }
            // Если элемент ниже видимой области - прерываем цикл
            if (currentY > contentYStart + contentH) break;

            drawModuleItem(ctx, mod, contentX, currentY, contentW, accent, mouseX, mouseY);

            currentY += mH + 4;
        }

        ctx.disableScissor();

        // Рисуем полосу прокрутки, если контент не влезает
        drawScrollBar(ctx, x, y, guiW, guiH, contentX, contentYStart, contentW, contentH, modules.size());

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx, int x, int y) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + SIDE_PANEL_WIDTH / 2f, y + 14, 0);
        ctx.getMatrices().scale(1.6f, 1.6f, 1.0f);
        ctx.drawTextWithShadow(this.textRenderer, "§lM", -4, 0, 0xFFFFFFFF);
        ctx.getMatrices().pop();
        ctx.drawTextWithShadow(this.textRenderer, "§7MyVisuals", x + 22, y + 32, 0xFF9CB0C8);
    }

    private void drawProfile(DrawContext ctx, int x, int y, int guiH) {
        int profY = y + guiH - 28;
        rect(ctx, x + 8, profY, SIDE_PANEL_WIDTH - 12, 20, 6, new Color(255, 255, 255, 16).getRGB());
        String nick = (this.client != null && this.client.getSession() != null)
                ? this.client.getSession().getUsername() : "Player";
        if (nick.length() > 10) nick = nick.substring(0, 10);
        ctx.drawTextWithShadow(this.textRenderer, "§b● §f" + nick, x + 14, profY + 6, 0xFFFFFFFF);
    }

    private void drawCategories(DrawContext ctx, int x, int y) {
        int catY = y + 50;
        for (Category cat : Category.values()) {
            boolean sel = cat == selectedCategory;
            if (sel)
                rect(ctx, x + 8, catY, SIDE_PANEL_WIDTH - 12, 20, 6, new Color(115, 175, 255, 50).getRGB());

            int color = sel ? 0xFFFFFFFF : 0xFF8E9EB2;
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), x + 16, catY + 6, color);
            catY += 23;
        }
    }

    private void drawModuleItem(DrawContext ctx, Module mod, int cX, int mY, int cW, Color accent, int mouseX, int mouseY) {
        boolean exp = (expandedModule == mod);
        int mH = exp ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED;

        // Фон модуля
        int bgColor = mod.isEnabled() ? new Color(42, 52, 68, 200).getRGB() : new Color(30, 36, 48, 175).getRGB();
        
        // Подсветка при наведении
        if (inBox(mouseX, mouseY, cX, mY, cW, mH)) {
             // Делаем цвет немного светлее при наведении
             Color baseColor = new Color(bgColor, true);
             bgColor = baseColor.brighter().getRGB();
        }

        rect(ctx, cX, mY, cW, mH, 6, bgColor);

        // Название и описание
        ctx.drawTextWithShadow(this.textRenderer, mod.getName(), cX + 8, mY + 5, mod.isEnabled() ? 0xFFFFFFFF : 0xFFC5D0E0);
        String desc = mod.getDescription();
        if (desc != null && desc.length() > 28) desc = desc.substring(0, 28) + "..";
        if (desc != null) {
            ctx.drawTextWithShadow(this.textRenderer, desc, cX + 8, mY + 15, 0xFF7D8DA4);
        }

        // Кнопки управления (Шестеренка и Тумблер)
        int gX = cX + cW - 44;
        int swX = cX + cW - 28;

        // Шестеренка
        boolean hoverGear = inBox(mouseX, mouseY, gX, mY + 6, 13, 13);
        rect(ctx, gX, mY + 6, 13, 13, 4, new Color(255, 255, 255, hoverGear || exp ? 45 : 18).getRGB());
        ctx.drawTextWithShadow(this.textRenderer, "⚙", gX + 3, mY + 9, 0xFFD0DCF0);

        // Тумблер
        rect(ctx, swX, mY + 7, 22, 11, 5, mod.isEnabled() ? accent.getRGB() : new Color(60, 70, 88).getRGB());
        int knobX = mod.isEnabled() ? swX + 13 : swX + 2;
        rect(ctx, knobX, mY + 9, 7, 7, 3, 0xFFFFFFFF);

        // Раскрытые настройки
        if (exp) {
            drawSettings(ctx, mod, cX, mY, cW);
        }
    }

    private void drawSettings(DrawContext ctx, Module mod, int cX, int mY, int cW) {
        String settingName = null;
        int colorRgb = 0;
        boolean hasColor = false;

        // Проверка типов модулей для настроек
        if (mod instanceof ChinaHatModule h) {
            settingName = h.getCurrentColorName();
            colorRgb = h.getCurrentColor().getRGB();
            hasColor = true;
        } else if (mod instanceof JumpCirclesModule j) {
            settingName = j.getCurrentColorName();
            colorRgb = j.getCurrentColor().getRGB();
            hasColor = true;
        } else if (mod instanceof TargetEspModule t) {
            settingName = t.getCurrentColorName();
            colorRgb = t.getCurrentColor().getRGB();
            hasColor = true;
        } else if (mod instanceof GuiSoundsModule gs) {
            settingName = gs.getCurrentSoundName();
        }

        if (settingName != null) {
            rect(ctx, cX + 6, mY + 26, cW - 12, 18, 4, new Color(255, 255, 255, 20).getRGB());
            String prefix = hasColor ? "§7Цвет: §f" : "§7Настройка: §f";
            ctx.drawTextWithShadow(this.textRenderer, prefix + settingName, cX + 10, mY + 31, 0xFFFFFFFF);

            if (hasColor) {
                int bx = cX + cW - 21;
                int by = mY + 29;
                int bw = 11;
                int bh = 11;

                // Рисуем сам цвет
                rect(ctx, bx, by, bw, bh, 3, colorRgb);

                // Рисуем белую рамку толщиной 1 пиксель (исправление для 1.20.4)
                int borderColor = 0xFFFFFFFF;
                ctx.fill(bx, by, bx + bw, by + 1, borderColor);       // Верх
                ctx.fill(bx, by + bh - 1, bx + bw, by + bh, borderColor); // Низ
                ctx.fill(bx, by, bx + 1, by + bh, borderColor);       // Лево
                ctx.fill(bx + bw - 1, by, bx + bw, by + bh, borderColor); // Право
            }
        }
    }

    private void drawScrollBar(DrawContext ctx, int guiX, int guiY, int guiW, int guiH, int contX, int contY, int contW, int contH, int totalItems) {
        // Примерный расчет высоты контента
        int estimatedContentHeight = totalItems * 30; 
        if (estimatedContentHeight <= contH) return;

        int maxScroll = estimatedContentHeight - contH;
        float scrollRatio = (float) (scrollOffset / maxScroll);
        
        int barHeight = Math.max(20, (int) ((float) contH / estimatedContentHeight * contH));
        // Ограничиваем положение скроллбара
        int maxBarY = contY + contH - barHeight;
        int barY = contY + (int) (scrollRatio * (contH - barHeight));
        if (barY > maxBarY) barY = maxBarY;
        if (barY < contY) barY = contY;

        // Рисуем сам скроллбар справа от контента
        rect(ctx, contX + contW + 4, barY, 4, barHeight, 2, new Color(115, 175, 255, 150).getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = Math.min(GUI_WIDTH, this.width - 24);
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;

        GuiSoundsModule sounds = MyVisualsClient.moduleManager.getGuiSounds();

        // Клик по категориям
        int catY = y + 50;
        for (Category cat : Category.values()) {
            if (inBox(mouseX, mouseY, x + 8, catY, SIDE_PANEL_WIDTH - 12, 20)) {
                selectedCategory = cat;
                expandedModule = null;
                scrollOffset = 0; // Сброс скролла при смене категории
                if (sounds != null) sounds.playToggleSound(true);
                return true;
            }
            catY += 23;
        }

        // Клик по модулям
        int contentX = x + SIDE_PANEL_WIDTH + 8;
        int contentW = guiW - SIDE_PANEL_WIDTH - 14;
        int contentYStart = y + 34;

        // Учитываем скролл при расчете позиции мыши относительно элементов
        double adjustedMouseY = mouseY + scrollOffset;

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int currentY = contentYStart;

        for (Module mod : modules) {
            boolean exp = (expandedModule == mod);
            int mH = exp ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED;

            // Проверяем попадание в текущий элемент
            if (inBox(mouseX, adjustedMouseY, contentX, currentY, contentW, mH)) {

                // Клик по настройкам (если раскрыто)
                if (exp && inBox(mouseX, adjustedMouseY, contentX + 6, currentY + 26, contentW - 12, 18)) {
                    handleSettingClick(mod, sounds);
                    return true;
                }

                // Клик по шестеренке
                if (inBox(mouseX, adjustedMouseY, contentX + contentW - 44, currentY, 16, mH)) {
                    expandedModule = exp ? null : mod;
                    if (sounds != null) sounds.playToggleSound(true);
                    return true;
                }

                // Клик по тумблеру или всему блоку
                if (button == 0) { // ЛКМ
                    mod.toggle();
                    if (sounds != null) sounds.playToggleSound(mod.isEnabled());
                } else if (button == 1 || button == 2) { // ПКМ или СКМ
                    expandedModule = exp ? null : mod;
                    if (sounds != null) sounds.playToggleSound(true);
                }
                return true;
            }
            currentY += mH + 4;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingClick(Module mod, GuiSoundsModule sounds) {
        if (mod instanceof ChinaHatModule h) { h.nextColor(); }
        else if (mod instanceof JumpCirclesModule j) { j.nextColor(); }
        else if (mod instanceof TargetEspModule t) { t.nextColor(); }
        else if (mod instanceof GuiSoundsModule gs) { gs.nextSound(); }

        if (sounds != null) sounds.playToggleSound(true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Логика скролла
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);
        int contentH = guiH - 40;
        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        
        // Примерная оценка высоты всего контента
        int totalContentHeight = 0;
        for (Module m : modules) {
            totalContentHeight += (expandedModule == m ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED) + 4;
        }

        if (totalContentHeight > contentH) {
            scrollOffset -= verticalAmount * 10; // Скорость скролла
            // Ограничиваем скролл
            scrollOffset = Math.max(0, Math.min(scrollOffset, totalContentHeight - contentH));
        }
        return true;
    }

    // Утилита для рисования прямоугольника со скруглением
    private void rect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w < 1 || h < 1) return;
        int rad = Math.min(r, Math.min(w / 2, h / 2));

        // Центр
        ctx.fill(x + rad, y, x + w - rad, y + h, color);
        // Лево и Право
        ctx.fill(x, y + rad, x + rad, y + h - rad, color);
        ctx.fill(x + w - rad, y + rad, x + w, y + h - rad, color);

        // Углы
        for (int i = 0; i < rad; i++) {
            int off = rad - (int) Math.round(Math.sqrt(rad * rad - (rad - i) * (rad - i)));
            ctx.fill(x + off, y + i, x + rad, y + i + 1, color);
            ctx.fill(x + w - rad, y + i, x + w - off, y + i + 1, color);
            ctx.fill(x + off, y + h - i - 1, x + rad, y + h - i, color);
            ctx.fill(x + w - rad, y + h - i - 1, x + w - off, y + h - i, color);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
