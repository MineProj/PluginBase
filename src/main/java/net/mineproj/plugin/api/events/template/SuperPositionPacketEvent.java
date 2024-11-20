package net.mineproj.plugin.api.events.template;

import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.api.data.PlayerProtocol;

public class SuperPositionPacketEvent {

    public final PlayerProtocol protocol;
    public final PacketEvent packetEvent;

    public SuperPositionPacketEvent(PlayerProtocol protocol, PacketEvent packetEvent) {
        this.protocol = protocol;
        this.packetEvent = packetEvent;
    }

}
