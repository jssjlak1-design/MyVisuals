package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;

public class AutoSprintModule extends Module {
    public AutoSprintModule() {
        super("AutoSprint", "Автоматический бег при ходьбе вперёд", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }
    }
}
