package net.mineproj.plugin.functionality.ballistics;

import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.functionality.effects.Effect;
import net.mineproj.plugin.functionality.logic.CustomFireball;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ShotTemplates {

    public static void basicShot(PlayerProtocol protocol) {
        BallisticsPhys.add(
                        new Ballistics(45, 65, 7,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.02F, 4,
                                        Particle.FIREWORK,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setHeavy(false)
                                        .customWeight(0.01));
    }
    public static void sonicCannonShot(PlayerProtocol protocol) {
        Effect effect = new Effect(Effect.Type.WAVE, Particle.ENCHANTED_HIT,
                        new Location(null, 0, 0, 0), 8)
                        .circularAddAtFor(20).setHeight(8)
                        .setEase(Interpolation.Ease.IN).setRadi(16);
        BallisticsPhys.add(
                        new Ballistics(20, 30, 4,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.05F, 0,
                                        Particle.SONIC_BOOM,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(10).setHeavy(true)
                                        .setExplosionType(Ballistics.ExplosionType.VELOCITY)
                                        .customWeight(0.08).setVelocityRange(15).setEffect(effect));

    }

    public static void grenadeShot(PlayerProtocol protocol) {
        Effect effect = new Effect(Effect.Type.CIRCULAR, Particle.FLASH,
                        new Location(null, 0, 0, 0), 5)
                        .circularAddAtFor(20)
                        .setEase(Interpolation.Ease.OUT).setRadi(4);
        BallisticsPhys.add(
                        new Ballistics(12, 16, 4,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.05F, 8,
                                        Particle.SMALL_FLAME,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(3).setHeavy(true)
                                        .setExplosionType(Ballistics.ExplosionType.VELOCITY)
                                        .customWeight(0.1).setVelocityRange(4).setEffect(effect));

    }
    public static void fireShot(PlayerProtocol protocol) {
        Effect effect = new Effect(Effect.Type.CIRCULAR, Particle.FLAME,
                        new Location(null, 0, 0, 0), 5)
                        .circularAddAtFor(20).setSimple(true)
                        .setEase(Interpolation.Ease.OUT).setRadi(3);
        BallisticsPhys.add(
                        new Ballistics(100, 200, 2,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.001F, 0,
                                        Particle.LAVA,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(8).setHeavy(false)
                                        .setExplosionType(Ballistics.ExplosionType.VELOCITY)
                                        .customWeight(0.0).setVelocityRange(7).setEffect(effect));
        launchFireball(protocol.getPlayer());
    }
    public static void fireShot(Location location, Vec2 direction) {
        Effect effect = new Effect(Effect.Type.CIRCULAR, Particle.FLAME,
                        new Location(null, 0, 0, 0), 5)
                        .circularAddAtFor(20).setSimple(true)
                        .setEase(Interpolation.Ease.OUT).setRadi(3);
        BallisticsPhys.add(
                        new Ballistics(100.0, 200, 2,
                                        (float) direction.getX(),
                                        (float) direction.getY(),
                                        0.0F, 0,
                                        Particle.LAVA,
                                        location)
                                        .setExplosive(8).setHeavy(false)
                                        .setExplosionType(Ballistics.ExplosionType.VELOCITY)
                                        .customWeight(0.0).setVelocityRange(7).setEffect(effect));
    }
    public static void chainAtomicBombShot(PlayerProtocol protocol) {
        BallisticsPhys.add(
                        new Ballistics(100, 200, 4,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.001F, 100,
                                        Particle.CAMPFIRE_COSY_SMOKE,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(3).setHeavy(true)
                                        .customWeight(0.03).setExplosionType(Ballistics.ExplosionType.CHAIN_ATOMIC));
    }
    public static void atomicBombShot(PlayerProtocol protocol) {
        BallisticsPhys.add(
                        new Ballistics(100, 200, 4,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.001F, 100,
                                        Particle.SOUL_FIRE_FLAME,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(25).setHeavy(true)
                                        .customWeight(0.03).setExplosionType(Ballistics.ExplosionType.ATOMIC));
    }

    public static void launchFireball(Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();
        PlayerProtocol protocol = ProtocolPlugin.getProtocol(player);
        Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> {
            Fireball fireball = player.getWorld().spawn(loc.add(direction.multiply(2)), Fireball.class);
            fireball.setShooter(player);
            fireball.setVelocity(direction.multiply(1));
            fireball.setIsIncendiary(false);
            fireball.setYield(0);
            CustomFireball.directional.put(fireball.getUniqueId(),
                            new Vec2(protocol.getLocation().getYaw(), protocol.getLocation().getPitch()));
        });
    }
}
