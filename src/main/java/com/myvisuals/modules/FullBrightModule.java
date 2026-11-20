package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;

public class FullBrightModule extends Module {
    private double previousGamma = 1.0;

    public FullBrightModule() {
        super("FullBright", "Светло в пещерах и ночью", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            previousGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(16.0);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(previousGamma);
        }
    }

    @Override
    public void onTick() {
        if (mc.options != null && mc.options.getGamma().getValue() < 16.0) {
            mc.options.getGamma().setValue(16.0);
        }
    }
}
