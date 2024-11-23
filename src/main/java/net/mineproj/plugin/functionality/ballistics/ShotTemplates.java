package net.mineproj.plugin.functionality.ballistics;

import net.mineproj.plugin.protocol.data.PlayerProtocol;
import org.bukkit.Particle;

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
        BallisticsPhys.add(
                        new Ballistics(20, 30, 4,
                                        protocol.getLocation().getYaw(),
                                        protocol.getLocation().getPitch(),
                                        0.05F, 12,
                                        Particle.SONIC_BOOM,
                                        protocol.getLocation().clone().add(0, 1, 0))
                                        .setExplosive(2).setHeavy(true)
                                        .customWeight(0.08));

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
}
