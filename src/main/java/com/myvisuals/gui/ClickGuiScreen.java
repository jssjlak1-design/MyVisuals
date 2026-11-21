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
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;

    // Позиция окна для drag-and-drop
    private int guiX, guiY;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    // Анимации
    private float expandAnimation = 0f;
    private float hoverAnimation = 0f;
    private float[] moduleHoverAnimations;
    private float[] moduleExpandAnimations;

    // Константы UI
    private static final int GUI_WIDTH = 420;
    private static final int GUI_HEIGHT = 280;
    private static final int SIDE_PANEL_WIDTH = 100;
    private static final int MODULE_ITEM_HEIGHT_CLOSED = 32;
    private static final int MODULE_ITEM_HEIGHT_OPEN = 56;
    private static final float ANIMATION_SPEED = 0.15f;

    // Скролл
    private double scrollOffset = 0;
    private double targetScrollOffset = 0;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
        this.guiX = (this.width - GUI_WIDTH) / 2;
        this.guiY = (this.height - GUI_HEIGHT) / 2;
    }

    @Override
    public void tick() {
        super.tick();
        
        // Плавная анимация скролла
        scrollOffset = MathHelper.lerp(ANIMATION_SPEED, scrollOffset, targetScrollOffset);
        
        // Анимация раскрытия
        if (expandedModule != null) {
            expandAnimation = MathHelper.lerp(ANIMATION_SPEED, expandAnimation, 1f);
        } else {
            expandAnimation = MathHelper.lerp(ANIMATION_SPEED, expandAnimation, 0f);
        }
    }

    private boolean inBox(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Затемнение фона с градиентом
        drawGradientRect(ctx, 0, 0, this.width, this.height, 
            new Color(8, 12, 18, 200).getRGB(), 
            new Color(15, 20, 30, 220).getRGB());

        int guiW = Math.min(GUI_WIDTH, this.width - 24);
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);

        // Обновляем позицию если окно слишком большое
        if (guiX + guiW > this.width) guiX = this.width - guiW - 12;
        if (guiY + guiH > this.height) guiY = this.height - guiH - 12;
        if (guiX < 12) guiX = 12;
        if (guiY < 12) guiY = 12;

        // Цвета с градиентом
        Color accent1 = new Color(100, 180, 255);
        Color accent2 = new Color(150, 120, 255);
        int accentGradient = blendColors(accent1.getRGB(), accent2.getRGB(), 0.5f);

        // --- Стеклянный эффект (Glassmorphism) ---
        // Внешнее свечение
        drawGlow(ctx, guiX - 8, guiY - 8, guiW + 16, guiH + 16, 20, accentGradient, 40);
        
        // Основная рамка с градиентом
        drawGradientRoundedRect(ctx, guiX - 1, guiY - 1, guiW + 2, guiH + 2, 12, 
            new Color(100, 180, 255, 80).getRGB(), 
            new Color(150, 120, 255, 80).getRGB());
        
        // Основной фон (стекло)
        roundedRect(ctx, guiX, guiY, guiW, guiH, 12, new Color(20, 25, 35, 230).getRGB());
        
        // Внутренняя подсветка сверху
        drawGradientRect(ctx, guiX + 1, guiY + 1, guiX + guiW - 1, guiY + 60, 
            new Color(255, 255, 255, 15).getRGB(), 
            new Color(255, 255, 255, 0).getRGB());

        // Левая панель
        roundedRect(ctx, guiX + 6, guiY + 6, SIDE_PANEL_WIDTH - 6, guiH - 12, 10, 
            new Color(15, 20, 28, 200).getRGB());

        // --- Логотип ---
        drawLogo(ctx, guiX, guiY);
        
        // --- Профиль ---
        drawProfile(ctx, guiX, guiY, guiH);

        // --- Категории с иконками ---
        drawCategories(ctx, guiX, guiY, mouseX, mouseY);

        // --- Правая панель (Модули) ---
        int contentX = guiX + SIDE_PANEL_WIDTH + 10;
        int contentW = guiW - SIDE_PANEL_WIDTH - 20;
        int contentYStart = guiY + 40;
        int contentH = guiH - 50;

        // Заголовок категории с градиентом
        ctx.getMatrices().push();
        ctx.getMatrices().translate(contentX + 8, guiY + 14, 0);
        ctx.drawTextWithShadow(this.textRenderer, "§l" + selectedCategory.getName(), 0, 0, 0xFFFFFFFF);
        ctx.getMatrices().pop();
        
        ctx.drawTextWithShadow(this.textRenderer, "§7Настрой визуалы под себя", contentX + 8, guiY + 26, 0xFF8899B0);

        // Разделительная линия с градиентом
        drawGradientLine(ctx, contentX, guiY + 38, contentX + contentW, guiY + 38, 
            new Color(100, 180, 255, 100).getRGB(), 
            new Color(150, 120, 255, 100).getRGB());

        // Обрезаем область
        ctx.enableScissor(contentX, contentYStart, contentX + contentW, contentYStart + contentH);

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int currentY = contentYStart - (int) scrollOffset;

        // Инициализируем массивы анимаций если нужно
        if (moduleHoverAnimations == null || moduleHoverAnimations.length != modules.size()) {
            moduleHoverAnimations = new float[modules.size()];
            moduleExpandAnimations = new float[modules.size()];
        }

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            boolean exp = (expandedModule == mod);
            int mH = exp ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED;

            // Анимация hover
            boolean isHovered = inBox(mouseX, mouseY, contentX, currentY, contentW, mH);
            float targetHover = isHovered ? 1f : 0f;
            moduleHoverAnimations[i] = MathHelper.lerp(ANIMATION_SPEED, moduleHoverAnimations[i], targetHover);

            // Анимация раскрытия
            float targetExpand = exp ? 1f : 0f;
            moduleExpandAnimations[i] = MathHelper.lerp(ANIMATION_SPEED, moduleExpandAnimations[i], targetExpand);

            if (currentY + mH < contentYStart) {
                currentY += mH + 6;
                continue;
            }
            if (currentY > contentYStart + contentH) break;

            drawModernModuleItem(ctx, mod, contentX, currentY, contentW, mH, 
                moduleHoverAnimations[i], moduleExpandAnimations[i], mouseX, mouseY);

            currentY += mH + 6;
        }

        ctx.disableScissor();

        // Полоса прокрутки
        drawModernScrollBar(ctx, contentX, contentYStart, contentW, contentH, modules.size());

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawLogo(DrawContext ctx, int x, int y) {
        // Анимированный логотип с градиентом
        ctx.getMatrices().push();
        float scale = 1.8f + MathHelper.sin((float) (System.currentTimeMillis() / 500.0)) * 0.1f;
        ctx.getMatrices().translate(x + SIDE_PANEL_WIDTH / 2f, y + 18, 0);
        ctx.getMatrices().scale(scale, scale, 1.0f);
        ctx.drawTextWithShadow(this.textRenderer, "§lM", -4, 0, 0xFFFFFFFF);
        ctx.getMatrices().pop();
        
        ctx.drawTextWithShadow(this.textRenderer, "§7MyVisuals", x + 26, y + 38, 0xFF9CB0C8);
    }

    private void drawProfile(DrawContext ctx, int x, int y, int guiH) {
        int profY = y + guiH - 34;
        
        // Фон с градиентом
        drawGradientRoundedRect(ctx, x + 10, profY, SIDE_PANEL_WIDTH - 20, 24, 8,
            new Color(255, 255, 255, 10).getRGB(),
            new Color(100, 180, 255, 15).getRGB());
        
        String nick = (this.client != null && this.client.getSession() != null)
                ? this.client.getSession().getUsername() : "Player";
        if (nick.length() > 10) nick = nick.substring(0, 10);
        
        // Иконка пользователя
        ctx.drawTextWithShadow(this.textRenderer, "§b●", x + 16, profY + 8, 0xFF64B4FF);
        ctx.drawTextWithShadow(this.textRenderer, "§f" + nick, x + 26, profY + 8, 0xFFFFFFFF);
    }

    private void drawCategories(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int catY = y + 60;
        for (Category cat : Category.values()) {
            boolean sel = cat == selectedCategory;
            boolean hover = inBox(mouseX, mouseY, x + 10, catY, SIDE_PANEL_WIDTH - 20, 26);
            
            // Анимированный фон
            if (sel || hover) {
                int alpha = sel ? 60 : 30;
                drawGradientRoundedRect(ctx, x + 10, catY, SIDE_PANEL_WIDTH - 20, 26, 8,
                    new Color(100, 180, 255, alpha).getRGB(),
                    new Color(150, 120, 255, alpha).getRGB());
            }
            
            // Иконка категории
            String icon = getCategoryIcon(cat);
            ctx.drawTextWithShadow(this.textRenderer, icon, x + 18, catY + 9, sel ? 0xFF64B4FF : 0xFF8E9EB2);
            
            // Название
            int textColor = sel ? 0xFFFFFFFF : 0xFF8E9EB2;
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), x + 34, catY + 9, textColor);
            
            // Индикатор выбранной категории
            if (sel) {
                drawGradientLine(ctx, x + 10, catY, x + 10, catY + 26,
                    new Color(100, 180, 255, 200).getRGB(),
                    new Color(150, 120, 255, 200).getRGB());
            }
            
            catY += 32;
        }
    }

    private String getCategoryIcon(Category cat) {
        return switch (cat) {
            case RENDER -> "✦";
            case MOVEMENT -> "➤";
            case PLAYER -> "☻";
            case COMBAT -> "⚔";
            case WORLD -> "◈";
            case MISC -> "✧";
            default -> "●";
        };
    }

    private void drawModernModuleItem(DrawContext ctx, Module mod, int cX, int mY, int cW, int mH, 
                                      float hoverAnim, float expandAnim, int mouseX, int mouseY) {
        boolean exp = (expandedModule == mod);
        
        // Фон с анимацией
        Color baseColor = mod.isEnabled() 
            ? new Color(42, 52, 68, 200) 
            : new Color(30, 36, 48, 175);
        
        // Hover эффект
        if (hoverAnim > 0.01f) {
            int hoverAlpha = (int) (hoverAnim * 30);
            baseColor = new Color(
                Math.min(255, baseColor.getRed() + 20),
                Math.min(255, baseColor.getGreen() + 20),
                Math.min(255, baseColor.getBlue() + 20),
                baseColor.getAlpha() + hoverAlpha
            );
        }
        
        // Рисуем фон с тенью
        roundedRect(ctx, cX + 2, mY + 2, cW, mH, 8, new Color(0, 0, 0, 40).getRGB());
        roundedRect(ctx, cX, mY, cW, mH, 8, baseColor.getRGB());
        
        // Левая полоска индикатора (если включен)
        if (mod.isEnabled()) {
            drawGradientLine(ctx, cX, mY + 4, cX, mY + mH - 4,
                new Color(100, 180, 255, 200).getRGB(),
                new Color(150, 120, 255, 200).getRGB());
        }

        // Название модуля
        int nameColor = mod.isEnabled() ? 0xFFFFFFFF : 0xFFC5D0E0;
        ctx.drawTextWithShadow(this.textRenderer, mod.getName(), cX + 12, mY + 8, nameColor);
        
        // Описание
        String desc = mod.getDescription();
        if (desc != null && desc.length() > 32) desc = desc.substring(0, 32) + "..";
        if (desc != null) {
            ctx.drawTextWithShadow(this.textRenderer, desc, cX + 12, mY + 20, 0xFF7D8DA4);
        }

        // Кнопки управления
        int gX = cX + cW - 50;
        int swX = cX + cW - 32;

        // Шестеренка с hover эффектом
        boolean hoverGear = inBox(mouseX, mouseY, gX, mY + 8, 16, 16);
        int gearAlpha = hoverGear ? 60 : 25;
        roundedRect(ctx, gX, mY + 8, 16, 16, 4, new Color(255, 255, 255, gearAlpha).getRGB());
        ctx.drawTextWithShadow(this.textRenderer, "⚙", gX + 4, mY + 11, 0xFFD0DCF0);

        // Тумблер с анимацией
        Color toggleBg = mod.isEnabled() 
            ? new Color(100, 180, 255) 
            : new Color(60, 70, 88);
        roundedRect(ctx, swX, mY + 10, 26, 12, 6, toggleBg.getRGB());
        
        // Анимированный кружок тумблера
        float knobPos = mod.isEnabled() ? 1f : 0f;
        int knobX = swX + 2 + (int) (knobPos * 12);
        roundedRect(ctx, knobX, mY + 12, 8, 8, 4, 0xFFFFFFFF);

        // Раскрытые настройки с анимацией
        if (expandAnim > 0.01f) {
            ctx.getMatrices().push();
            float scaleY = expandAnim;
            ctx.getMatrices().translate(cX, mY + 34, 0);
            ctx.getMatrices().scale(1f, scaleY, 1f);
            drawModernSettings(ctx, mod, 0, 0, cW);
            ctx.getMatrices().pop();
        }
    }

    private void drawModernSettings(DrawContext ctx, Module mod, int cX, int mY, int cW) {
        String settingName = null;
        int colorRgb = 0;
        boolean hasColor = false;

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
            roundedRect(ctx, cX + 8, mY, cW - 16, 20, 6, new Color(255, 255, 255, 15).getRGB());
            
            String prefix = hasColor ? "§7Цвет: §f" : "§7Настройка: §f";
            ctx.drawTextWithShadow(this.textRenderer, prefix + settingName, cX + 14, mY + 6, 0xFFFFFFFF);

            if (hasColor) {
                int bx = cX + cW - 28;
                int by = mY + 4;
                int bw = 12;
                int bh = 12;

                // Цветной квадрат с тенью
                roundedRect(ctx, bx + 1, by + 1, bw, bh, 4, new Color(0, 0, 0, 60).getRGB());
                roundedRect(ctx, bx, by, bw, bh, 4, colorRgb);

                // Белая рамка
                int borderColor = 0xFFFFFFFF;
                ctx.fill(bx, by, bx + bw, by + 1, borderColor);
                ctx.fill(bx, by + bh - 1, bx + bw, by + bh, borderColor);
                ctx.fill(bx, by, bx + 1, by + bh, borderColor);
                ctx.fill(bx + bw - 1, by, bx + bw, by + bh, borderColor);
            }
        }
    }

    private void drawModernScrollBar(DrawContext ctx, int contX, int contY, int contW, int contH, int totalItems) {
        int estimatedContentHeight = totalItems * 38;
        if (estimatedContentHeight <= contH) return;

        int maxScroll = estimatedContentHeight - contH;
        float scrollRatio = (float) (scrollOffset / maxScroll);

        int barHeight = Math.max(30, (int) ((float) contH / estimatedContentHeight * contH));
        int maxBarY = contY + contH - barHeight;
        int barY = contY + (int) (scrollRatio * (contH - barHeight));
        if (barY > maxBarY) barY = maxBarY;
        if (barY < contY) barY = contY;

        // Фон скроллбара
        roundedRect(ctx, contX + contW + 6, contY, 4, contH, 2, new Color(255, 255, 255, 10).getRGB());
        
        // Сам скроллбар с градиентом
        drawGradientRoundedRect(ctx, contX + contW + 6, barY, 4, barHeight, 2,
            new Color(100, 180, 255, 180).getRGB(),
            new Color(150, 120, 255, 180).getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = Math.min(GUI_WIDTH, this.width - 24);
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);

        GuiSoundsModule sounds = MyVisualsClient.moduleManager.getGuiSounds();

        // Проверка drag-and-drop (клик по заголовку)
        if (inBox(mouseX, mouseY, guiX, guiY, guiW, 40)) {
            isDragging = true;
            dragOffsetX = (int) (mouseX - guiX);
            dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        // Клик по категориям
        int catY = guiY + 60;
        for (Category cat : Category.values()) {
            if (inBox(mouseX, mouseY, guiX + 10, catY, SIDE_PANEL_WIDTH - 20, 26)) {
                selectedCategory = cat;
                expandedModule = null;
                targetScrollOffset = 0;
                if (sounds != null) sounds.playToggleSound(true);
                return true;
            }
            catY += 32;
        }

        // Клик по модулям
        int contentX = guiX + SIDE_PANEL_WIDTH + 10;
        int contentW = guiW - SIDE_PANEL_WIDTH - 20;
        int contentYStart = guiY + 40;

        double adjustedMouseY = mouseY + scrollOffset;

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int currentY = contentYStart;

        for (Module mod : modules) {
            boolean exp = (expandedModule == mod);
            int mH = exp ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED;

            if (inBox(mouseX, adjustedMouseY, contentX, currentY, contentW, mH)) {
                if (exp && inBox(mouseX, adjustedMouseY, contentX + 8, currentY + 34, contentW - 16, 20)) {
                    handleSettingClick(mod, sounds);
                    return true;
                }

                if (inBox(mouseX, adjustedMouseY, contentX + contentW - 50, currentY, 20, mH)) {
                    expandedModule = exp ? null : mod;
                    if (sounds != null) sounds.playToggleSound(true);
                    return true;
                }

                if (button == 0) {
                    mod.toggle();
                    if (sounds != null) sounds.playToggleSound(mod.isEnabled());
                } else if (button == 1 || button == 2) {
                    expandedModule = exp ? null : mod;
                    if (sounds != null) sounds.playToggleSound(true);
                }
                return true;
            }
            currentY += mH + 6;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            guiX = (int) (mouseX - dragOffsetX);
            guiY = (int) (mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
        int guiH = Math.min(GUI_HEIGHT, this.height - 24);
        int contentH = guiH - 50;
        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);

        int totalContentHeight = 0;
        for (Module m : modules) {
            totalContentHeight += (expandedModule == m ? MODULE_ITEM_HEIGHT_OPEN : MODULE_ITEM_HEIGHT_CLOSED) + 6;
        }

        if (totalContentHeight > contentH) {
            targetScrollOffset -= verticalAmount * 15;
            targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, totalContentHeight - contentH));
        }
        return true;
    }

    // Утилиты для рисования

    private void roundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w < 1 || h < 1) return;
        int rad = Math.min(r, Math.min(w / 2, h / 2));

        ctx.fill(x + rad, y, x + w - rad, y + h, color);
        ctx.fill(x, y + rad, x + rad, y + h - rad, color);
        ctx.fill(x + w - rad, y + rad, x + w, y + h - rad, color);

        for (int i = 0; i < rad; i++) {
            int off = rad - (int) Math.round(Math.sqrt(rad * rad - (rad - i) * (rad - i)));
            ctx.fill(x + off, y + i, x + rad, y + i + 1, color);
            ctx.fill(x + w - rad, y + i, x + w - off, y + i + 1, color);
            ctx.fill(x + off, y + h - i - 1, x + rad, y + h - i, color);
            ctx.fill(x + w - rad, y + h - i - 1, x + w - off, y + h - i, color);
        }
    }

    private void drawGradientRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color1, int color2) {
        for (int y = y1; y < y2; y++) {
            float progress = (float) (y - y1) / (y2 - y1);
            int color = blendColors(color1, color2, progress);
            ctx.fill(x1, y, x2, y + 1, color);
        }
    }

    private void drawGradientRoundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color1, int color2) {
        // Упрощенная версия - рисуем градиентный прямоугольник
        drawGradientRect(ctx, x, y, x + w, y + h, color1, color2);
    }

    private void drawGradientLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color1, int color2) {
        if (y1 == y2) { // Горизонтальная линия
            for (int x = x1; x < x2; x++) {
                float progress = (float) (x - x1) / (x2 - x1);
                int color = blendColors(color1, color2, progress);
                ctx.fill(x, y1, x + 1, y2 + 1, color);
            }
        } else { // Вертикальная линия
            for (int y = y1; y < y2; y++) {
                float progress = (float) (y - y1) / (y2 - y1);
                int color = blendColors(color1, color2, progress);
                ctx.fill(x1, y, x2 + 1, y + 1, color);
            }
        }
    }

    private void drawGlow(DrawContext ctx, int x, int y, int w, int h, int radius, int color, int alpha) {
        for (int i = 0; i < radius; i++) {
            float progress = 1f - ((float) i / radius);
            int currentAlpha = (int) (alpha * progress);
            int glowColor = (color & 0x00FFFFFF) | (currentAlpha << 24);
            roundedRect(ctx, x - i, y - i, w + i * 2, h + i * 2, 12 + i, glowColor);
        }
    }

    private int blendColors(int color1, int color2, float progress) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
