package com.myvisuals;

import com.myvisuals.gui.ClickGuiScreen;
import com.myvisuals.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MyVisualsClient implements ClientModInitializer {
    public static ModuleManager moduleManager;
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.myvisuals.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.myvisuals"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            moduleManager.onTick();

            while (openGuiKey.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }
        });
    }
}
