package com.myvisuals.module;

import com.myvisuals.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    // Добавляем <Module> - это делает код безопасным и убирает необходимость в (Module) obj
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new TargetEspModule());
        modules.add(new KillEffectsModule());
        modules.add(new HitParticlesModule());
        modules.add(new ChinaHatModule());
        modules.add(new JumpCirclesModule());
        modules.add(new FullBrightModule());
        modules.add(new WatermarkHudModule());
        modules.add(new CustomSkyModule());
        modules.add(new AutoSprintModule());
        modules.add(new GuiSoundsModule());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) { // Больше никаких Object и приведений типов!
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    // Удобный метод для поиска модуля по классу (понадобится для GUI)
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) {
                return (T) m;
            }
        }
        return null;
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}
