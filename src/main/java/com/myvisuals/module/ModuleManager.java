package com.myvisuals.module;

import com.myvisuals.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new TargetEspModule());
        modules.add(new KillEffectsModule());
        
