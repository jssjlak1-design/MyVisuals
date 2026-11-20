package com.myvisuals.module;

import com.myvisuals.modules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat (Боевые визуалы)
        modules.add(new TargetEspModule());
        modules.add(new KillEffectsModule());
        modules.add(new HitParticlesModule());

        // Render (Визуалы игрока и экрана)
        modules.add(new ChinaHatModule());
        modules.add(new JumpCirclesModule());
        modules.add(new FullBrightModule());

        // World (Мир и небо)
        modules.add(new CustomSkyModule());

        // Misc (Разное и HUD)
        modules.add(new WatermarkHudModule());
        modules.add(new AutoSprintModule());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}

