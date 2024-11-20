package net.mineproj.plugin.functionality.ballistics;

import lombok.experimental.UtilityClass;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.api.data.PlayerProtocol;
import net.mineproj.plugin.api.data.ProtocolPlugin;
import net.mineproj.plugin.millennium.math.BuildSpeed;
import net.mineproj.plugin.millennium.math.GeneralMath;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.millennium.math.RayTrace;
import net.mineproj.plugin.utils.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
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
            Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> to.getWorld().createExplosion(to, ballistics.getExplosive()));
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
