package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBrightModule extends Module {
    public FullBrightModule() {
        super("FullBright", "Яркое ночное зрение без темноты", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
