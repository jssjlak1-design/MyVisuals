package com.myvisuals.utils;

public class Animation {
    private float target;
    private float current;
    private float speed;

    public Animation(float initial, float speed) {
        this.current = initial;
        this.target = initial;
        this.speed = speed;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getTarget() {
        return target;
    }

    public void update(float delta) {
        float diff = target - current;
        if (Math.abs(diff) < 0.01f) {
            current = target;
        } else {
            current += diff * speed * delta;
        }
    }

    public float get() {
        return current;
    }

    // Easing функции для красоты
    public static float easeOut(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    public static float easeInOut(float x) {
        return x < 0.5 ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2;
    }
}
