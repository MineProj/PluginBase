package net.mineproj.plugin.millennium.math;

import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.millennium.vectors.Vec3;
import org.bukkit.Location;

public class RayTrace {
    public static boolean doRayTrace(BuildSpeed s, Vec2 rotation, Vec3 from, AxisAlignedBB box, double dist) {
        boolean intersection = false;
        intersection = isIntersection(from, rotation, intersection, box, dist, s);
        return intersection;
    }

    private static boolean isIntersection(Vec3 location, Vec2 rotation, boolean intersection, AxisAlignedBB boundingBox, double dist, BuildSpeed s) {
        final float yaw = (float) rotation.getY();
        final float pitch = (float) rotation.getX();
        final MovingObjectPosition result = rayCast(yaw, pitch, boundingBox, location, dist, s);
        intersection |= result != null && result.hitVec != null;
        return intersection;
    }

    private static MovingObjectPosition rayCast(final float yaw, final float pitch, final AxisAlignedBB bb, Vec3 locationIn, double dist, BuildSpeed s) {
        double lastX = locationIn.xCoord,
                        lastY = locationIn.yCoord,
                        lastZ = locationIn.zCoord;
        Vec3 vec3 = new Vec3(lastX, lastY, lastZ),
                        vec31 = getVectorForRotation(pitch, yaw, s),
                        vec32 = vec3.add(new Vec3(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist));
        return bb.calculateIntercept(vec3, vec32);
    }

    private static Vec3 getVectorForRotation(final float pitch, final float yaw, BuildSpeed s) {
        float f = FastMath.cos(-yaw * 0.017453292F - (float) Math.PI),
                        f1 = GeneralMath.sin(-yaw * 0.017453292F - (float) Math.PI, s),
                        f2 = -GeneralMath.cos(-pitch * 0.017453292F, s),
                        f3 = GeneralMath.sin(-pitch * 0.017453292F, s);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static boolean isIntersectionRay(Location location, Location targetLocation, double dist) {
        boolean intersection;
        double targetX = targetLocation.getX();
        double targetY = targetLocation.getY();
        double targetZ = targetLocation.getZ();
        AxisAlignedBB boundingBox = new AxisAlignedBB(
                        targetX - 0.4, targetY - 0.1, targetZ - 0.4,
                        targetX + 0.4, targetY + 1.9, targetZ + 0.4);
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();
        final MovingObjectPosition result = rayCastWithoutPlayer(yaw, pitch, boundingBox, location, dist);
        intersection = result != null && result.hitVec != null;
        return intersection;
    }

    private static MovingObjectPosition rayCastWithoutPlayer(final float yaw, final float pitch, final AxisAlignedBB bb, Location locationIn, double dist) {
        Location position = locationIn;
        double lastX = position.getX(),
                        lastY = position.getY(),
                        lastZ = position.getZ();
        Vec3 vec3 = new Vec3(lastX, lastY, lastZ),
                        vec31 = getVectorForRotation(pitch, yaw, BuildSpeed.FAST),
                        vec32 = vec3.add(new Vec3(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist));
        return bb.calculateIntercept(vec3, vec32);
    }
}
