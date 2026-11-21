package com.myvisuals.module;

import com.myvisuals.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
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
        for (Module m : modules) {
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    // Универсальный метод для получения любого модуля по классу
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) {
                return (T) m;
            }
        }
        return null;
    }

    // Специфичный метод для GuiSounds (оставлен для удобства или старых вызовов)
    public GuiSoundsModule getGuiSounds() {
        return getModule(GuiSoundsModule.class);
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}
