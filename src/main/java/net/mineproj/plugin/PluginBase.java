package net.mineproj.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import net.mineproj.plugin.api.data.ProtocolPlugin;
import net.mineproj.plugin.api.listeners.ActionListener;
import net.mineproj.plugin.api.listeners.BlockPlaceFixerListener;
import net.mineproj.plugin.api.listeners.MovementListener;
import net.mineproj.plugin.api.listeners.VelocityListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginBase extends JavaPlugin {

    @Getter
    private static PluginBase instance;

    /*
    Analyzing the protocol for prohibited modifications.
    Helps against many kinds of exploits
     */
    public final static boolean protocolAnalyzer = true;

    @Override
    public void onEnable() {
        instance = this;
        this.runListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void runListeners() {
        ProtocolManager m = ProtocolLibrary.getProtocolManager();

        // Add players to container with PlayerProtocol
        m.addPacketListener(new ProtocolPlugin());

        // Other listeners
        m.addPacketListener(new MovementListener());
        m.addPacketListener(new ActionListener());
        m.addPacketListener(new VelocityListener());

        // Goofy
        m.addPacketListener(new BlockPlaceFixerListener());
    }
}
