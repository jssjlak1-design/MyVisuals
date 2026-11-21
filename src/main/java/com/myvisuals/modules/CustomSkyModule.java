package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;

public class CustomSkyModule extends Module {
    private boolean isApplied = false;

    public CustomSkyModule() {
        super("CustomSky", "Вечная звёздная ночь и ясная погода", Category.WORLD);
    }

    @Override
    public void onEnable() {
        applySky();
    }

    @Override
    public void onTick() {
        // Применяем только если игрок зашел в новый мир или настройки сбросились
        if (mc.world != null && !isApplied) {
            applySky();
        }
    }

    @Override
    public void onDisable() {
        isApplied = false; // При выключении позволяем миру вернуться к норме (или можно сделать сброс)
    }

    private void applySky() {
        if (mc.world == null) return;
        // Устанавливаем ночь и убираем дождь только один раз
        mc.world.setTimeOfDay(18000L);
        mc.world.setRainGradient(0.0f);
        mc.world.setThunderGradient(0.0f);
        isApplied = true;
    }
}
