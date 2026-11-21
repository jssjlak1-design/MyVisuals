package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.ChinaHatModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;
    private int themeIndex = 0;
    private final String[] themes = {"Тёмная", "Фиолетовая", "Рубин"};

    public ClickGuiScreen() {
        super(Text.literal("MyVisuals GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 120).getRGB());

        int guiW = Math.min(420, this.width - 20);
        int guiH = Math.min(245, this.height - 20);
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;

        Color accent = themeIndex == 0
                ? new Color(225, 65, 65, 255)
                : (themeIndex == 1 ? new Color(145, 80, 255, 255) : new Color(235, 75, 135, 255));

        drawRoundedRect(context, x - 1, y - 1, guiW + 2, guiH + 2, 9, new Color(255, 255, 255, 36).getRGB());
        drawRoundedRect(context, x, y, guiW, guiH, 9, new Color(25, 26, 32, 235).getRGB());

        int sideW = 96;
        drawRoundedRect(context, x + 4, y + 4, sideW - 4, guiH - 8, 7, new Color(19, 20, 25, 225).getRGB());

        context.drawTextWithShadow(this.textRenderer, "§lMy Visuals", x + 12, y + 13, 0xFFF2F2F7);
        context.fill(x + 12, y + 26, x + sideW - 8, y + 27, new Color(255, 255, 255, 20).getRGB());

        int catY = y + 34;
        for (Category category : Category.values()) {
            boolean selected = category == selectedCategory;
            if (selected) {
                drawRoundedRect(context, x + 8, catY, sideW - 12, 20, 5, new Color(255, 255, 255, 22).getRGB());
                drawRoundedRect(context, x + 8, catY + 4, 2, 12, 1, accent.getRGB());
            }
            context.drawTextWithShadow(this.textRenderer, category.getName(), x + 16, catY + 6, selected ? 0xFFFFFFFF : 0xFF9EA2B2);
            catY += 23;
        }

        int profY = y + guiH - 30;
        drawRoundedRect(context, x + 8, profY, sideW - 12, 22, 5, new Color(255, 255, 255, 14).getRGB());
        drawRoundedRect(context, x + 11, profY + 4, 14, 14, 3, accent.getRGB());
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        if (nick.length() > 9) nick = nick.substring(0, 9);
        context.drawTextWithShadow(this.textRenderer, nick, x + 28, profY + 3, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "User", x + 28, profY + 12, 0xFF7E8292);

        int contentX = x + sideW + 8;
        int contentW = guiW - sideW - 14;
        drawRoundedRect(context, contentX, y + 6, contentW, 22, 5, new Color(255, 255, 255, 12).getRGB());
        context.drawTextWithShadow(this.textRenderer, "§7Модули › §f" + selectedCategory.getName(), contentX + 8, y + 13, 0xFFFFFFFF);

        String themeLabel = themes[themeIndex];
        int tBtnW = this.textRenderer.getWidth(themeLabel) + 12;
        int tBtnX = contentX + contentW - tBtnW - 4;
        drawRoundedRect(context, tBtnX, y + 9, tBtnW, 16, 4, new Color(255, 255, 255, 20).getRGB());
        context.drawTextWithShadow(this.textRenderer, themeLabel, tBtnX + 6, y + 13, 0xFFDCDCE6);

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int colW = (contentW - 6) / 2;
        int leftY = y + 34;
        int rightY = y + 34;

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            boolean isLeft = (i % 2 == 0);
            int cardX = isLeft ? contentX : contentX + colW + 6;
            int cardY = isLeft ? leftY : rightY;
            boolean isExpanded = (expandedModule == mod);
            int cardH = isExpanded ? 52 : 26;

            int bg = mod.isEnabled() ? new Color(40, 41, 52, 235).getRGB() : new Color(30, 31, 39, 220).getRGB();
            drawRoundedRect(context, cardX, cardY, colW, cardH, 5, bg);
            if (mod.isEnabled()) {
                drawRoundedRect(context, cardX, cardY + 5, 2, 16, 1, accent.getRGB());
            }

            String name = mod.getName();
            if (name.length() > 11) name = name.substring(0, 11);
            context.drawTextWithShadow(this.textRenderer, name, cardX + 7, cardY + 9, mod.isEnabled() ? 0xFFFFFFFF : 0xFFB8BCCB);

            int gearX = cardX + colW - 44;
            drawRoundedRect(context, gearX, cardY + 6, 13, 13, 3, new Color(255, 255, 255, isExpanded ? 38 : 16).getRGB());
            context.drawTextWithShadow(this.textRenderer, "⚙", gearX + 3, cardY + 9, 0xFFCCCCDD);

            int swX = cardX + colW - 28;
            int swY = cardY + 7;
            drawRoundedRect(context, swX, swY, 22, 11, 5, mod.isEnabled() ? accent.getRGB() : new Color(55, 57, 68, 255).getRGB());
            drawRoundedRect(context, mod.isEnabled() ? swX + 13 : swX + 2, swY + 2, 7, 7, 3, 0xFFFFFFFF);

            if (isExpanded) {
                context.fill(cardX + 6, cardY + 25, cardX + colW - 6, cardY + 26, new Color(255, 255, 255, 16).getRGB());
                if (mod instanceof ChinaHatModule hat) {
                    drawRoundedRect(context, cardX + 6, cardY + 29, colW - 12, 18, 4, new Color(255, 255, 255, 18).getRGB());
                    context.drawTextWithShadow(this.textRenderer, "§7Цвет: §f" + hat.getCurrentColorName(), cardX + 10, cardY + 34, 0xFFFFFFFF);
                    drawRoundedRect(context, cardX + colW - 21, cardY + 32, 11, 11, 3, hat.getCurrentColor().getRGB());
                } else {
                    String desc = mod.getDescription();
                    if (desc.length() > 20) desc = desc.substring(0, 20) + "..";
                    context.drawTextWithShadow(this.textRenderer, desc, cardX + 7, cardY + 34, 0xFF8C90A0);
                }
            }

            if (isLeft) leftY += cardH + 5;
            else rightY += cardH + 5;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = Math.min(420, this.width - 20);
        int guiH = Math.min(245, this.height - 20);
        int x = (this.width - guiW) / 2;
        int y = (this.height - guiH) / 2;
        int sideW = 96;

        int catY = y + 34;
        for (Category category : Category.values()) {
            if (mouseX >= x + 8 && mouseX <= x + sideW - 4 && mouseY >= catY && mouseY <= catY + 20) {
                selectedCategory = category;
                expandedModule = null;
                return true;
            }
            catY += 23;
        }

        int contentX = x + sideW + 8;
        int contentW = guiW - sideW - 14;

        String themeLabel = themes[themeIndex];
        int tBtnW = this.textRenderer.getWidth(themeLabel) + 12;
        int tBtnX = contentX + contentW - tBtnW - 4;
        if (mouseX >= tBtnX && mouseX <= tBtnX + tBtnW && mouseY >= y + 9 && mouseY <= y + 25) {
            themeIndex = (themeIndex + 1) % themes.length;
            return true;
        }

        List<Module> modules = MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory);
        int colW = (contentW - 6) / 2;
        int leftY = y + 34;
        int rightY = y + 34;

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            boolean isLeft = (i % 2 == 0);
            int cardX = isLeft ? contentX : contentX + colW + 6;
            int cardY = isLeft ? leftY : rightY;
            boolean isExpanded = (expandedModule == mod);
            int cardH = isExpanded ? 52 : 26;

            if (isExpanded && mod instanceof ChinaHatModule hat) {
                if (mouseX >= cardX + 6 && mouseX <= cardX + colW - 6 && mouseY >= cardY + 29 && mouseY <= cardY + 47) {
                    hat.nextColor();
                    return true;
                }
            }

            if (mouseX >= cardX && mouseX <= cardX + colW && mouseY >= cardY && mouseY <= cardY + 26) {
                int gearX = cardX + colW - 44;
                boolean clickedGear = mouseX >= gearX && mouseX <= gearX + 15;
                if (button == 2 || button == 1 || clickedGear) {
                    expandedModule = isExpanded ? null : mod;
                } else if (button == 0) {
                    mod.toggle();
                }
                return true;
            }

            if (isLeft) leftY += cardH + 5;
            else rightY += cardH + 5;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        int radius = Math.min(r, Math.min(w / 2, h / 2));
        ctx.fill(x + radius, y, x + w - radius, y + h, color);
        ctx.fill(x, y + radius, x + radius, y + h - radius, color);
        ctx.fill(x + w - radius, y + radius, x + w, y + h - radius, color);

        for (int i = 0; i < radius; i++) {
            int offset = radius - (int) Math.round(Math.sqrt(radius * radius - (radius - i) * (radius - i)));
            ctx.fill(x + offset, y + i, x + radius, y + i + 1, color);
            ctx.fill(x + w - radius, y + i, x + w - offset, y + i + 1, color);
            ctx.fill(x + offset, y + h - i - 1, x + radius, y + h - i, color);
            ctx.fill(x + w - radius, y + h - i - 1, x + w - offset, y + h - i, color);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    
