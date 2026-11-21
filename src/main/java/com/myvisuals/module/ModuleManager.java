    // ... другие методы ...

    public GuiSoundsModule getGuiSounds() {
        for (Module m : modules) {
            if (m instanceof GuiSoundsModule) {
                return (GuiSoundsModule) m;
            }
        }
        return null;
    }
}
