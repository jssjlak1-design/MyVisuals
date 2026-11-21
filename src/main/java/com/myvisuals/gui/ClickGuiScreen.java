package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private static final int WINDOW_WIDTH = 200;
    private static final int HEADER_HEIGHT = 25;
    private static final int ROW_HEIGHT = 22;
    private static final int ROW_CONTENT_HEIGHT = 20;

    private static final int GLASS_BG = RenderUtils.getColor(20, 25, 30, 180);
    private static final int ACCENT_COLOR = RenderUtils.getColor(0, 200, 255, 255);
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_DIM = 0xFFAAAAAA;
    private static final int HOVER_COLOR = RenderUtils.getColor(255, 255, 255, 30);

    private final Map<Category, Window> windows = new LinkedHashMap<>();

    private Window draggedWindow;
    private double dragOffsetX;
    private double dragOffsetY;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals ClickGUI"));

        int startX = 50;
        int gap = 220;

        for (Category category : Category.values()) {
            int x = startX + category.ordinal() * gap;
            windows.put(category, new Window(category, x, 50));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, width, height, 0x80000000, 0x80000000);

        for (Window window : windows.values()) {
            window.render(context, mouseX, mouseY);
        }

        context.drawTextWithShadow(
                textRenderer,
                "Shift + ЛКМ — перемещение окна",
                10,
                height - 20,
                TEXT_DIM
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Проверяем окна в обратном порядке: верхнее окно получает клик первым.
        Window[] orderedWindows = windows.values().toArray(new Window[0]);

        for (int i = orderedWindows.length - 1; i >= 0; i--) {
            Window window = orderedWindows[i];

            if (window.isMouseOverHeader(mouseX, mouseY)) {
                if (hasShiftDown()) {
                    draggedWindow = window;
                    dragOffsetX = mouseX - window.x;
                    dragOffsetY = mouseY - window.y;
                }
                return true;
            }

            Module module = window.getModuleAt(mouseX, mouseY);
            if (module != null) {
                module.toggle();
                playToggleSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {
        if (button == 0 && draggedWindow != null) {
            draggedWindow.x = clampX((int) (mouseX - dragOffsetX));
            draggedWindow.y = clampY((int) (mouseY - dragOffsetY));
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggedWindow = null;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private int clampX(int x) {
        return Math.max(0, Math.min(x, Math.max(0, width - WINDOW_WIDTH)));
    }

    private int clampY(int y) {
        return Math.max(0, Math.min(y, Math.max(0, height - HEADER_HEIGHT)));
    }

    private void playToggleSound() {
        if (MyVisualsClient.moduleManager == null) {
            return;
        }

        GuiSoundsModule sounds =
                MyVisualsClient.moduleManager.getModule(GuiSoundsModule.class);

        if (sounds != null && sounds.isEnabled()) {
            sounds.playToggleSound(true);
        }
    }

    private class Window {
        private final Category category;
        private int x;
        private int y;

        private Window(Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }

        private int getHeight() {
            int moduleCount =
                    MyVisualsClient.moduleManager.getModulesByCategory(category).size();

            return HEADER_HEIGHT + 5 + moduleCount * ROW_HEIGHT + 5;
        }

        private void render(DrawContext context, int mouseX, int mouseY) {
            int height = getHeight();

            RenderUtils.drawRoundedRect(
                    context, x, y, WINDOW_WIDTH, height, 8, GLASS_BG
            );

            context.fill(x, y, x + WINDOW_WIDTH, y + 2, ACCENT_COLOR);

            context.drawTextWithShadow(
                    textRenderer,
                    category.getName(),
                    x + 10,
                    y + 8,
                    category.getColor()
            );

            int rowY = y + HEADER_HEIGHT + 5;

            for (Module module :
                    MyVisualsClient.moduleManager.getModulesByCategory(category)) {
                boolean hovered = isMouseOver(
                        mouseX, mouseY,
                        x + 5, rowY,
                        WINDOW_WIDTH - 10, ROW_CONTENT_HEIGHT
                );

                if (hovered) {
                    RenderUtils.drawRoundedRect(
                            context,
                            x + 5, rowY,
                            WINDOW_WIDTH - 10, ROW_CONTENT_HEIGHT,
                            4, HOVER_COLOR
                    );
                }

                if (module.isEnabled()) {
                    context.fill(
                            x + 7, rowY + 5,
                            x + 9, rowY + 15,
                            ACCENT_COLOR
                    );
                }

                context.drawTextWithShadow(
                        textRenderer,
                        module.getName(),
                        x + 15,
                        rowY + 6,
                        module.isEnabled() ? TEXT_COLOR : TEXT_DIM
                );

                rowY += ROW_HEIGHT;
            }
        }

        private boolean isMouseOverHeader(double mouseX, double mouseY) {
            return mouseX >= x
                    && mouseX <= x + WINDOW_WIDTH
                    && mouseY >= y
                    && mouseY <= y + HEADER_HEIGHT;
        }

        private Module getModuleAt(double mouseX, double mouseY) {
            int rowY = y + HEADER_HEIGHT + 5;

            for (Module module :
                    MyVisualsClient.moduleManager.getModulesByCategory(category)) {
                boolean insideRow = mouseX >= x + 5
                        && mouseX <= x + WINDOW_WIDTH - 5
                        && mouseY >= rowY
                        && mouseY <= rowY + ROW_CONTENT_HEIGHT;

                if (insideRow) {
                    return module;
                }

                rowY += ROW_HEIGHT;
            }

            return null;
        }
    }

    private boolean isMouseOver(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int w,
            int h
    ) {
        return mouseX >= x
                && mouseX <= x + w
                && mouseY >= y
                && mouseY <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
