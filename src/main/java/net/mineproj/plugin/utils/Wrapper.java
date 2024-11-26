package net.mineproj.plugin.utils;

import com.comphenix.protocol.injector.temporary.TemporaryPlayer;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class Wrapper {
    public static String wrapColors(String text) { return text.replace("&", "§"); }
    public static void sendMessagesToPlayers(String permission, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }
    public static Player foundPlayer(String playerString) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerString)) {
                return  player;
            }
        }
        return null;
    }
    public static boolean isTemporary(Player player) {
        return player instanceof TemporaryPlayer;
    }
}
