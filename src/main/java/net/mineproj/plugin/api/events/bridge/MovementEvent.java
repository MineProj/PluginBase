package net.mineproj.plugin.api.events.bridge;

import net.mineproj.plugin.api.events.template.PlayerTickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class MovementEvent {

    public static void move(PlayerMoveEvent event, boolean cancelled) {

    }

    // Aka knockback
    public static void velocity(PlayerVelocityEvent event, boolean cancelled) {

    }

    public static void tick(PlayerTickEvent event) {

    }
}
