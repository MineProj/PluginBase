package net.mineproj.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import net.mineproj.plugin.core.AsyncScheduler;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import net.mineproj.plugin.protocol.listeners.ActionListener;
import net.mineproj.plugin.protocol.listeners.BlockPlaceFixerListener;
import net.mineproj.plugin.protocol.listeners.MovementListener;
import net.mineproj.plugin.protocol.listeners.VelocityListener;
import net.mineproj.plugin.functionality.logic.Ticker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginBase extends JavaPlugin {

    @Getter
    private static PluginBase instance;

    /*
    Analyzing the protocol for prohibited modifications.
    Helps against many kinds of exploits
     */
    public final static boolean protocolAnalyzer = true;

    /*
    Process advanced physics (such as ballistics)
    through the physics handler every tick.
    Consumes resources, turn it off if you don't need it
     */
    public final static boolean physProcessor = true;

    @Override
    public void onEnable() {
        instance = this;
        this.runListeners();
        this.runPhys();
        { // Your logic

        }
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
    private void runPhys() {
        if (!physProcessor) return;
        Bukkit.getScheduler().runTaskTimer(this,
                        () -> AsyncScheduler.run(Ticker::run), 0L, 1L);
    }
}
