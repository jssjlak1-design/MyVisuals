    // ... (предыдущий код)

    public Setting<?> getSettingByName(String name) {
        for (Setting<?> setting : settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null; // <-- Вот здесь должно быть null с двумя L
    }
} // <-- И вот эта закрывающая скобка класса
