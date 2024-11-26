package net.mineproj.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import net.mineproj.plugin.core.AsyncScheduler;
import net.mineproj.plugin.functionality.commands.WsCommand;
import net.mineproj.plugin.functionality.logic.CustomFireball;
import net.mineproj.plugin.functionality.logic.DisableFallDamage;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import net.mineproj.plugin.protocol.listeners.*;
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
    /*
    Custom fireball explosions!
    Deal 0 damage, has own knockback logic.
    You can shoot fireball by interact
    with fire charge...
    */
    public final static boolean customFireballs = true;
    /*
    Simple, right? Just disable fall damage
    */
    public final static boolean disableFallDamage = true;
    /*
    Check all player packets by debug!
     */
    public final static boolean debug = false;

    @Override
    public void onEnable() {
        instance = this;
        this.runListeners();
        this.runPhys();
        this.initializeCommands();
        { // Your logic

        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initializeCommands() {
        this.getCommand("ws").setExecutor(new WsCommand());
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
        if (customFireballs) {
            Bukkit.getPluginManager().registerEvents(new CustomFireball(), this);
            m.addPacketListener(new FireUpdaterListener());
        }
        if (disableFallDamage) {
            Bukkit.getPluginManager().registerEvents(new DisableFallDamage(), this);
        }
        if (debug) {
            m.addPacketListener(new DebugListener());
        }
    }
    private void runPhys() {
        if (!physProcessor) return;
        Bukkit.getScheduler().runTaskTimer(this,
                        () -> AsyncScheduler.run(Ticker::run), 0L, 1L);
    }
}
