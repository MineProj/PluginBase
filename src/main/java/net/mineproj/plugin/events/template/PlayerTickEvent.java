package net.mineproj.plugin.events.template;


import lombok.Getter;
import net.mineproj.plugin.protocol.data.PlayerProtocol;

public class PlayerTickEvent {

    public final PlayerProtocol protocol;
    public final long time;
    public final boolean legacy;
    @Getter
    private long delay;

    public PlayerTickEvent(PlayerProtocol protocol, boolean legacy) {
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
        this.delay = -1;
        this.legacy = legacy;
    }

    public PlayerTickEvent build() {
        this.delay = this.time - this.protocol.getTickTime();

        if (this.legacy) {
            // Via Version :cry:
            if (this.delay > 1020 && this.delay < 1060) {
                this.delay -= 1000;
            } else if (this.delay > 950) {
                this.delay -= 950;
            }
        }
        this.protocol.setTickTime(this.time);;
        return this;
    }

}
