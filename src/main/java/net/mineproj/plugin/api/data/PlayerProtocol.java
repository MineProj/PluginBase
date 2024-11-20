package net.mineproj.plugin.api.data;

import lombok.Data;
import net.mineproj.plugin.api.analyzer.modules.AnalyzerVL;
import net.mineproj.plugin.api.analyzer.modules.TimerBalancer;
import net.mineproj.plugin.utils.ProtocolTools;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
}
