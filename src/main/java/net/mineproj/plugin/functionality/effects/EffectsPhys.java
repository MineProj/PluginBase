package net.mineproj.plugin.functionality.effects;

import lombok.experimental.UtilityClass;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.millennium.math.BuildSpeed;
import net.mineproj.plugin.millennium.math.GeneralMath;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.utils.BlockUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class EffectsPhys {

    private static List<Effect> effectSet = new CopyOnWriteArrayList<>();
    private static List<Effect> modify = null;
    private static final Object lock = new Object();
    private static final BukkitScheduler s = Bukkit.getScheduler();

    public static void tick() {
        synchronized (lock) {
            if (modify != null) {
                effectSet = new CopyOnWriteArrayList<>(modify);
                modify = null;
            }

            for (Effect effect : effectSet) {
                if (effect.localTime == effect.getTime())
                    effect.setLiving(false);
                if (!effect.isLiving()) {
                    effectSet.remove(effect);
                    continue;
                }

                effect.localTime++;
                double percent = ((double) effect.getLocalTime() / (double) effect.getTime());
                double interpolateRadi =
                                Interpolation.interpolate(0, effect.getRadi(),
                                percent, effect.getInterpolation(), effect.getEase());

                if (effect.getType().equals(Effect.Type.CIRCULAR)) {
                    Location l = effect.getLocation().clone();
                    for (double v = -interpolateRadi; v <= interpolateRadi; v++) {
                        double radiScale = Math.abs(v - interpolateRadi) / interpolateRadi;
                        radiScale = 1 - radiScale;
                        for (int angle = 0; angle <= 360; angle += effect.getQuality()) {
                            Location totalLocation = l.clone().add(
                                            -GeneralMath.sin((float) Math.toRadians(angle), BuildSpeed.FAST) * (interpolateRadi * radiScale),
                                            v,
                                            GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST) * (interpolateRadi * radiScale)
                            );
                            p(totalLocation.getWorld(), totalLocation,
                                            effect.getParticle(), 1);
                        }
                    }
                }
                if (effect.getType().equals(Effect.Type.WAVE)) {
                    Location l = effect.getLocation().clone();
                    for (double v = -effect.getHeight(); v <= effect.getHeight(); v++) {
                        for (int angle = 0; angle <= 360; angle += effect.getQuality()) {
                            Location totalLocation = l.clone().add(
                                            -GeneralMath.sin((float) Math.toRadians(angle), BuildSpeed.FAST) * (interpolateRadi),
                                            v,
                                            GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST) * (interpolateRadi)
                            );
                            p(totalLocation.getWorld(), totalLocation,
                                            effect.getParticle(), 1);
                        }
                    }
                }
            }
        }
    }

    public static void add(Effect effect) {
        effectSet.add(effect); // thread-safe add
    }

    private static void p(World w, Location l, Particle p, int c) {
        // thread-safe
        Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> w.spawnParticle(p, l, c));
    }


    private static boolean isHitBlock(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block block = BlockUtil.getBlockAsync(
                                    new Location(location.getWorld(), x + (dx * 0.15), y + (dy * 0.15), z + (dz * 0.15)));
                    Material material = block != null ? block.getType() : null;
                    if (material == null) continue;
                    if (material.isSolid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static double circOut(double from, double to, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        double change = to - from;
        double progress = percent / 100.0;
        return from + change * Math.sqrt(1 - Math.pow(progress - 1, 2));
    }

    private static double circIn(double from, double to, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        double change = to - from;
        double progress = percent / 100.0;
        return from - change * (Math.sqrt(1 - Math.pow(progress, 2)) - 1);
    }

    private static void p(Location l, Particle p) {
        // thread-safe
        Bukkit.getScheduler().runTask(PluginBase.getInstance(),
                        () -> Objects.requireNonNull(l.getWorld()).spawnParticle(p, l, 1, 0, 0, 0, 0));
    }
    private static void pSplash(Location l, Particle p) {
        // thread-safe
        Bukkit.getScheduler().runTask(PluginBase.getInstance(),
                        () -> Objects.requireNonNull(l.getWorld()).spawnParticle(p, l, 1));
    }

}
