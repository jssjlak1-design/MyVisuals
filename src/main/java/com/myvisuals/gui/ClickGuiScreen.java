package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.utils.Animation;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    
    // Стиль старого Expensive: минимализм, темный, компактный
    private static final int BG_COLOR = RenderUtils.getColor(15, 15, 18, 255);
    private static final int PANEL_COLOR = RenderUtils.getColor(22, 22, 26, 255);
    private static final int HEADER_COLOR = RenderUtils.getColor(28, 28, 32, 255);
    private static final int LINE_COLOR = RenderUtils.getColor(40, 40, 45, 255);
    private static final int ACCENT_COLOR = RenderUtils.getColor(255, 255, 255, 255); // Белый акцент
    private static final int TEXT_ON = 0xFFFFFFFF;
    private static final int TEXT_OFF = 0xFF555555;
    private static final int HOVER_BG = RenderUtils.getColor(255, 255, 255, 10);

    private Category selectedCategory = Category.COMBAT;
    private final Animation hoverAnim = new Animation(0.0f, 0.3f);
    private int hoveredModuleIndex = -1;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        ctx.fill(0, 0, this.width, this.height, RenderUtils.getColor(0, 0, 0, 120));

        // Главное окно (центрированное)
        int windowWidth = 400;
        int windowHeight = 280;
        int windowX = (this.width - windowWidth) / 2;
        int windowY = (this.height - windowHeight) / 2;

        // Фон окна
        RenderUtils.drawRoundedRect(ctx, windowX, windowY, windowWidth, windowHeight, 2, BG_COLOR);

        // Заголовок
        ctx.drawTextWithShadow(this.textRenderer, "§lMyVisuals", windowX + 10, windowY + 8, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§71.20.4", windowX + windowWidth - 40, windowY + 8, 0xFF666666);
        
        // Линия под заголовком
        ctx.fill(windowX, windowY + 22, windowX + windowWidth, windowY + 23, LINE_COLOR);

        // Табы категорий (горизонтально)
        int tabY = windowY + 25;
        int tabHeight = 20;
        int tabX = windowX + 10;
        
        for (Category cat : Category.values()) {
            boolean isSelected = (cat == selectedCategory);
            boolean isHovered = (mouseX >= tabX && mouseX <= tabX + 70 && 
                                 mouseY >= tabY && mouseY <= tabY + tabHeight);

            // Фон таба
            if (isSelected) {
                ctx.fill(tabX, tabY + tabHeight - 2, tabX + 70, tabY + tabHeight, ACCENT_COLOR);
            } else if (isHovered) {
                ctx.fill(tabX, tabY + tabHeight - 2, tabX + 70, tabY + tabHeight, 
                         RenderUtils.getColor(255, 255, 255, 50));
            }

            // Текст таба
            int textColor = isSelected ? TEXT_ON : (isHovered ? 0xFFCCCCCC : TEXT_OFF);
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), tabX + 5, tabY + 6, textColor);

            tabX += 75;
        }

        // Линия под табами
        ctx.fill(windowX, windowY + 47, windowX + windowWidth, windowY + 48, LINE_COLOR);

        // Панель модулей
        int panelX = windowX + 10;
        int panelY = windowY + 55;
        int panelWidth = windowWidth - 20;
        int moduleHeight = 18;
        
        List<Module> modules = getModulesByCategory(selectedCategory);
        hoveredModuleIndex = -1;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int moduleY = panelY + (i * moduleHeight);
            
            boolean isHovered = (mouseX >= panelX && mouseX <= panelX + panelWidth && 
                                 mouseY >= moduleY && mouseY <= moduleY + moduleHeight);
            
            if (isHovered) {
                hoveredModuleIndex = i;
                ctx.fill(panelX, moduleY, panelX + panelWidth, moduleY + moduleHeight, HOVER_BG);
            }

            // Индикатор включения (тонкая линия слева)
            if (module.isEnabled()) {
                ctx.fill(panelX, moduleY + 4, panelX + 2, moduleY + moduleHeight - 4, ACCENT_COLOR);
            }

            // Название модуля
            int textColor = module.isEnabled() ? TEXT_ON : TEXT_OFF;
            ctx.drawTextWithShadow(this.textRenderer, module.getName(), panelX + 8, moduleY + 5, textColor);

            // Статус справа
            String status = module.isEnabled() ? "§aON" : "§7OFF";
            ctx.drawTextWithShadow(this.textRenderer, status, panelX + panelWidth - 25, moduleY + 5, 
                                   module.isEnabled() ? 0xFF55FF55 : 0xFF555555);
        }

        // Нижняя линия
        ctx.fill(windowX, windowY + windowHeight - 20, windowX + windowWidth, windowY + windowHeight - 19, LINE_COLOR);
        
        // Подсказка внизу
        ctx.drawTextWithShadow(this.textRenderer, "§7[ЛКМ] — Вкл/Выкл  §7[ESC] — Закрыть", 
                               windowX + 10, windowY + windowHeight - 12, 0xFF666666);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int windowWidth = 400;
        int windowHeight = 280;
        int windowX = (this.width - windowWidth) / 2;
        int windowY = (this.height - windowHeight) / 2;

        // Клик по табам
        int tabY = windowY + 25;
        int tabX = windowX + 10;
        
        for (Category cat : Category.values()) {
            if (mouseX >= tabX && mouseX <= tabX + 70 && mouseY >= tabY && mouseY <= tabY + 20) {
                selectedCategory = cat;
                playSound();
                return true;
            }
            tabX += 75;
        }

        // Клик по модулям
        int panelX = windowX + 10;
        int panelY = windowY + 55;
        int panelWidth = windowWidth - 20;
        int moduleHeight = 18;
        
        List<Module> modules = getModulesByCategory(selectedCategory);
        
        for (int i = 0; i < modules.size(); i++) {
            int moduleY = panelY + (i * moduleHeight);
            if (mouseX >= panelX && mouseX <= panelX + panelWidth && 
                mouseY >= moduleY && mouseY <= moduleY + moduleHeight) {
                modules.get(i).toggle();
                playSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private List<Module> getModulesByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module m : MyVisualsClient.moduleManager.getModules()) {
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    private void playSound() {
        GuiSoundsModule sounds = MyVisualsClient.moduleManager.getModule(GuiSoundsModule.class);
        if (sounds != null && sounds.isEnabled()) {
            sounds.playToggleSound(true);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
