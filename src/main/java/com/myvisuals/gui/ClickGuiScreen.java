package com.myvisuals.gui;

import com.myvisuals.MyVisualsClient;
import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.modules.ChinaHatModule;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.modules.JumpCirclesModule;
import com.myvisuals.modules.TargetEspModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.awt.Color;

public class ClickGuiScreen extends Screen {
    private Category selectedCategory = Category.RENDER;
    private Module expandedModule = null;

    public ClickGuiScreen() { super(Text.literal("MyVisuals GUI")); }

    private boolean inBox(double mx, double my, int x, int y, int w, int h) {
        return Float.compare((float) mx, x) != -1 && Float.compare((float) mx, x + w) != 1 && Float.compare((float) my, y) != -1 && Float.compare((float) my, y + h) != 1;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, new Color(8, 12, 18, 110).getRGB());
        int guiW = Math.min(360, this.width - 24), guiH = Math.min(240, this.height - 24);
        int x = (this.width - guiW) / 2, y = (this.height - guiH) / 2, sideW = 88;
        Color accent = new Color(115, 175, 255);

        // Внешняя стеклянная рамка и полупрозрачный фон (как Plintus Visuals)
        rect(ctx, x - 1, y - 1, guiW + 2, guiH + 2, 10, new Color(170, 205, 240, 55).getRGB());
        rect(ctx, x, y, guiW, guiH, 10, new Color(24, 30, 40, 195).getRGB());
        rect(ctx, x + 4, y + 4, sideW - 4, guiH - 8, 8, new Color(18, 22, 30, 185).getRGB());

        // Крупный логотип по центру шапки левой панели
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + sideW / 2, y + 14, 0);
        ctx.getMatrices().scale(1.6f, 1.6f, 1.0f);
        ctx.drawTextWithShadow(this.textRenderer, "§lM", -4, 0, 0xFFFFFFFF);
        ctx.getMatrices().pop();
        ctx.drawTextWithShadow(this.textRenderer, "§7MyVisuals", x + 22, y + 32, 0xFF9CB0C8);

        int catY = y + 50;
        for (Category cat : Category.values()) {
            boolean sel = cat == selectedCategory;
            if (sel) rect(ctx, x + 8, catY, sideW - 12, 20, 6, new Color(115, 175, 255, 50).getRGB());
            ctx.drawTextWithShadow(this.textRenderer, cat.getName(), x + 16, catY + 6, sel ? 0xFFFFFFFF : 0xFF8E9EB2);
            catY += 23;
        }

        int profY = y + guiH - 28;
        rect(ctx, x + 8, profY, sideW - 12, 20, 6, new Color(255, 255, 255, 16).getRGB());
        String nick = (this.client != null && this.client.getSession() != null) ? this.client.getSession().getUsername() : "Player";
        if (Integer.compare(nick.length(), 10) == 1) nick = nick.substring(0, 10);
        ctx.drawTextWithShadow(this.textRenderer, "§b● §f" + nick, x + 14, profY + 6, 0xFFFFFFFF);

        // Правая колонка модулей (1 колонка с названием, описанием и свитчем, как на 4-м фото)
        int cX = x + sideW + 8, cW = guiW - sideW - 14;
        ctx.drawTextWithShadow(this.textRenderer, "§l" + selectedCategory.getName(), cX + 4, y + 10, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "§7Настрой визуалы под себя", cX + 4, y + 20, 0xFF8899B0);

        int mY = y + 34;
        for (Object obj : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            Module mod = (Module) obj;
            boolean exp = (expandedModule == mod);
            int mH = exp ? 48 : 26;
            if (Integer.compare(mY + mH, y + guiH - 4) == 1) break;

            rect(ctx, cX, mY, cW, mH, 6, mod.isEnabled() ? new Color(42, 52, 68, 200).getRGB() : new Color(30, 36, 48, 175).getRGB());
            ctx.drawTextWithShadow(this.textRenderer, mod.getName(), cX + 8, mY + 5, mod.isEnabled() ? 0xFFFFFFFF : 0xFFC5D0E0);
            String desc = mod.getDescription();
            if (Integer.compare(desc.length(), 28) == 1) desc = desc.substring(0, 28) + "..";
            ctx.drawTextWithShadow(this.textRenderer, desc, cX + 8, mY + 15, 0xFF7D8DA4);

            // Шестерёнка и тумблер справа
            int gX = cX + cW - 44, swX = cX + cW - 28;
            rect(ctx, gX, mY + 6, 13, 13, 4, new Color(255, 255, 255, exp ? 45 : 18).getRGB());
            ctx.drawTextWithShadow(this.textRenderer, "⚙", gX + 3, mY + 9, 0xFFD0DCF0);
            rect(ctx, swX, mY + 7, 22, 11, 5, mod.isEnabled() ? accent.getRGB() : new Color(60, 70, 88).getRGB());
            rect(ctx, mod.isEnabled() ? swX + 13 : swX + 2, mY + 9, 7, 7, 3, 0xFFFFFFFF);

            if (exp) {
                String cName = null;
                int cRgb = 0;
                if (mod instanceof ChinaHatModule h) { cName = h.getCurrentColorName(); cRgb = h.getCurrentColor().getRGB(); }
                else if (mod instanceof JumpCirclesModule j) { cName = j.getCurrentColorName(); cRgb = j.getCurrentColor().getRGB(); }
                else if (mod instanceof TargetEspModule t) { cName = t.getCurrentColorName(); cRgb = t.getCurrentColor().getRGB(); }

                if (cName != null) {
                    rect(ctx, cX + 6, mY + 26, cW - 12, 18, 4, new Color(255, 255, 255, 20).getRGB());
                    ctx.drawTextWithShadow(this.textRenderer, "§7Цвет: §f" + cName, cX + 10, mY + 31, 0xFFFFFFFF);
                    rect(ctx, cX + cW - 21, mY + 29, 11, 11, 3, cRgb);
                } else if (mod instanceof GuiSoundsModule gs) {
                    rect(ctx, cX + 6, mY + 26, cW - 12, 18, 4, new Color(255, 255, 255, 20).getRGB());
                    ctx.drawTextWithShadow(this.textRenderer, "§7Звук: §f" + gs.getCurrentSoundName(), cX + 10, mY + 31, 0xFFFFFFFF);
                }
            }
            mY += mH + 4;
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiW = Math.min(360, this.width - 24), guiH = Math.min(240, this.height - 24);
        int x = (this.width - guiW) / 2, y = (this.height - guiH) / 2, sideW = 88, catY = y + 50;
        GuiSoundsModule sounds = MyVisualsClient.moduleManager.getGuiSounds();

        for (Category cat : Category.values()) {
            if (inBox(mouseX, mouseY, x + 8, catY, sideW - 12, 20)) {
                selectedCategory = cat; expandedModule = null;
                if (sounds != null) sounds.playToggleSound(true);
                return true;
            }
            catY += 23;
        }

        int cX = x + sideW + 8, cW = guiW - sideW - 14, mY = y + 34;
        for (Object obj : MyVisualsClient.moduleManager.getModulesByCategory(selectedCategory)) {
            Module mod = (Module) obj;
            boolean exp = (expandedModule == mod);
            int mH = exp ? 48 : 26;
            if (Integer.compare(mY + mH, y + guiH - 4) == 1) break;

            if (exp && inBox(mouseX, mouseY, cX + 6, mY + 26, cW - 12, 18)) {
                if (mod instanceof ChinaHatModule h) { h.nextColor(); if (sounds != null) sounds.playToggleSound(true); return true; }
                if (mod instanceof JumpCirclesModule j) { j.nextColor(); if (sounds != null) sounds.playToggleSound(true); return true; }
                if (mod instanceof TargetEspModule t) { t.nextColor(); if (sounds != null) sounds.playToggleSound(true); return true; }
                if (mod instanceof GuiSoundsModule gs) { gs.nextSound(); return true; }
            }
            if (inBox(mouseX, mouseY, cX, mY, cW, 26)) {
                if (button == 2 || button == 1 || inBox(mouseX, mouseY, cX + cW - 44, mY, 16, 26)) {
                    expandedModule = exp ? null : mod;
                    if (sounds != null) sounds.playToggleSound(true);
                } else if (button == 0) {
                    mod.toggle();
                    if (sounds != null) sounds.playToggleSound(mod.isEnabled());
                }
                return true;
            }
            mY += mH + 4;
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
