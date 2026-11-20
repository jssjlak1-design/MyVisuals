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
        context.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());

        int guiWidth = 420;
        int guiHeight = 260;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        context.fill(x, y, x + guiWidth, y + guiHeight, new Color(18, 18, 24, 240).getRGB());
        context.fill(x, y, x + guiWidth, y + 3, new Color(110, 85, 255, 255).getRGB());
        context.drawTextWithShadow(this.textRenderer, "MY VISUALS 1.20.4", x + 12, y + 12, 0xFFFFFF);

        int catY = y + 40;
        for (Category category : Category.values()) {
            boolean isSelected = category == selectedCategory;
            int bgColor = isSelected ? new Color(110, 85, 255, 180).getRGB() : new Color(30, 30, 40, 200).getRGB();

            context.fill(x + 10, catY, x + 105, catY + 24, bgColor);
            context.drawTextWithShadow(this.textRenderer, category.getName(), x + 18, catY + 8, 0xFFFFFF);
            catY += 30;
        }

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int modY = y + 40;
        for (Module module : modules) {
            boolean hovered = mouseX >= x + 120 && mouseX <= x + guiWidth - 15 && mouseY >= modY && mouseY <= modY + 28;
            int cardColor = hovered ? new Color(42, 42, 56, 230).getRGB() : new Color(28, 28, 38, 220).getRGB();

            context.fill(x + 120, modY, x + guiWidth - 15, modY + 28, cardColor);
            int statusColor = module.isEnabled() ? new Color(110, 85, 255, 255).getRGB() : new Color(80, 80, 95, 255).getRGB();
            context.fill(x + 120, modY, x + 124, modY + 28, statusColor);

            context.drawTextWithShadow(this.textRenderer, module.getName(), x + 132, modY + 6, module.isEnabled() ? 0xFFFFFF : 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, module.getDescription(), x + 132, modY + 17, 0x777788);
            modY += 34;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiWidth = 420;
        int guiHeight = 260;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        int catY = y + 40;
        for (Category category : Category.values()) {
            if (mouseX >= x + 10 && mouseX <= x + 105 && mouseY >= catY && mouseY <= catY + 24) {
                selectedCategory = category;
                return true;
            }
            catY += 30;
        }

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int modY = y + 40;
        for (Module module : modules) {
            if (mouseX >= x + 120 && mouseX <= x + guiWidth - 15 && mouseY >= modY && mouseY <= modY + 28) {
                if (button == 0) {
                    module.toggle();
                }
                return true;
            }
            modY += 34;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
