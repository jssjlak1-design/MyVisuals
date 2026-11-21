package com.myvisuals.module;

public enum Category {
    COMBAT("Combat", 0xFFFF4D4D),   // Красный
    RENDER("Render", 0xFF4DFFFF),   // Голубой
    WORLD("World", 0xFF4DFF4D),     // Зелёный
    MISC("Misc", 0xFFFFD94D);       // Жёлтый

    private final String name;
    private final int color;

    Category(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public int getColor() { return color; } // Теперь этот метод есть!
}
