package net.mineproj.plugin.functionality.ballistics;

import net.mineproj.plugin.api.data.PlayerProtocol;
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
                                        .customWeight(0.2));
    }
}
