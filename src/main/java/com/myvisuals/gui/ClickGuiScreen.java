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
    private static final int HEADER_HEIGHT = 28;
    private static final int ROW_HEIGHT = 23;
    private static final int ROW_HEIGHT_VISIBLE = 20;

    private static final int ACCENT_COLOR =
            RenderUtils.getColor(0, 200, 255, 255);

    private final Map<Category, Window> windows = new LinkedHashMap<>();

    private Window draggedWindow;
    private double dragOffsetX;
    private double dragOffsetY;

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals"));

        int startX = 35;
        int gap = 215;

        for (Category category : Category.values()) {
            int x = startX + category.ordinal() * gap;
            windows.put(category, new Window(category, x, 55));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемнённый фон
        context.fillGradient(
                0, 0, width, height,
                0xC9080B12,
                0xE6080B12
        );

        // Заголовок экрана
        context.drawTextWithShadow(
                textRenderer,
                "MYVISUALS",
                18,
                15,
                ACCENT_COLOR
        );

        context.drawTextWithShadow(
                textRenderer,
                "CLIENT MENU",
                18,
                28,
                0xFF8B97AA
        );

        for (Window window : windows.values()) {
            window.render(context, mouseX, mouseY);
        }

        context.drawTextWithShadow(
                textRenderer,
                "SHIFT + ЛКМ — перемещение окна",
                18,
                height - 20,
                0xFF8B97AA
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        Window[] orderedWindows = windows.values().toArray(new Window[0]);

        // Последнее нарисованное окно проверяем первым
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

    @Override
    public boolean shouldPause() {
        return false;
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
            if (MyVisualsClient.moduleManager == null) {
                return HEADER_HEIGHT + 25;
            }

            int moduleCount =
                    MyVisualsClient.moduleManager
                            .getModulesByCategory(category)
                            .size();

            return HEADER_HEIGHT + 12 + moduleCount * ROW_HEIGHT + 20;
        }

        private void render(DrawContext context, int mouseX, int mouseY) {
            if (MyVisualsClient.moduleManager == null) {
                return;
            }

            int height = getHeight();

            // Фон панели
            RenderUtils.drawRoundedRect(
                    context,
                    x, y,
                    WINDOW_WIDTH, height,
                    9,
                    RenderUtils.getColor(13, 17, 25, 238)
            );

            // Акцентная полоса и линия под заголовком
            context.fill(
                    x + 10, y,
                    x + WINDOW_WIDTH - 10, y + 2,
                    ACCENT_COLOR
            );

            context.fill(
                    x + 10, y + HEADER_HEIGHT,
                    x + WINDOW_WIDTH - 10, y + HEADER_HEIGHT + 1,
                    RenderUtils.getColor(255, 255, 255, 28)
            );

            // Цветная точка и название категории
            context.fill(
                    x + 12, y + 11,
                    x + 17, y + 16,
                    category.getColor()
            );

            context.drawTextWithShadow(
                    textRenderer,
                    category.getName(),
                    x + 23,
                    y + 9,
                    0xFFFFFFFF
            );

            int moduleCount =
                    MyVisualsClient.moduleManager
                            .getModulesByCategory(category)
                            .size();

            String countText = String.valueOf(moduleCount);
            int countWidth = textRenderer.getWidth(countText);

            RenderUtils.drawRoundedRect(
                    context,
                    x + WINDOW_WIDTH - countWidth - 21,
                    y + 7,
                    countWidth + 12,
                    15,
                    5,
                    RenderUtils.getColor(255, 255, 255, 20)
            );

            context.drawTextWithShadow(
                    textRenderer,
                    countText,
                    x + WINDOW_WIDTH - countWidth - 15,
                    y + 10,
                    0xFFB8C4D8
            );

            // Карточки модулей
            int rowY = y + HEADER_HEIGHT + 6;

            for (Module module :
                    MyVisualsClient.moduleManager
                            .getModulesByCategory(category)) {

                boolean hovered = isMouseOver(
                        mouseX, mouseY,
                        x + 7, rowY,
                        WINDOW_WIDTH - 14, ROW_HEIGHT_VISIBLE
                );

                int cardColor;

                if (module.isEnabled()) {
                    cardColor = hovered
                            ? RenderUtils.getColor(0, 200, 255, 48)
                            : RenderUtils.getColor(0, 200, 255, 24);
                } else {
                    cardColor = hovered
                            ? RenderUtils.getColor(255, 255, 255, 25)
                            : RenderUtils.getColor(255, 255, 255, 9);
                }

                RenderUtils.drawRoundedRect(
                        context,
                        x + 7, rowY,
                        WINDOW_WIDTH - 14, ROW_HEIGHT_VISIBLE,
                        5, cardColor
                );

                // Индикатор включённого состояния
                if (module.isEnabled()) {
                    RenderUtils.drawRoundedRect(
                            context,
                            x + 11, rowY + 6,
                            3, 8,
                            2, ACCENT_COLOR
                    );
                }

                context.drawTextWithShadow(
                        textRenderer,
                        module.getName(),
                        x + 21,
                        rowY + 6,
                        module.isEnabled() ? 0xFFFFFFFF : 0xFF9AA4B2
                );

                if (module.isEnabled()) {
                    context.fill(
                            x + WINDOW_WIDTH - 18, rowY + 8,
                            x + WINDOW_WIDTH - 13, rowY + 13,
                            ACCENT_COLOR
                    );
                }

                rowY += ROW_HEIGHT;
            }

            // Подсказка внизу окна
            context.drawTextWithShadow(
                    textRenderer,
                    "ЛКМ — переключить",
                    x + 10,
                    y + height - 13,
                    0xFF687386
            );
        }

        private boolean isMouseOverHeader(double mouseX, double mouseY) {
            return mouseX >= x
                    && mouseX <= x + WINDOW_WIDTH
                    && mouseY >= y
                    && mouseY <= y + HEADER_HEIGHT;
        }

        private Module getModuleAt(double mouseX, double mouseY) {
            if (MyVisualsClient.moduleManager == null) {
                return null;
            }

            int rowY = y + HEADER_HEIGHT + 6;

            for (Module module :
                    MyVisualsClient.moduleManager
                            .getModulesByCategory(category)) {

                boolean insideRow =
                        mouseX >= x + 7
                                && mouseX <= x + WINDOW_WIDTH - 7
                                && mouseY >= rowY
                                && mouseY <= rowY + ROW_HEIGHT_VISIBLE;

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
}
