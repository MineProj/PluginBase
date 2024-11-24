package net.mineproj.plugin.protocol.data;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Data;
import net.mineproj.plugin.protocol.analyzer.modules.AnalyzerVL;
import net.mineproj.plugin.protocol.analyzer.modules.TimerBalancer;
import net.mineproj.plugin.utils.ProtocolTools;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Objects;

@Data
public class PlayerProtocol {

    private final Player player;

    private Location location, fromLocation, teleport;
    private boolean onGround, onGroundFrom, sprinting, sneaking;
    private LinkedList<Location> positionHistory, positionHistoryLong;
    private final TimerBalancer timerBalancer;
    private String fromWorld;
    private int hashPosBuffer;
    private long tickTime;

    private AnalyzerVL analyzerVL;

    public PlayerProtocol(Player player) {
        this.player = player;
        this.location = ProtocolTools.getLoadLocation(player);
        this.fromLocation = ProtocolTools.getLoadLocation(player);
        this.fromWorld = "";
        this.teleport = null;
        this.onGround = false;
        this.onGroundFrom = false;
        this.sprinting = false;
        this.sneaking = false;
        this.hashPosBuffer = 0;
        this.positionHistory = new LinkedList<>();
        this.positionHistoryLong = new LinkedList<>();

        this.tickTime = System.currentTimeMillis();
        this.timerBalancer = new TimerBalancer();
        this.analyzerVL = new AnalyzerVL();
    }

    public void pushHashPosition(Location location) {
        this.hashPosBuffer = Objects.hashCode((location.getX() + location.getY() + location.getZ()));
    }

    public boolean isSameWithHash(Location location) {
        return this.hashPosBuffer == Objects.hashCode((location.getX() + location.getY() + location.getZ()));
    }

    public void addRawLocation(Location location) {
        this.positionHistory.addLast(location.clone());
        this.positionHistoryLong.addLast(location.clone());
        if (this.positionHistory.size() > 20) {
            this.positionHistory.removeFirst();
        }
        if (this.positionHistoryLong.size() > 50) {
            this.positionHistoryLong.removeFirst();
        }
    }

    public void punch(Vector velocity) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer velocityPacket = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.ENTITY_VELOCITY);
        velocityPacket.getIntegers().write(0, this.player.getEntityId());
        velocityPacket.getIntegers()
                        .write(1, (int) (velocity.getX() * 8000))
                        .write(2, (int) (velocity.getY() * 8000))
                        .write(3, (int) (velocity.getZ() * 8000));
        protocolManager.sendServerPacket(this.player, velocityPacket);
    }
}
