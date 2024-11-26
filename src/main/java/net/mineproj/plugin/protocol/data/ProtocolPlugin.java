package net.mineproj.plugin.protocol.data;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolPlugin extends PacketAdapter implements Listener {

    private static final Map<Player, PlayerProtocol> container = new ConcurrentHashMap<>();
    public ProtocolPlugin() {
        super(
                        PluginBase.getInstance(),
                        ListenerPriority.HIGHEST,
                        PacketType.Play.Server.LOGIN
        );
    }

    public static PlayerProtocol getProtocol(Player player) {
        return container.get(player);
    }
    @Override
    public void onPacketSending(PacketEvent event) {
        container.put(
                        event.getPlayer(),
                        new PlayerProtocol(event.getPlayer())
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        container.remove(event.getPlayer());
    }
}
