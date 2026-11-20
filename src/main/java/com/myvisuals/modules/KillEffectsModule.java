package com.myvisuals.modules;

import com.myvisuals.module.Category;
import com.myvisuals.module.Module;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class KillEffectsModule extends Module {
    private final Set<LivingEntity> attackedEntities = new HashSet<>();

    public KillEffectsModule() {
        super("KillEffects", "Молния и частицы при убийстве", Category.COMBAT);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (isEnabled() && world.isClient() && entity instanceof LivingEntity living && entity != mc.player) {
                attackedEntities.add(living);
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        Iterator<LivingEntity> it = attackedEntities.iterator();
        while (it.hasNext()) {
            LivingEntity entity = it.next();
            if (entity.isDead() || Float.compare(entity.getHealth(), 0.0f) == 0 || Float.compare(entity.getHealth(), 0.0f) == -1) {
                spawnKillEffect(entity);
                it.remove();
            } else if (entity.isRemoved() || Float.compare(entity.distanceTo(mc.player), 32.0f) == 1) {
                it.remove();
            }
        }
    }

    private void spawnKillEffect(LivingEntity target) {
        if (mc.world == null) return;

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
        lightning.setPosition(target.getX(), target.getY(), target.getZ());
        lightning.setCosmetic(true);
        mc.world.addEntity(lightning);

        for (int i = 0; i < 35; i++) {
            double offsetX = (Math.random() - 0.5) * 1.5;
            double offsetY = Math.random() * 1.8;
            double offsetZ = (Math.random() - 0.5) * 1.5;
            mc.world.addParticle(ParticleTypes.END_ROD,
                    target.getX() + offsetX,
                    target.getY() + offsetY,
                    target.getZ() + offsetZ,
                    offsetX * 0.25, 0.15, offsetZ * 0.25);
        }

        mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.2f);
    }
}
