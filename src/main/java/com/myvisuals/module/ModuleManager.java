package com.myvisuals.module;

import com.myvisuals.modules.AutoSprintModule;
import com.myvisuals.modules.ChinaHatModule;
import com.myvisuals.modules.CustomSkyModule;
import com.myvisuals.modules.FullBrightModule;
import com.myvisuals.modules.GuiSoundsModule;
import com.myvisuals.modules.HitParticlesModule;
import com.myvisuals.modules.JumpCirclesModule;
import com.myvisuals.modules.KillEffectsModule;
import com.myvisuals.modules.TargetEspModule;
import com.myvisuals.modules.WatermarkHudModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List modules = new ArrayList();

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

    public List getModules() {
        return modules;
    }

    public List getModulesByCategory(Category category) {
        List result = new ArrayList();
        for (Object obj : modules) {
            Module m = (Module) obj;
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    public GuiSoundsModule getGuiSounds() {
        for (Object obj : modules) {
            if (obj instanceof GuiSoundsModule gs) return gs;
        }
        return null;
    }

    public void onTick() {
        for (Object obj : modules) {
            Module m = (Module) obj;
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}
