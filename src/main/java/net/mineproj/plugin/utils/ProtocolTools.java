package net.mineproj.plugin.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.temporary.TemporaryPlayer;
import net.mineproj.plugin.api.listeners.MovementListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ProtocolTools {

    public static Location readLocation(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        if (packet.getDoubles().size() >= 3) {
            return new Location(
                    getWorld(event.getPlayer()),
                    packet.getDoubles().read(0),
                    packet.getDoubles().read(1),
                    packet.getDoubles().read(2)
            );
        } else {
            event.getPlayer().kick();
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
    }
    public static boolean isFlying(PacketEvent event) {
        PacketType p = event.getPacket().getType();
        return
        (
        p.equals(
        PacketType.Play.Client.FLYING)
        ||
        p.equals(
        PacketType.Play.Client.GROUND)
        );
    }

    public static boolean onGroundPacketLevel(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        return packet.getBooleans().size() > 0
                && packet.getBooleans().read(0);
    }

    public static Set<MovementListener.tpFlags> getTeleportFlags(PacketEvent event) {
        String s = event.getPacket().getStructures().getValues().get(0).toString();
        Set<MovementListener.tpFlags> flags = new HashSet<>();
        s = s.replace("X_ROT", "").replace("Y_ROT", "");
        if (s.contains("X")) flags.add(MovementListener.tpFlags.X);
        if (s.contains("Y")) flags.add(MovementListener.tpFlags.Y);
        if (s.contains("Z")) flags.add(MovementListener.tpFlags.Z);
        return flags;
    }

    public static boolean invalidTeleport(Location location) {
        return location == null
                || location.getX() == 8.5D
                || location.getZ() == 8.5D;
    }
    public static boolean isLoadLocation(Location location) {
        return (location.getX() == 1 && location.getY() == 1 && location.getZ() == 1);
    }

    public static Location getLoadLocation(Player player) {
        return new Location(getWorld(player), 1, 1, 1);
    }

    public static boolean hasPosition(PacketType type) {
        return (type.equals(PacketType.Play.Client.POSITION)
                        || type.equals(PacketType.Play.Client.POSITION_LOOK));
    }
    public static boolean hasRotation(PacketType type) {
        return (type.equals(PacketType.Play.Client.LOOK)
                        || type.equals(PacketType.Play.Client.POSITION_LOOK));
    }
    public static World getWorld(Entity entity) {
        if (entity instanceof Player) {
            if (isTemporary((Player) entity)) {
                return Bukkit.getWorlds().get(0);
            } else {
                return entity.getWorld();
            }
        } else {
            return entity.getWorld();
        }
    }

    public static boolean isTemporary(OfflinePlayer player) {
        return player instanceof TemporaryPlayer;
    }
}
