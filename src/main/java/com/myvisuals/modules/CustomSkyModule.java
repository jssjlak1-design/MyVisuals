package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;

public class CustomSkyModule extends Module {
    public CustomSkyModule() {
        super("CustomSky", "Вечная звёздная ночь и ясная погода", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;

        // Устанавливаем красивую глубокую ночь (18000 тиков) и убираем дождь
        mc.world.setTimeOfDay(18000L);
        mc.world.setRainGradient(0.0f);
        mc.world.setThunderGradient(0.0f);
    }
}
