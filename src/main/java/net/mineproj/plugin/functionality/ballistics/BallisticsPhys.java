package net.mineproj.plugin.functionality.ballistics;

import lombok.experimental.UtilityClass;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.core.AsyncScheduler;
import net.mineproj.plugin.functionality.effects.EffectsPhys;
import net.mineproj.plugin.millennium.math.*;
import net.mineproj.plugin.millennium.shapes.Circle;
import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.millennium.vectors.Vec3;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import net.mineproj.plugin.utils.BlockUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class BallisticsPhys {

    private static List<Ballistics> ballisticsSet = new CopyOnWriteArrayList<>();
    private static List<Ballistics> modify = null;
    private static final Object lock = new Object();
    private static final BukkitScheduler s = Bukkit.getScheduler();

    public static void tick() {
        synchronized (lock) {
            if (modify != null) {
                ballisticsSet = new CopyOnWriteArrayList<>(modify);
                modify = null;
            }

            for (Ballistics ballistics : ballisticsSet) {
                if (!ballistics.isLiving()) {
                    pSplash(ballistics.getLocation(), ballistics.getParticle());
                    ballisticsSet.remove(ballistics);
                    continue;
                }

                PlayerProtocol creator = ballistics.getCreator();
                final List<Player> cachedPlayers =
                                (creator == null) ? new ArrayList<>()
                                : creator.getLocation().getWorld().getPlayers().stream().toList();
                final float yaw = ballistics.getYaw();
                final float pitch = ballistics.getPitch();

                Vector direction = new Vector(
                                -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                                -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                                GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));

                final double speed = ballistics.getSpeed();
                double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);

                direction.setX(direction.getX() * interpolatePitch);
                direction.setZ(direction.getZ() * interpolatePitch);

                for (int i = 0; i < speed; i++) {
                    Location from = ballistics.getLocation().clone();
                    Location to = ballistics.getLocation().clone().add(direction);

                    if (isHitBlock(to)) {
                        ballistics.setLiving(false);
                        if (ballistics.getEffect() != null) {
                            Location toAdd = ballistics.getEffect().getLocation().clone();
                            toAdd.setWorld(to.getWorld());
                            EffectsPhys.add(ballistics.getEffect().setLocation(to.clone().add(toAdd)));
                        }
                        explode(ballistics, to);
                        break;
                    } else {
                        for (Player target : cachedPlayers) {
                            PlayerProtocol p = ProtocolPlugin.getProtocol(target);
                            Location targetLoc = p.getLocation();
                            if (target.getName().hashCode() == creator.getPlayer().getName().hashCode()) {
                                continue;
                            }
                            if (targetLoc.distance(to) < 2 && RayTrace.isIntersectionRay(from, targetLoc, 1.0)) {
                                s.runTask(PluginBase.getInstance(), () -> {
                                    target.getPlayer().damage(ballistics.getDamage());
                                    if (ballistics.getEffect() != null) {
                                        Location toAdd = ballistics.getEffect().getLocation().clone();
                                        toAdd.setWorld(to.getWorld());
                                        EffectsPhys.add(ballistics.getEffect().setLocation(to.clone().add(toAdd)));
                                    }
                                    explode(ballistics, to);
                                    ballistics.setLiving(false);
                                });
                            }
                        }
                        ballistics.setLocation(to);
                        p(to, ballistics.getParticle());
                        ballistics.setDistance(ballistics.getDistance() + 1);
                        int pOut = (int) ((ballistics.getDistance() / ballistics.getTargetDistance()) * 100);
                        if (ballistics.getDistance() > ballistics.getTargetDistance()) {
                            ballistics.outOfDistance();
                            ballistics.setSpeed(ballistics.getSpeed() / 1.04);
                            ballistics.getLocation().add(0, -ballistics.getWeight(), 0);
                            if (!ballistics.isHeavy()) {
                                ballistics.setDamage(1);
                                if (ballistics.getDistance() > ballistics.getTargetDistance() * 10) {
                                    ballistics.setLiving(false);
                                }
                            }
                        } else {
                            ballistics.getLocation().add(0, -circOut(0, ballistics.getWeight(), pOut), 0);
                        }
                    }
                }
            }
        }
    }

    public static void add(Ballistics ballistics) {
        ballisticsSet.add(ballistics); // thread-safe add
    }

    public static void explode(Ballistics ballistics, Location to) {
        if (ballistics.getExplosive() > 0) {
            Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> {
                switch (ballistics.getExplosionType()) {
                    case VANILLA ->
                    to.getWorld()
                    .createExplosion(to, ballistics.getExplosive());
                    case VELOCITY -> {
                        World w = to.getWorld();
                        for (Player player : w.getPlayers()) {
                            AsyncScheduler.run(() -> {
                                PlayerProtocol protocol = ProtocolPlugin.getProtocol(player);
                                if (protocol.getLocation().distance(to) <= ballistics.getVelocityRange()) {
                                    double calculateRealisticVertical = 0;
                                    if (ballistics.isVelocityRealisticPostProcessing()) {
                                        double delta = protocol.getLocation().getY() - to.getY();
                                        calculateRealisticVertical = (delta >= 1.0) ? 0 :
                                                        Interpolation.interpolate(0.5, 1,
                                                        delta, Interpolation.Type.BACK, Interpolation.Ease.OUT);
                                    }
                                    Vec2 vec = Euler.calculateVec2Vec(new Vec3(to),
                                                    new Vec3(protocol.getLocation().clone().add(0, calculateRealisticVertical, 0)));
                                    Vector velo = new Vector(
                                                    -GeneralMath.sin(
                                                                    (float) Math.toRadians(vec.getX())
                                                                    , BuildSpeed.FAST),
                                                    -GeneralMath.sin(
                                                                    (float) Math.toRadians(vec.getY()), BuildSpeed.FAST),
                                                    GeneralMath.cos(
                                                                    (float) Math.toRadians(vec.getX())
                                                                    , BuildSpeed.FAST)).multiply(ballistics.getExplosive() / 5);
                                    double interpolatePitch = 1 - ((Math.abs(vec.getY())) / 90);
                                    velo.setX(velo.getX() * 3 * interpolatePitch);
                                    velo.setZ(velo.getZ() * 3 * interpolatePitch);
                                    if (ballistics.isVelocityRealisticPostProcessing()) {
                                        double delta = protocol.getLocation().distance(to) / ballistics.getVelocityRange();
                                        double calculateRealisticHorizontal = Interpolation.interpolate(1, 0.4,
                                                                        delta, Interpolation.Type.BACK, Interpolation.Ease.OUT);
                                        velo.setX(velo.getX() * calculateRealisticHorizontal);
                                        velo.setZ(velo.getZ() * calculateRealisticHorizontal);
                                    }
                                    protocol.punch(velo);
                                }
                            });
                        }
                    }
                    case ATOMIC -> {
                        to.getWorld().createExplosion(to, 5);
                        AsyncScheduler.run(() -> {
                            for (int h = 1; h <= ballistics.getExplosive(); h++) {
                                for (int r = 1; r <= 360; r += 10) {
                                    Location calculatedLocation = to.clone();
                                    calculatedLocation.add(
                                    -GeneralMath.sin((float) Math.toRadians(r), BuildSpeed.FAST) * (h * 4),
                                    4 + (h * 2),
                                    GeneralMath.cos((float) Math.toRadians(r), BuildSpeed.FAST) * (h * 4));
                                    add(new Ballistics((double) h / 2.0, h, h / 3.0, r, 90, 0, 50,
                                                    ballistics.getParticle(),
                                                    calculatedLocation)
                                                    .setHeavy(true)
                                                    .setExplosive(5)
                                                    .setExplosionType(Ballistics.ExplosionType.VANILLA)
                                                    .customWeight(0.5));
                                }
                            }
                        });
                    }
                    case CHAIN_ATOMIC -> {
                        to.getWorld()
                        .createExplosion(to, 5);
                        AsyncScheduler.run(() -> {
                            if (ballistics.getExplosive() > 0) {
                                for (int r = 1; r <= 360; r += 30) {
                                    add(new Ballistics(1, 1, 1, r, -20, 0, 50,
                                                    ballistics.getParticle(),
                                                    to.clone().add(0, 1, 0))
                                                    .setHeavy(true)
                                                    .setExplosive(ballistics.getExplosive() - 1)
                                                    .setExplosionType(Ballistics.ExplosionType.CHAIN_ATOMIC)
                                                    .customWeight(0.3));
                                }
                            }
                        });
                    }
                }
            });
        }
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
