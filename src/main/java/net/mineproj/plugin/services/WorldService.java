package net.mineproj.plugin.services;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class WorldService {
    public World createWorld(String worldName, int sessionId) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        File targetWorld = new File(Bukkit.getWorldContainer(), "session_" + sessionId);
        World world = null;
        World gameWorld = null;
        if (worldFolder.exists()) {
            WorldCreator worldCreator = new WorldCreator(worldName);
            world = Bukkit.createWorld(worldCreator);
            copyWorld(worldFolder, targetWorld);
            WorldCreator worldCreatorCopy = new WorldCreator("session_" + sessionId);
            gameWorld = Bukkit.createWorld(worldCreatorCopy);
            return (world != null) ? gameWorld : null;
        } else {
            return null;
        }
    }
    public boolean deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            boolean unloaded = Bukkit.unloadWorld(world, false);
            if (!unloaded) {
                Bukkit.getLogger().severe("Failed to unload world: " + worldName);
                return false;
            }
        }
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (deleteDirectory(worldFolder)) {
            Bukkit.getLogger().info("Successfully deleted world: " + worldName);
            return true;
        } else {
            Bukkit.getLogger().severe("Failed to delete a world directory: " + worldName);
            return false;
        }
    }
    public boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    public void copyWorld(File source, File target) {
        try {
            if (!target.exists()) {
                target.mkdirs();
            }

            for (File file : source.listFiles()) {
                File newFile = new File(target, file.getName());

                if (file.getName().equals("session.lock") || file.getName().equals("uid.dat")) {
                    continue;
                }

                if (file.isDirectory()) {
                    copyWorld(file, newFile);
                } else {
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
