package net.mineproj.plugin.protocol.analyzer;

import net.kyori.adventure.text.Component;
import net.mineproj.plugin.PluginBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnalyzerManager {
    public static void punish(Player player, String reason) {
        Bukkit.getScheduler().runTask(PluginBase.getInstance(), () -> {
            player.kick(Component.text("Inappropriate protocol inheritance"));
            PluginBase.getInstance().getLogger().warning(
                            "<ProtocolAnalyzer> " + player.getName()
                                            + " modified protocol (" + reason + ") and was kicked out."
            );
        });
    }
}
