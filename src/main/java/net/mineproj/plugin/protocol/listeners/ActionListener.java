package net.mineproj.plugin.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.events.bridge.ClientPacketRegister;
import net.mineproj.plugin.functionality.ballistics.ShotTemplates;
import net.mineproj.plugin.functionality.effects.Effect;
import net.mineproj.plugin.functionality.effects.EffectsPhys;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import org.bukkit.Particle;

public class ActionListener extends PacketAdapter {

    public enum AbilitiesEnum {
        START_SPRINTING,
        STOP_SPRINTING,
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY
    }

    public ActionListener() {
        super(
                PluginBase.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Client.ENTITY_ACTION
        );
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PlayerProtocol protocol = ProtocolPlugin.getProtocol(event.getPlayer());
        if (event.getPacket().getModifier().getValues().size() > 1) {
            String typeString = event.getPacket().getModifier().getValues().get(1).toString();
            AbilitiesEnum type = getEnum(typeString);

            if (typeString != null) {
                if (type == AbilitiesEnum.PRESS_SHIFT_KEY) {
                    protocol.setSneaking(true);
                } else if (type == AbilitiesEnum.RELEASE_SHIFT_KEY) {
                    protocol.setSneaking(false);
                } else if (type == AbilitiesEnum.START_SPRINTING) {
                    protocol.setSprinting(true);
                } else if (type == AbilitiesEnum.STOP_SPRINTING) {
                    protocol.setSprinting(false);
                }
            }

            ClientPacketRegister.run(type);
        }
    }

    private AbilitiesEnum getEnum(String s) {
        for (AbilitiesEnum type : AbilitiesEnum.values()) {
            if (type.toString().equals(s)) {
                return type;
            }
        }
        return null;
    }

}
