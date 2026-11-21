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

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, new Color(10, 8, 22, 175).getRGB(), new Color(25, 12, 45, 195).getRGB());

        int guiWidth = 440;
        int guiHeight = 275;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        // Внешняя неоновая обводка
        context.fill(x - 1, y - 1, x + guiWidth + 1, y + guiHeight + 1, new Color(138, 75, 255, 220).getRGB());
        // Основной фон окна
        context.fill(x, y, x + guiWidth, y + guiHeight, new Color(14, 14, 22, 248).getRGB());
        // Левая панель категорий
        context.fill(x, y, x + 115, y + guiHeight, new Color(20, 19, 32, 255).getRGB());
        // Верхняя градиентная полоса
        context.fillGradient(x, y, x + guiWidth, y + 4, new Color(135, 65, 255, 255).getRGB(), new Color(235, 75, 185, 255).getRGB());

        // Заголовок
        context.drawTextWithShadow(this.textRenderer, "§lMY VISUALS", x + 16, y + 15, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "§d1.20.4 PREMIUM", x + 16, y + 26, 0xBB88FF);

        // Кнопки категорий
        int catY = y + 48;
        for (Category category : Category.values()) {
            boolean isSelected = category == selectedCategory;
            if (isSelected) {
                context.fillGradient(x + 10, catY, x + 105, catY + 28, new Color(125, 65, 255, 230).getRGB(), new Color(195, 70, 215, 230).getRGB());
                context.fill(x + 10, catY, x + 13, catY + 28, 0xFFFFFFFF);
            } else {
                context.fill(x + 10, catY, x + 105, catY + 28, new Color(28, 27, 44, 210).getRGB());
            }

            context.drawTextWithShadow(this.textRenderer, category.getName(), x + 20, catY + 10, isSelected ? 0xFFFFFF : 0x9999B5);
            catY += 35;
        }

        // Карточки модулей
        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int modY = y + 18;
        for (Module module : modules) {
            boolean hovered = mouseX >= x + 128 && mouseX <= x + guiWidth - 14 && mouseY >= modY && mouseY <= modY + 34;
            int cardBg = module.isEnabled()
                    ? new Color(36, 28, 62, 240).getRGB()
                    : (hovered ? new Color(32, 31, 48, 235).getRGB() : new Color(22, 22, 34, 225).getRGB());

            // Рамка карточки
            if (module.isEnabled()) {
                context.fill(x + 127, modY - 1, x + guiWidth - 13, modY + 35, new Color(145, 80, 255, 190).getRGB());
            }
            context.fill(x + 128, modY, x + guiWidth - 14, modY + 34, cardBg);

            // Цветной индикатор слева
            int barColor = module.isEnabled() ? new Color(165, 90, 255, 255).getRGB() : new Color(65, 65, 85, 255).getRGB();
            context.fill(x + 128, modY, x + 132, modY + 34, barColor);

            // Название и описание
            context.drawTextWithShadow(this.textRenderer, module.getName(), x + 140, modY + 7, module.isEnabled() ? 0xFFFFFF : 0xBBBBCC);
            context.drawTextWithShadow(this.textRenderer, module.getDescription(), x + 140, modY + 20, 0x8888A0);

            // Переключатель ON / OFF справа
            String stateText = module.isEnabled() ? "ON" : "OFF";
            int pillColor = module.isEnabled() ? new Color(135, 65, 255, 240).getRGB() : new Color(45, 45, 60, 240).getRGB();
            context.fill(x + guiWidth - 52, modY + 9, x + guiWidth - 22, modY + 25, pillColor);
            context.drawTextWithShadow(this.textRenderer, stateText, x + guiWidth - 44, modY + 13, module.isEnabled() ? 0xFFFFFF : 0x888899);

            modY += 40;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiWidth = 440;
        int guiHeight = 275;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        int catY = y + 48;
        for (Category category : Category.values()) {
            if (mouseX >= x + 10 && mouseX <= x + 105 && mouseY >= catY && mouseY <= catY + 28) {
                selectedCategory = category;
                return true;
            }
            catY += 35;
        }

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int modY = y + 18;
        for (Module module : modules) {
            if (mouseX >= x + 128 && mouseX <= x + guiWidth - 14 && mouseY >= modY && mouseY <= modY + 34) {
                if (button == 0) {
                    module.toggle();
                }
                return true;
            }
            modY += 40;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
