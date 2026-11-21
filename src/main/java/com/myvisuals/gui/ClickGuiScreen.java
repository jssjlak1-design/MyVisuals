package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.module.Setting;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private final Map<Category, Window> windows = new HashMap<>();
    private Window draggedWindow = null;
    private double dragOffsetX, dragOffsetY;
    
    // Цвета для стиля "Стекло"
    private static final int GLASS_BG = RenderUtils.getColor(20, 25, 30, 180); // Темный полупрозрачный
    private static final int GLASS_BORDER = RenderUtils.getColor(100, 150, 255, 80); // Голубая обводка
    private static final int ACCENT_COLOR = RenderUtils.getColor(0, 200, 255, 255);
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_DIM = 0xFFAAAAAA;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals ClickGUI"));
        
        // Инициализация окон для каждой категории
        int startX = 50;
        int gap = 220;
        for (Category cat : Category.values()) {
            windows.put(cat, new Window(cat, startX + (cat.ordinal() * gap), 50));
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Рисуем затемненный фон (опционально, можно убрать для чистого стекла)
        ctx.fillGradient(0, 0, this.width, this.height, 0x80000000, 0x80000000);

        // Рисуем окна
        for (Window window : windows.values()) {
            window.render(ctx, mouseX, mouseY, delta);
        }
        
        // Подсказка
        ctx.drawTextWithShadow(this.textRenderer, "Shift + ЛКМ — Перемещение окна", 10, this.height - 20, TEXT_DIM);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем клик по окнам (в обратном порядке, чтобы верхнее было активным)
        for (Window window : windows.values()) {
            if (window.isMouseOverHeader(mouseX, mouseY)) {
                if (hasShiftDown()) {
                    // Начало перетаскивания
                    draggedWindow = window;
                    dragOffsetX = mouseX - window.x;
                    dragOffsetY = mouseY - window.y;
                } else if (button == 0) {
                    // Клик по заголовку без Shift — ничего не делаем или сворачиваем (по желанию)
                }
                return true;
            }
            
            // Клик по модулям внутри окна
            if (window.isMouseOverContent(mouseX, mouseY)) {
                Module clickedModule = window.getModuleAt(mouseX, mouseY);
                if (clickedModule != null) {
                    clickedModule.toggle();
                    playSound();
                    return true;
                }
                
                // Клик по настройкам (простая реализация: переключение первого boolean setting)
                Setting<?> clickedSetting = window.getSettingAt(mouseX, mouseY);
                if (clickedSetting != null && clickedSetting.getValue() instanceof Boolean) {
                    Boolean current = (Boolean) clickedSetting.getValue();
                    clickedSetting.setValue(!current);
                    playSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggedWindow = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggedWindow != null) {
            draggedWindow.x = (int) (mouseX - dragOffsetX);
            draggedWindow.y = (int) (mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void playSound() {
        GuiSoundsModule sounds = MyVisualsClient.moduleManager.getModule(GuiSoundsModule.class);
        if (sounds != null && sounds.isEnabled()) {
            sounds.playToggleSound(true);
        }
    }

    // Внутренний класс для окна категории
    private class Window {
        private final Category category;
        private int x, y;
        private final int width = 200;
        private int height = 0; // Вычисляется динамически
        private boolean expanded = true;

        public Window(Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            // Вычисляем высоту окна
            int moduleCount = MyVisualsClient.moduleManager.getModulesByCategory(category).size();
            this.height = 25 + (moduleCount * 22) + 10; // Заголовок + модули + отступ

            // Рисуем фон окна (Стекло)
            RenderUtils.drawRoundedRect(ctx, x, y, width, height, 8, GLASS_BG);
            
            // Рисуем обводку (для эффекта свечения)
            // Можно использовать ctx.drawBorder, но fill с градиентом выглядит лучше для стекла
            ctx.fill(x, y, x + width, y + 2, ACCENT_COLOR); // Верхняя полоска акцента

            // Заголовок
            String title = category.getName();
            ctx.drawTextWithShadow(textRenderer, title, x + 10, y + 8, category.getColor());

            // Список модулей
            if (expanded) {
                int currentY = y + 30;
                for (Module module : MyVisualsClient.moduleManager.getModulesByCategory(category)) {
                    boolean hovered = isMouseOver(mouseX, mouseY, x + 5, currentY, width - 10, 20);
                    
                    // Фон кнопки
                    int btnColor = hovered ? RenderUtils.getColor(255, 255, 255, 30) : RenderUtils.getColor(255, 255, 255, 0);
                    RenderUtils.drawRoundedRect(ctx, x + 5, currentY, width - 10, 20, 4, btnColor);

                    // Индикатор включения (слева)
                    if (module.isEnabled()) {
                        ctx.fill(x + 7, currentY + 5, x + 9, currentY + 15, ACCENT_COLOR);
                    }

                    // Текст названия
                    int textColor = module.isEnabled() ? TEXT_COLOR : TEXT_DIM;
                    ctx.drawTextWithShadow(textRenderer, module.getName(), x + 15, currentY + 6, textColor);

                    // Отображение первой настройки (если есть boolean)
                    if (!module.getSettings().isEmpty()) {
                        Setting<?> firstSetting = module.getSettings().get(0);
                        if (firstSetting.getValue() instanceof Boolean) {
                            String valText = (Boolean) firstSetting.getValue() ? "[ON]" : "[OFF]";
                            ctx.drawTextWithShadow(textRenderer, valText, x + width - 45, currentY + 6, 
                                (Boolean) firstSetting.getValue() ? ACCENT_COLOR : TEXT_DIM);
                        }
                    }

                    currentY += 22;
                }
            }
        }

        public boolean isMouseOverHeader(double mx, double my) {
            return mx >= x && mx <= x + width && my >= y && my <= y + 25;
        }

        public boolean isMouseOverContent(double mx, double my) {
            return mx >= x && mx <= x + width && my >= y + 25 && my <= y + height;
        }

        public Module getModuleAt(double mx, double my) {
            if (!isMouseOverContent(mx, my)) return null;
            int currentY = y + 30;
            for (Module module : MyVisualsClient.moduleManager.getModulesByCategory(category)) {
                if (mx >= x + 5 && mx <= x + width - 5 && my >= currentY && my <= currentY + 20) {
                    return module;
                }
                currentY += 22;
            }
            return null;
        }
        
        public Setting<?> getSettingAt(double mx, double my) {
             // Упрощенная логика: если кликнули правее названия, пытаемся переключить первую настройку
             // В полноценном GUI здесь нужна более сложная проверка координат
             return null; 
        }
    }

    private boolean isMouseOver(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
