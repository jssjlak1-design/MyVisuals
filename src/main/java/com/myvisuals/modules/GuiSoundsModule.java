package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class GuiSoundsModule extends Module {
    private int soundIndex = 0;

    public GuiSoundsModule() {
        super("GuiSounds", "Звуки при переключении модулей", Category.MISC);
        setEnabled(true);
    }

    public void nextSound() {
        soundIndex = (soundIndex + 1) % 3;
        playToggleSound(true);
    }

    public String getCurrentSoundName() {
        if (soundIndex == 1) return "Пузырёк";
        if (soundIndex == 2) return "Колокольчик";
        return "Мягкий клик";
    }

    public void playToggleSound(boolean enabled) {
        if (!isEnabled() || mc.getSoundManager() == null) return;
        float pitch = enabled ? 1.25f : 0.85f;

        if (soundIndex == 1) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ITEM_PICKUP, pitch, 0.7f));
        } else if (soundIndex == 2) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), pitch, 0.8f));
        } else {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), pitch, 0.6f));
        }
    }
}
