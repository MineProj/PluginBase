package net.mineproj.plugin.events.bridge;

import net.mineproj.plugin.events.template.PlayerTickEvent;
import net.mineproj.plugin.protocol.listeners.ActionListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public interface ClientPacketListener {
    void movement(PlayerMoveEvent event);
    void tick(PlayerTickEvent event);
    void velocity(PlayerVelocityEvent event);
    void action(ActionListener.AbilitiesEnum event);
}
