package net.mineproj.plugin.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import org.bukkit.entity.Player;

/*
That's not mean event after block place...
That's mean just Block place packet.
Need to fix timer falsest.
Minecraft's developers mixed up the names of packets,
and BLOCK_PLACE = USE_ITEM
*/
public class BlockPlaceFixerListener extends PacketAdapter {

    public BlockPlaceFixerListener() {
        super(
                PluginBase.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Client.BLOCK_PLACE
        );
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        PlayerProtocol protocol = ProtocolPlugin.getProtocol(player);
        protocol.getTimerBalancer().addBalance(50);
    }
}