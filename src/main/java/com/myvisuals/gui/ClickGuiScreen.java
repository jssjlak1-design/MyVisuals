package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    
    // Цвета в стиле Expensive
    private static final int BG_COLOR = RenderUtils.getColor(15, 15, 20, 240);
    private static final int PANEL_BG = RenderUtils.getColor(25, 25, 35, 200);
    private static final int ACCENT_COLOR = RenderUtils.getColor(100, 100, 255, 255); // Синий акцент
    private static final int TEXT_ON = 0xFFFFFFFF;
    private static final int TEXT_OFF = 0xFF666666;
    private static final int HOVER_COLOR = RenderUtils.getColor(255, 255, 255, 20);

    private Category selectedCategory = Category.COMBAT;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Фон
        ctx.fill(0, 0, this.width, this.height, BG_COLOR);

        // Заголовок
        ctx.drawTextWithShadow(this.textRenderer, "§b§lMyVisuals", 20, 20, ACCENT_COLOR);
        ctx.drawTextWithShadow(this.textRenderer, "§71.20.4", 20, 32, 0xFF888888);

        // Панель категорий (горизонтально сверху)
        int catX = 150;
        int catY = 25;
        for (Category cat : Category.values()) {
            boolean isSelected = (cat == selectedCategory);
            boolean isHovered = (mouseX >= catX && mouseX <= catX + 80 && mouseY >= catY && mouseY <= catY + 20);

            if (isSelected) {
                ctx.fill(catX, catY + 18, catX + 80, catY + 20, ACCENT_COLOR); // Подчеркивание
            }

            int textColor = isSelected ? TEXT_ON : (isHovered ? 0xFFCCCCCC : TEXT_OFF);
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), catX + 5, catY + 6, textColor);

            catX += 90;
        }

        // Панель модулей (вертикально слева)
        int panelX = 20;
        int panelY = 70;
        int panelWidth = 180;
        
        int moduleCount = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory).size();
        int panelHeight = 30 + (moduleCount * 22) + 10;

        RenderUtils.drawRoundedRect(ctx, panelX, panelY, panelWidth, panelHeight, 4, PANEL_BG);

        // Заголовок панели
        ctx.drawTextWithShadow(this.textRenderer, selectedCategory.getName(), panelX + 10, panelY + 8, ACCENT_COLOR);
        ctx.fill(panelX, panelY + 20, panelX + panelWidth, panelY + 21, RenderUtils.getColor(255, 255, 255, 30));

        // Список модулей
        int currentY = panelY + 30;
        for (Module module : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean isHovered = (mouseX >= panelX + 5 && mouseX <= panelX + panelWidth - 5 && 
                                 mouseY >= currentY && mouseY <= currentY + 20);

            if (isHovered) {
                RenderUtils.drawRoundedRect(ctx, panelX + 5, currentY, panelWidth - 10, 20, 3, HOVER_COLOR);
            }

            // Индикатор включения
            if (module.isEnabled()) {
                ctx.fill(panelX + 10, currentY + 6, panelX + 14, currentY + 14, ACCENT_COLOR);
            }

            // Название модуля
            int textColor = module.isEnabled() ? TEXT_ON : TEXT_OFF;
            ctx.drawTextWithShadow(this.textRenderer, module.getName(), panelX + 22, currentY + 6, textColor);

            currentY += 22;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        // Клик по категориям
        int catX = 150;
        int catY = 25;
        for (Category cat : Category.values()) {
            if (mouseX >= catX && mouseX <= catX + 80 && mouseY >= catY && mouseY <= catY + 20) {
                selectedCategory = cat;
                playSound();
                return true;
            }
            catX += 90;
        }

        // Клик по модулям
        int panelX = 20;
        int panelY = 70 + 30;
        int panelWidth = 180;

        for (Module module : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            if (mouseX >= panelX + 5 && mouseX <= panelX + panelWidth - 5 && 
                mouseY >= panelY && mouseY <= panelY + 20) {
                module.toggle();
                playSound();
                return true;
            }
            panelY += 22;
        }

        return super.mouseClicked(mouseX, mouseY, button);
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
