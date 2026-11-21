package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private final Map<Category, Panel> panels = new HashMap<>();
    private Panel draggedPanel = null;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals"));
        
        float startX = 30;
        float gap = 140;
        int i = 0;
        for (Category cat : Category.values()) {
            panels.put(cat, new Panel(cat, startX + (i * gap), 40));
            i++;
        }
    }

    // Статический метод для получения модулей по категории (нужен для Panel.java)
    public static List<Module> getModules(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module m : MyVisualsClient.moduleManager.getModules()) {
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        ctx.fill(0, 0, this.width, this.height, RenderUtils.getColor(0, 0, 0, 100));
        
        // Рисуем панели
        for (Panel panel : panels.values()) {
            panel.render(ctx, mouseX, mouseY, delta);
        }
        
        // Подсказка внизу
        ctx.drawTextWithShadow(this.textRenderer, 
            "§7[Shift + ЛКМ] — Перетащить  §7[ЛКМ] — Вкл/Выкл  §7[ЛКМ по заголовку] — Свернуть", 
            10, this.height - 15, 0xFFAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Обрабатываем клики в обратном порядке (верхние панели приоритетнее)
        Panel[] panelArray = panels.values().toArray(new Panel[0]);
        for (int i = panelArray.length - 1; i >= 0; i--) {
            if (panelArray[i].mouseClicked(mouseX, mouseY, button)) {
                playSound();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels.values()) {
            panel.mouseReleased();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Panel panel : panels.values()) {
            panel.mouseDragged(mouseX, mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
