package net.mineproj.plugin.api.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.api.analyzer.checks.TickRateCheck;
import net.mineproj.plugin.api.data.PlayerProtocol;
import net.mineproj.plugin.api.data.ProtocolPlugin;
import net.mineproj.plugin.api.events.bridge.MovementEvent;
import net.mineproj.plugin.api.events.template.PlayerTickEvent;
import net.mineproj.plugin.api.events.template.SuperPositionPacketEvent;
import net.mineproj.plugin.utils.ProtocolTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;

public class MovementListener extends PacketAdapter {
    public MovementListener() {
        super(
                        PluginBase.getInstance(),
                        ListenerPriority.LOWEST,
                        PacketType.Play.Server.POSITION,
                        PacketType.Play.Client.POSITION,
                        PacketType.Play.Client.POSITION_LOOK,
                        PacketType.Play.Client.LOOK,
                        PacketType.Play.Client.GROUND
        );
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        PlayerProtocol p = ProtocolPlugin.getProtocol(player);
        Set<tpFlags> flags = ProtocolTools.getTeleportFlags(event);
        Location tp = ProtocolTools.readLocation(event);
        Location loc = p.getLocation().clone();
        Location result = ProtocolTools.readLocation(event);
        for (tpFlags flag : flags) {
            if (flag.equals(tpFlags.X))
                result.setX(loc.getX() + tp.getX());
            if (flag.equals(tpFlags.Y))
                result.setY(loc.getY() + tp.getY());
            if (flag.equals(tpFlags.Z))
                result.setZ(loc.getZ() + tp.getZ());
        }
        p.setTeleport(result.clone());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        PlayerProtocol p = ProtocolPlugin.getProtocol(player);
        PacketContainer packet = event.getPacket();

        Location l = p.getLocation();
        p.setFromLocation(l.clone());
        boolean onGround = ProtocolTools.onGroundPacketLevel(event);
        p.setOnGround(onGround);

        boolean legacy = ProtocolTools.isFlying(event)
                        && p.isSameWithHash(ProtocolTools.readLocation(event));

        PlayerTickEvent tickEvent = new PlayerTickEvent(p, legacy).build();

        if (PluginBase.protocolAnalyzer)
            TickRateCheck.check(tickEvent);

        MovementEvent.tick(tickEvent);

        if (!ProtocolTools.getWorld(player).getName().equals(p.getFromWorld())) {
            p.setFromWorld(ProtocolTools.getWorld(player).getName());
            p.setLocation(ProtocolTools.getLoadLocation(player));
        }
        if (!ProtocolTools.isFlying(event)) {
            boolean hasPosition = ProtocolTools.hasPosition(packet.getType());
            boolean hasRotation = ProtocolTools.hasRotation(packet.getType());
            if (hasPosition) {
                p.addRawLocation(ProtocolTools.readLocation(event));
                p.pushHashPosition(ProtocolTools.readLocation(event));
            }
            if (ProtocolTools.isLoadLocation(p.getLocation())) {
                p.setLocation(ProtocolTools.readLocation(event));
                p.setFromLocation(ProtocolTools.readLocation(event));
            } else {
                if (hasPosition) {
                    if (p.getTeleport() != null) {
                        Location from = ProtocolTools.readLocation(event);
                        Location to = p.getTeleport();

                        // Let's check guys with bad internet
                        if (to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) {
                            p.setLocation(p.getTeleport().clone());
                            p.setFromLocation(p.getTeleport().clone());
                            p.setTeleport(null);
                        } else {
                            // Force packet stop if your packet are shit
                            event.setCancelled(true);
                            Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> {
                                if (p.getTeleport() != null) {
                                    Location build = p.getTeleport().clone();
                                    build.setYaw(p.getLocation().getYaw());
                                    build.setPitch(p.getLocation().getPitch());
                                    p.getPlayer().teleport(build);
                                }
                            });
                            return;
                        }
                    } else {
                        Location build = ProtocolTools.readLocation(event);
                        l.setX(build.getX());
                        l.setY(build.getY());
                        l.setZ(build.getZ());
                    }
                }
                if (hasRotation) {
                    l.setYaw(packet.getFloat().read(0));
                    l.setPitch(packet.getFloat().read(1));
                }
            }

            PlayerMoveEvent moveEvent = new PlayerMoveEvent(
                            player,
                            p.getFromLocation(),
                            p.getLocation()
            );
            moveEvent.setCancelled(event.isCancelled());
            MovementEvent.move(moveEvent, true);

            //player.sendMessage("event: " + moveEvent.getTo().toVector());
        } else {
            superPosition(new SuperPositionPacketEvent(p, event));
        }
    }

    private static void superPosition(SuperPositionPacketEvent packet) {
        boolean cancelled = packet.packetEvent.isCancelled();
        // "afk" packets
    }

    public enum tpFlags {
         X, Y, Z
    }
}
