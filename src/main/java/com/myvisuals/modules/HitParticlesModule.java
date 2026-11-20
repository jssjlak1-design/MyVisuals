package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;

public class HitParticlesModule extends Module {
    public HitParticlesModule() {
        super("HitParticles", "Магические искры при ударе по врагу", Category.COMBAT);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (isEnabled() && world.isClient() && entity instanceof LivingEntity target && entity != mc.player) {
                for (int i = 0; i < 18; i++) {
                    double vx = (Math.random() - 0.5) * 0.4;
                    double vy = Math.random() * 0.35;
                    double vz = (Math.random() - 0.5) * 0.4;
                    world.addParticle(ParticleTypes.WITCH,
                            target.getX(),
                            target.getY() + target.getHeight() * 0.5,
                            target.getZ(),
                            vx, vy, vz);
                }
            }
            return ActionResult.PASS;
        });
    }
}
