package net.mineproj.plugin.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import org.bukkit.entity.Player;

public class DebugListener extends PacketAdapter {

    public DebugListener() {
        super(PluginBase.getInstance(), PacketType.Play.Client.getInstance());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("P: " + event.getPacket().getType().name()
                        + " " + event.getPacket().getStructures().getValues().toString());
    }
}
