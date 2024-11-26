package net.mineproj.plugin.functionality.commands;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.mineproj.plugin.core.AsyncScheduler;
import net.mineproj.plugin.services.WorldService;
import net.mineproj.plugin.utils.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class WsCommand extends WorldService implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Invalid Sender");
            return true;
        }
        Player player = ((Player) sender).getPlayer();

        if (!player.hasPermission("mp.admin")) {
            player.sendMessage("You dont have permission!");
            return true;
        }
        if ((!label.equalsIgnoreCase("ws"))) {
            player.sendMessage("Usage: /ws");
            return true;
        }
        if (args.length == 0) {
            String[] s = new String[]{
                            Wrapper.wrapColors("&c/ws&8 - &f WorldService Help"),
                            Wrapper.wrapColors("&c/ws list&8 - &fWorld list"),
                            Wrapper.wrapColors("&c/ws join &e<name>&8 - &fMove to world"),
                            Wrapper.wrapColors("&c/ws create &e<name>&8 - &fNew empty world"),
                            Wrapper.wrapColors("&c/ws create &e<name> <world>&8 - &fClone world"),
                            Wrapper.wrapColors("&c/ws delete &e<name>&8 - &fDelete world")

            };
            player.sendMessage(s);
            return true;
        } else {
            final String s = args[0];
            switch (s) {
                case ("create") -> {
                    try {
                        World w = (args.length >= 3) ? createWorld(args[2], args[1]) : createEmptyWorld(args[1]);
                        if (w != null) {
                            player.sendMessage(Wrapper.wrapColors("&aWorld created! &8[" + w.getName() + "]"));
                        } else {
                            player.sendMessage(Wrapper.wrapColors("&4Unknown error!"));
                        }
                    } catch (Exception e) {
                        player.sendMessage(Wrapper.wrapColors("&4" + e.getMessage()));
                    }
                }
                case ("list") -> {
                    player.sendMessage(Wrapper.wrapColors("&c&lWorld list:"));
                    int i = 1;
                    for (World world : Bukkit.getWorlds()) {
                        player.sendMessage(Wrapper.wrapColors("&4&l[" + i + "] &e" + world.getName()
                                        + " &8[" + world.getPlayers().size() + " players]"));
                        i++;
                    }
                }
                case ("join") -> {
                    try {
                        World w = Bukkit.getWorld(args[1]);
                        if (w == null) {
                            player.sendMessage(Wrapper.wrapColors("&4The world has not been found!"));
                        } else {
                            player.teleport(w.getSpawnLocation());
                        }
                    } catch (Exception e) {
                        player.sendMessage(Wrapper.wrapColors("&4" + e.getMessage()));
                    }
                }
                case ("delete") -> {
                    try {
                        if (deleteWorld(args[1])) {
                            player.sendMessage(Wrapper.wrapColors("&aWorld deleted!"));
                        } else {
                            player.sendMessage(Wrapper.wrapColors("&4Unknown error!"));
                        }
                    } catch (Exception e) {
                        player.sendMessage(Wrapper.wrapColors("&4" + e.getMessage()));
                    }
                }
            }
        }
        return true;
    }
}
