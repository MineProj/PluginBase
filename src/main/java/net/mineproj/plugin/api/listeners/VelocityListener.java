package net.mineproj.plugin.api.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.api.data.PlayerProtocol;
import net.mineproj.plugin.api.data.ProtocolPlugin;
import net.mineproj.plugin.api.events.bridge.MovementEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class VelocityListener extends PacketAdapter {


    public VelocityListener() {
        super(
                PluginBase.getInstance(),
                ListenerPriority.LOWEST,
                PacketType.Play.Server.ENTITY_VELOCITY
        );
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        PlayerProtocol protocol = ProtocolPlugin.getProtocol(player);

        PacketContainer packet = event.getPacket();

        if (!packet.getIntegers().getValues().isEmpty()) {
            int id = packet.getIntegers().getValues().get(0);
            if (protocol.getPlayer().getEntityId() == id) {

                double x = packet.getIntegers().read(1).doubleValue() / 8000.0D,
                        y = packet.getIntegers().read(2).doubleValue() / 8000.0D,
                        z = packet.getIntegers().read(3).doubleValue() / 8000.0D;
                PlayerVelocityEvent bukkitEvent = new PlayerVelocityEvent(player, new Vector(x, y, z));
                bukkitEvent.setCancelled(event.isCancelled());
                MovementEvent.velocity(bukkitEvent, true);
            }
        }
    }

}
