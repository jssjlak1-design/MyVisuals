private void render(DrawContext context, int mouseX, int mouseY) {
    int height = getHeight();

    // Основная карточка окна
    RenderUtils.drawRoundedRect(
            context, x, y, WINDOW_WIDTH, height, 9,
            RenderUtils.getColor(13, 17, 25, 230)
    );

    // Верхняя цветная полоса и разделитель заголовка
    context.fill(x + 10, y, x + WINDOW_WIDTH - 10, y + 2, ACCENT_COLOR);
    context.fill(
            x + 10, y + HEADER_HEIGHT,
            x + WINDOW_WIDTH - 10, y + HEADER_HEIGHT + 1,
            RenderUtils.getColor(255, 255, 255, 28)
    );

    // Цветная точка рядом с названием категории
    context.fill(x + 12, y + 11, x + 17, y + 16, category.getColor());

    context.drawTextWithShadow(
            textRenderer,
            category.getName(),
            x + 23,
            y + 8,
            0xFFFFFFFF
    );

    // Количество модулей в категории
    int moduleCount =
            MyVisualsClient.moduleManager.getModulesByCategory(category).size();

    String countText = String.valueOf(moduleCount);
    int countWidth = textRenderer.getWidth(countText);

    RenderUtils.drawRoundedRect(
            context,
            x + WINDOW_WIDTH - countWidth - 22,
            y + 6,
            countWidth + 12,
            15,
            5,
            RenderUtils.getColor(255, 255, 255, 20)
    );

    context.drawTextWithShadow(
            textRenderer,
            countText,
            x + WINDOW_WIDTH - countWidth - 16,
            y + 10,
            0xFFB8C4D8
    );

    // Карточки модулей
    int rowY = y + HEADER_HEIGHT + 7;

    for (Module module :
            MyVisualsClient.moduleManager.getModulesByCategory(category)) {

        boolean hovered = isMouseOver(
                mouseX, mouseY,
                x + 7, rowY,
                WINDOW_WIDTH - 14, ROW_CONTENT_HEIGHT
        );

        int cardColor;
        if (module.isEnabled()) {
            cardColor = hovered
                    ? RenderUtils.getColor(0, 200, 255, 45)
                    : RenderUtils.getColor(0, 200, 255, 24);
        } else {
            cardColor = hovered
                    ? RenderUtils.getColor(255, 255, 255, 24)
                    : RenderUtils.getColor(255, 255, 255, 8);
        }

        RenderUtils.drawRoundedRect(
                context,
                x + 7, rowY,
                WINDOW_WIDTH - 14, ROW_CONTENT_HEIGHT,
                5, cardColor
        );

        // Индикатор состояния
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

        // Маленький маркер включённого модуля
        if (module.isEnabled()) {
            context.fill(
                    x + WINDOW_WIDTH - 18, rowY + 8,
                    x + WINDOW_WIDTH - 13, rowY + 13,
                    ACCENT_COLOR
            );
        }

        rowY += ROW_HEIGHT;
    }

    // Нижняя подсказка окна
    int footerY = y + height - 13;
    context.drawTextWithShadow(
            textRenderer,
            "ЛКМ — переключить",
            x + 10,
            footerY,
            0xFF687386
    );
}
