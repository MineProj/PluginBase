package net.mineproj.plugin.functionality.effects;

import lombok.experimental.UtilityClass;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.millennium.math.BuildSpeed;
import net.mineproj.plugin.millennium.math.Euler;
import net.mineproj.plugin.millennium.math.GeneralMath;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.millennium.shapes.Shape;
import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.millennium.vectors.Vec3;
import net.mineproj.plugin.protocol.data.Pair;
import net.mineproj.plugin.utils.BlockUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

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
                if (effect.getPreTime() > 0) {
                    effect.setPreTime(effect.getPreTime() - 1);
                    continue;
                }

                effect.localTime++;
                double percent = ((double) effect.getLocalTime() / (double) effect.getTime());
                double interpolateRadi = (!effect.isStaticAnim()) ?
                                Interpolation.interpolate(0, effect.getRadi(),
                                                percent, effect.getInterpolation(), effect.getEase())
                                : effect.getRadi();

                if (effect.getType().equals(Effect.Type.CIRCULAR)) {
                    Location l = effect.getLocation().clone();
                    for (double v = -interpolateRadi; v <= interpolateRadi; v++) {
                        double radiScale = (Math.abs((v == 0) ? 1 : v) / interpolateRadi);
                        for (int angle = (int) effect.rotationYawLocal; angle <= 360 + effect.rotationYawLocal; angle += effect.getQuality()) {
                            Location totalLocation = l.clone().add(
                                            -GeneralMath.sin((float) Math.toRadians(angle), BuildSpeed.FAST)
                                                            * ((effect.isStaticAnim()) ? effect.getRadi() / radiScale : (interpolateRadi / radiScale)),
                                            v,
                                            GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST)
                                                            * ((effect.isStaticAnim()) ? effect.getRadi() / radiScale : (interpolateRadi / radiScale))
                            );
                            if (!(effect.getChance() != 1.0 && Math.random() > effect.getChance())) {
                                if (effect.isSimple()) {
                                    p2(totalLocation, effect, effect.getParticle());
                                } else {
                                    p(totalLocation.getWorld(), totalLocation,
                                                    effect.getParticle(), 1);
                                }
                            }
                        }
                    }
                } else
                if (effect.getType().equals(Effect.Type.WAVE)) {
                    Location l = effect.getLocation().clone();
                    for (double v = -effect.getHeight(); v <= effect.getHeight(); v++) {
                        for (int angle = (int) effect.rotationYawLocal; angle <= 360 + effect.rotationYawLocal; angle += effect.getQuality()) {
                            Location totalLocation = l.clone().add(
                                            -GeneralMath.sin((float) Math.toRadians(angle), BuildSpeed.FAST) * (effect.isStaticAnim() ? effect.getRadi() : (interpolateRadi)),
                                            v,
                                            GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST) * (effect.isStaticAnim() ? effect.getRadi() : (interpolateRadi))
                            );
                            if (!(effect.getChance() != 1.0 && Math.random() > effect.getChance())) {
                                if (effect.isSimple()) {
                                    p2(totalLocation, effect, effect.getParticle());
                                } else {
                                    p(totalLocation.getWorld(), totalLocation,
                                                    effect.getParticle(), 1);
                                }
                            }
                        }
                    }
                } else if (effect.getType().equals(Effect.Type.RING)) {
                    Location l = effect.getLocation().clone();
                    if (effect.getRadiTo() != 0.0) {
                        effect.setRadi(Interpolation.interpolate(
                                        effect.getRadiFrom(), effect.getRadiTo(),
                                        ((double) effect.localTime / effect.getTime()),
                                        effect.getInterpolation(), effect.getEase()));
                    }
                    for (int angle = (int) effect.rotationYawLocal; angle <= (effect.isOnlyAtEnd()
                                    ? effect.rotationYawLocal + 1 : 360 + effect.rotationYawLocal); angle += effect.getQuality()) {
                        Location totalLocation = l.clone().add(
                                        -GeneralMath.sin((float) Math.toRadians(angle), BuildSpeed.FAST) * effect.getRadi(),
                                        Math.sin(Math.toRadians(effect.getRotationPitchLocal())) * (((GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST) * effect.getRadi()))),
                                        Math.cos(Math.toRadians(effect.getRotationPitchLocal())) * GeneralMath.cos((float) Math.toRadians(angle), BuildSpeed.FAST) * effect.getRadi()
                        );
                        effect.rotationPitchLocal += effect.getRotationPitch();
                        if (!(effect.getChance() != 1.0 && Math.random() > effect.getChance())) {
                            if (effect.isSimple()) {
                                p2(totalLocation, effect, effect.getParticle());
                            } else {
                                p(totalLocation.getWorld(), totalLocation,
                                                effect.getParticle(), 1);
                            }
                        }
                    }
                } else if (effect.getType().equals(Effect.Type.MILLENNIUM_CUSTOM)) {
                    Location l = effect.getLocation().clone();
                    for (Pair<Shape, Vec2> layer : effect.getMillenniumMatrix()) {
                        Shape shape = layer.getX();
                        Vec2 vec2 = layer.getY();
                        double deltaY = vec2.getX();
                        double multiply = vec2.getY();
                        for (Vec2 vector : shape.getVectors()) {
                            double deltaX = vector.getX() * multiply;
                            double deltaZ = vector.getY() * multiply;
                            Location fL = l.clone().add(deltaX, deltaY, deltaZ);
                            if (!(effect.getChance() != 1.0 && Math.random() > effect.getChance())) {
                                if (effect.isSimple()) {
                                    p2(fL, effect, effect.getParticle());
                                } else {
                                    p(fL.getWorld(), fL,
                                                    effect.getParticle(), 1);
                                }
                            }
                        }
                    }
                }
                effect.rotationYawLocal += effect.getRotationYaw();
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
    private static void p2(Location l, Effect effect, Particle p) {
        // thread-safe
        Bukkit.getScheduler().runTask(PluginBase.getInstance(),
                        () -> {
                            if (effect.getDustOptions() != null) {
                                l.getWorld().spawnParticle(p, l, 1, effect.getDustOptions());
                            } else {
                                if (effect.isDirection()) {
                                    Location r = effect.getLocation().clone();
                                    Vec2 rot = Euler.calculateVec2Vec(
                                                    new Vec3(l.getX(), l.getY(), l.getZ()),
                                                    new Vec3(r.getX(), r.getY(), r.getZ()));
                                    r.setYaw((float) rot.getX());
                                    r.setPitch((float) rot.getY());
                                    Vector f = r.getDirection();
                                    l.getWorld().spawnParticle(p, l, 0, f.getX(), f.getY(), f.getZ(), effect.getCustomSpeed());
                                } else {
                                    Vector d = effect.getMotion();
                                    l.getWorld().spawnParticle(p, l, 1, d.getX(), d.getY(), d.getZ(), effect.getCustomSpeed());
                                }
                            }
                        }
        );

    }
    private static void pSplash(Location l, Particle p) {
        // thread-safe
        Bukkit.getScheduler().runTask(PluginBase.getInstance(),
                        () -> Objects.requireNonNull(l.getWorld()).spawnParticle(p, l, 1));
    }

}
