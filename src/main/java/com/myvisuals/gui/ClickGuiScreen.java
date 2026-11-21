package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.ChinaHatModule;
import com.myvisuals.modules.JumpCirclesModule;
import com.myvisuals.modules.TargetEspModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.awt.Color;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;
    private int themeIndex = 0;

    public ClickGuiScreen() { super(Text.literal("MyVisuals GUI")); }

    private String getTheme() { return themeIndex == 1 ? "Фиолетовая" : (themeIndex == 2 ? "Рубин" : "Тёмная"); }

    private boolean inBox(double mx, double my, int x, int y, int w, int h) {
        return Float.compare((float) mx, x) != -1 && Float.compare((float) mx, x + w) != 1 && Float.compare((float) my, y) != -1 && Float.compare((float) my, y + h) != 1;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 120).getRGB());
        int guiW = Math.min(420, this.width - 20), guiH = Math.min(245, this.height - 20);
        int x = (this.width - guiW) / 2, y = (this.height - guiH) / 2, sideW = 96;
        Color accent = themeIndex == 1 ? new Color(145, 80, 255) : (themeIndex == 2 ? new Color(235, 75, 135) : new Color(225, 65, 65));

        rect(ctx, x - 1, y - 1, guiW + 2, guiH + 2, 9, new Color(255, 255, 255, 36).getRGB());
        rect(ctx, x, y, guiW, guiH, 9, new Color(25, 26, 32, 235).getRGB());
        rect(ctx, x + 4, y + 4, sideW - 4, guiH - 8, 7, new Color(19, 20, 25, 225).getRGB());

        ctx.drawTextWithShadow(this.textRenderer, "§lMy Visuals", x + 12, y + 13, 0xFFF2F2F7);
        ctx.fill(x + 12, y + 26, x + sideW - 8, y + 27, new Color(255, 255, 255, 20).getRGB());

        int catY = y + 34;
        for (Category cat : Category.values()) {
            boolean sel = cat == selectedCategory;
            if (sel) { rect(ctx, x + 8, catY, sideW - 12, 20, 5, new Color(255, 255, 255, 22).getRGB()); rect(ctx, x + 8, catY + 4, 2, 12, 1, accent.getRGB()); }
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), x + 16, catY + 6, sel ? 0xFFFFFFFF : 0xFF9EA2B2);
            catY += 23;
        }

        int profY = y + guiH - 30;
        rect(ctx, x + 8, profY, sideW - 12, 22, 5, new Color(255, 255, 255, 14).getRGB());
        rect(ctx, x + 11, profY + 4, 14, 14, 3, accent.getRGB());
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        if (Integer.compare(nick.length(), 9) == 1) nick = nick.substring(0, 9);
        ctx.drawTextWithShadow(this.textRenderer, nick, x + 28, profY + 3, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "User", x + 28, profY + 12, 0xFF7E8292);

        int contentX = x + sideW + 8, contentW = guiW - sideW - 14;
        rect(ctx, contentX, y + 6, contentW, 22, 5, new Color(255, 255, 255, 12).getRGB());
        ctx.drawTextWithShadow(this.textRenderer, "§7Модули › §f" + selectedCategory.getName(), contentX + 8, y + 13, 0xFFFFFFFF);

        String tLbl = getTheme();
        int tW = this.textRenderer.getWidth(tLbl) + 12, tX = contentX + contentW - tW - 4;
        rect(ctx, tX, y + 9, tW, 16, 4, new Color(255, 255, 255, 20).getRGB());
        ctx.drawTextWithShadow(this.textRenderer, tLbl, tX + 6, y + 13, 0xFFDCDCE6);

        int colW = (contentW - 6) / 2, leftY = y + 34, rightY = y + 34, idx = 0;
        for (Module mod : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean isLeft = (idx % 2 == 0), exp = (expandedModule == mod);
            int cX = isLeft ? contentX : contentX + colW + 6, cY = isLeft ? leftY : rightY, cH = exp ? 52 : 26;
            rect(ctx, cX, cY, colW, cH, 5, mod.isEnabled() ? new Color(40, 41, 52, 235).getRGB() : new Color(30, 31, 39, 220).getRGB());
            if (mod.isEnabled()) rect(ctx, cX, cY + 5, 2, 16, 1, accent.getRGB());

            String name = mod.getName();
            if (Integer.compare(name.length(), 11) == 1) name = name.substring(0, 11);
            ctx.drawTextWithShadow(this.textRenderer, name, cX + 7, cY + 9, mod.isEnabled() ? 0xFFFFFFFF : 0xFFB8BCCB);

            int gX = cX + colW - 44, swX = cX + colW - 28;
            rect(ctx, gX, cY + 6, 13, 13, 3, new Color(255, 255, 255, exp ? 38 : 16).getRGB());
            ctx.drawTextWithShadow(this.textRenderer, "⚙", gX + 3, cY + 9, 0xFFCCCCDD);
            rect(ctx, swX, cY + 7, 22, 11, 5, mod.isEnabled() ? accent.getRGB() : new Color(55, 57, 68).getRGB());
            rect(ctx, mod.isEnabled() ? swX + 13 : swX + 2, cY + 9, 7, 7, 3, 0xFFFFFFFF);

            if (exp) {
                ctx.fill(cX + 6, cY + 25, cX + colW - 6, cY + 26, new Color(255, 255, 255, 16).getRGB());
                String cName = null;
                int cRgb = 0;
                if (mod instanceof ChinaHatModule h) { cName = h.getCurrentColorName(); cRgb = h.getCurrentColor().getRGB(); }
                else if (mod instanceof JumpCirclesModule j) { cName = j.getCurrentColorName(); cRgb = j.getCurrentColor().getRGB(); }
                else if (mod instanceof TargetEspModule t) { cName = t.getCurrentColorName(); cRgb = t.getCurrentColor().getRGB(); }

                if (cName != null) {
                    rect(ctx, cX + 6, cY + 29, colW - 12, 18, 4, new Color(255, 255, 255, 18).getRGB());
                    ctx.drawTextWithShadow(this.textRenderer, "§7Цвет: §f" + cName, cX + 10, cY + 34, 0xFFFFFFFF);
                    rect(ctx, cX + colW - 21, cY + 32, 11, 11, 3, cRgb);
                } else {
                    String desc = mod.getDescription();
                    if (Integer.compare(desc.length(), 20) == 1) desc = desc.substring(0, 20) + "..";
                    ctx.drawTextWithShadow(this.textRenderer, desc, cX + 7, cY + 34, 0xFF8C90A0);
                }
            }
            if (isLeft) leftY += cH + 5; else rightY += cH + 5;
            idx++;
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = Math.min(420, this.width - 20), guiH = Math.min(245, this.height - 20);
        int x = (this.width - guiW) / 2, y = (this.height - guiH) / 2, sideW = 96, catY = y + 34;
        for (Category cat : Category.values()) {
            if (inBox(mouseX, mouseY, x + 8, catY, sideW - 12, 20)) { selectedCategory = cat; expandedModule = null; return true; }
            catY += 23;
        }
        int contentX = x + sideW + 8, contentW = guiW - sideW - 14;
        int tW = this.textRenderer.getWidth(getTheme()) + 12, tX = contentX + contentW - tW - 4;
        if (inBox(mouseX, mouseY, tX, y + 9, tW, 16)) { themeIndex = (themeIndex + 1) % 3; return true; }

        int colW = (contentW - 6) / 2, leftY = y + 34, rightY = y + 34, idx = 0;
        for (Module mod : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean isLeft = (idx % 2 == 0), exp = (expandedModule == mod);
            int cX = isLeft ? contentX : contentX + colW + 6, cY = isLeft ? leftY : rightY, cH = exp ? 52 : 26;
            if (exp && inBox(mouseX, mouseY, cX + 6, cY + 29, colW - 12, 18)) {
                if (mod instanceof ChinaHatModule h) { h.nextColor(); return true; }
                if (mod instanceof JumpCirclesModule j) { j.nextColor(); return true; }
                if (mod instanceof TargetEspModule t) { t.nextColor(); return true; }
            }
            if (inBox(mouseX, mouseY, cX, cY, colW, 26)) {
                if (button == 2 || button == 1 || inBox(mouseX, mouseY, cX + colW - 44, cY, 16, 26)) expandedModule = exp ? null : mod;
                else if (button == 0) mod.toggle();
                return true;
            }
            if (isLeft) leftY += cH + 5; else rightY += cH + 5;
            idx++;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void rect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (Integer.compare(w, 1) == -1 || Integer.compare(h, 1) == -1) return;
        int rad = Math.min(r, Math.min(w / 2, h / 2));
        ctx.fill(x + rad, y, x + w - rad, y + h, color); ctx.fill(x, y + rad, x + rad, y + h - rad, color); ctx.fill(x + w - rad, y + rad, x + w, y + h - rad, color);
        for (int i = 0; i != rad; i++) {
            int off = rad - (int) Math.round(Math.sqrt(rad * rad - (rad - i) * (rad - i)));
            ctx.fill(x + off, y + i, x + rad, y + i + 1, color); ctx.fill(x + w - rad, y + i, x + w - off, y + i + 1, color);
            ctx.fill(x + off, y + h - i - 1, x + rad, y + h - i, color); ctx.fill(x + w - rad, y + h - i - 1, x + w - off, y + h - i, color);
        }
    }

    @Override
    public boolean shouldPause() { return false; }
}
