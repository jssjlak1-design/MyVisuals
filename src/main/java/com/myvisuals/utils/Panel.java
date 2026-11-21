package com.myvisuals.gui;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import com.myvisuals.module.Setting;
import com.myvisuals.utils.Animation;
import com.myvisuals.utils.ColorUtils;
import com.myvisuals.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class Panel {
    private final Category category;
    public float x, y;
    public final float width = 120;
    public float height;
    private boolean dragging = false;
    private float dragX, dragY;
    private boolean collapsed = false;
    
    private final Animation expandAnimation = new Animation(1.0f, 0.3f);
    private final Animation hoverAnimation = new Animation(0.0f, 0.4f);
    
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Panel(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        TextRenderer tr = mc.textRenderer;
        List<Module> modules = ClickGuiScreen.getModules(category);
        
        // Обновляем анимации
        expandAnimation.setTarget(collapsed ? 0.0f : 1.0f);
        expandAnimation.update(delta);
        
        boolean headerHovered = isOverHeader(mouseX, mouseY);
        hoverAnimation.setTarget(headerHovered ? 1.0f : 0.0f);
        hoverAnimation.update(delta);
        
        // Целевая высота с анимацией
        float targetHeight = 24 + (modules.size() * 20) * expandAnimation.get();
        this.height = targetHeight;
        
        // Тень под панелью
        RenderUtils.drawRoundedRect(ctx, (int) x + 2, (int) y + 2, (int) width, (int) height, 6, 
            RenderUtils.getColor(0, 0, 0, 60));
        
        // Основной фон (стекло)
        int bgColor = RenderUtils.getColor(18, 18, 22, 230);
        RenderUtils.drawRoundedRect(ctx, (int) x, (int) y, (int) width, (int) height, 6, bgColor);
        
        // Заголовок с градиентом
        int headerColor = RenderUtils.getColor(25, 25, 30, 255);
        RenderUtils.drawRoundedRect(ctx, (int) x, (int) y, (int) width, 24, 6, headerColor);
        ctx.fill((int) x, (int) y + 18, (int) x + (int) width, (int) y + 24, headerColor);
        
        // Акцентная линия сверху (радуга)
        int rainbow = ColorUtils.getRainbow(0.5f, category.ordinal() * 1000, 0.7f, 1.0f);
        ctx.fill((int) x + 6, (int) y + 2, (int) x + (int) width - 6, (int) y + 3, rainbow);
        
        // Hover эффект на заголовке
        if (hoverAnimation.get() > 0.01f) {
            int hoverColor = ColorUtils.withAlpha(RenderUtils.getColor(255, 255, 255, 20), 
                (int) (20 * hoverAnimation.get()));
            RenderUtils.drawRoundedRect(ctx, (int) x, (int) y, (int) width, 24, 6, hoverColor);
        }
        
        // Название категории
        ctx.drawTextWithShadow(tr, category.getName(), (int) x + 8, (int) y + 8, 0xFFFFFFFF);
        
        // Стрелка collapse
        String arrow = collapsed ? "+" : "−";
        ctx.drawTextWithShadow(tr, arrow, (int) x + (int) width - 14, (int) y + 8, 0xFFAAAAAA);
        
        // Модули (только если не свернуто)
        if (expandAnimation.get() > 0.01f) {
            int currentY = (int) y + 26;
            for (Module module : modules) {
                renderModule(ctx, module, mouseX, mouseY, currentY, delta);
                currentY += 20;
            }
        }
    }

    private void renderModule(DrawContext ctx, Module module, int mouseX, int mouseY, int y, float delta) {
        TextRenderer tr = mc.textRenderer;
        boolean hovered = (mouseX >= x + 4 && mouseX <= x + width - 4 && 
                           mouseY >= y && mouseY <= y + 18);
        
        // Фон модуля
        if (hovered) {
            RenderUtils.drawRoundedRect(ctx, (int) x + 4, y, (int) width - 8, 18, 3, 
                RenderUtils.getColor(255, 255, 255, 15));
        }
        
        // Индикатор включения (анимированный)
        if (module.isEnabled()) {
            int accentColor = ColorUtils.getRainbow(0.3f, 0, 0.8f, 1.0f);
            ctx.fill((int) x + 6, y + 4, (int) x + 8, y + 14, accentColor);
        }
        
        // Название модуля
        int textColor = module.isEnabled() ? 0xFFFFFFFF : 0xFF888888;
        ctx.drawTextWithShadow(tr, module.getName(), (int) x + 14, y + 5, textColor);
        
        // Галочка справа
        if (module.isEnabled()) {
            ctx.drawTextWithShadow(tr, "✓", (int) x + (int) width - 14, y + 5, 0xFF55FF55);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isOverHeader(mouseX, mouseY)) {
                if (hasShiftDown()) {
                    dragging = true;
                    dragX = (float) (mouseX - x);
                    dragY = (float) (mouseY - y);
                } else {
                    collapsed = !collapsed;
                }
                return true;
            }
            
            if (!collapsed && isOverModules(mouseX, mouseY)) {
                Module clicked = getModuleAt(mouseX, mouseY);
                if (clicked != null) {
                    clicked.toggle();
                    return true;
                }
            }
        } else if (button == 1) {
            // ПКМ по модулю — открыть настройки (заглушка)
            if (!collapsed && isOverModules(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void mouseDragged(double mouseX, double mouseY) {
        if (dragging) {
            x = (float) (mouseX - dragX);
            y = (float) (mouseY - dragY);
        }
    }

    private boolean isOverHeader(double mx, double my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + 24;
    }

    private boolean isOverModules(double mx, double my) {
        return mx >= x && mx <= x + width && my >= y + 24 && my <= y + height;
    }

    private Module getModuleAt(double mx, double my) {
        if (!isOverModules(mx, my)) return null;
        int currentY = (int) y + 26;
        for (Module module : ClickGuiScreen.getModules(category)) {
            if (my >= currentY && my <= currentY + 18) {
                return module;
            }
            currentY += 20;
        }
        return null;
    }

    private boolean hasShiftDown() {
        return net.minecraft.client.util.InputUtil.isKeyPressed(
            mc.getWindow().getHandle(), 
            net.minecraft.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
        ) || net.minecraft.client.util.InputUtil.isKeyPressed(
            mc.getWindow().getHandle(), 
            net.minecraft.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
        );
    }
}
