package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WatermarkHudModule extends Module {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public WatermarkHudModule() {
        super("WatermarkHUD", "Двойная стеклянная ватермарка в стиле Plintus", Category.RENDER);
        setEnabled(true);
        HudRenderCallback.EVENT.register((ctx, delta) -> {
            if (!isEnabled() || mc.player == null || mc.options.hudHidden) return;
            renderHud(ctx);
        });
    }

    private void renderHud(DrawContext ctx) {
        String nick = mc.getSession() != null ? mc.getSession().getUsername() : "Player";
        int fps = mc.getCurrentFps();
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) ping = e.getLatency();
        }

        String line1 = "§b§lM §f§lMyVisuals §8| §7" + nick + " §8| §f" + fps + " §7fps §8| §f" + ping + " §7ms";
        int w1 = mc.textRenderer.getWidth(line1) + 14;
        rect(ctx, 5, 5, w1 + 2, 17, 5, new Color(150, 200, 220, 45).getRGB());
        rect(ctx, 6, 6, w1, 15, 5, new Color(22, 28, 36, 185).getRGB());
        ctx.drawTextWithShadow(mc.textRenderer, line1, 12, 10, 0xFFFFFFFF);

        int px = (int) mc.player.getX(), py = (int) mc.player.getY(), pz = (int) mc.player.getZ();
        double dx = mc.player.getX() - mc.player.prevX, dz = mc.player.getZ() - mc.player.prevZ;
        String bps = String.format("%.1f", Math.sqrt(dx * dx + dz * dz) * 20.0);
        String line2 = "§7XYZ: §f" + px + " " + py + " " + pz + " §8| §7BPS: §f" + bps + " §8| §f" + LocalTime.now().format(FMT);
        int w2 = mc.textRenderer.getWidth(line2) + 14;
        rect(ctx, 5, 24, w2 + 2, 16, 5, new Color(150, 200, 220, 40).getRGB());
        rect(ctx, 6, 25, w2, 14, 5, new Color(22, 28, 36, 175).getRGB());
        ctx.drawTextWithShadow(mc.textRenderer, line2, 12, 28, 0xFFFFFFFF);
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
}
