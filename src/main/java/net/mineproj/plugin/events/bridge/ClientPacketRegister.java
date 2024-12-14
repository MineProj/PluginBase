package net.mineproj.plugin.events.bridge;

import lombok.experimental.UtilityClass;
import net.mineproj.plugin.events.template.PlayerTickEvent;
import net.mineproj.plugin.protocol.listeners.ActionListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ClientPacketRegister {

    private static final Set<ClientPacketListener> listeners = new HashSet<>();

    public static void addListener(ClientPacketListener packetListener) {
        listeners.add(packetListener);
    }
    public static void removeListener(ClientPacketListener packetListener) {
        listeners.remove(packetListener);
    }
    public static void run(Object event) {
        for (ClientPacketListener packetListener : listeners) {
            if (event instanceof PlayerMoveEvent) {
                packetListener.movement((PlayerMoveEvent) event);
            } else if (event instanceof PlayerTickEvent) {
                packetListener.tick((PlayerTickEvent) event);
            } else if (event instanceof PlayerVelocityEvent) {
                packetListener.velocity((PlayerVelocityEvent) event);
            } else if (event instanceof ActionListener.AbilitiesEnum) {
                packetListener.action((ActionListener.AbilitiesEnum) event);
            }
        }
    }
}
