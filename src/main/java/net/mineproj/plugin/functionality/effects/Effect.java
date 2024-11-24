package net.mineproj.plugin.functionality.effects;

import lombok.Data;
import net.mineproj.plugin.millennium.math.Interpolation;
import org.bukkit.Location;
import org.bukkit.Particle;

@Data
public class Effect {

    private boolean living;
    private Type type;
    private Particle particle;
    private final int time;
    public int localTime;
    private int quality;
    private Location location;

    private double radi;
    private double height;

    private boolean isSimple;

    private Interpolation.Type interpolation;
    private Interpolation.Ease ease;
    public Effect(Type type, Particle particle, Location location, int time) {
        this.localTime = 0;
        this.living = true;
        this.particle = particle;
        this.type = type;
        this.time = time;
        this.radi = 5.0;
        this.height = 5.0;
        this.interpolation = Interpolation.Type.CIRC;
        this.ease = Interpolation.Ease.OUT;
        this.location = location;
        this.quality = 20;
        this.isSimple = false;
    }


    public Effect setRadi(double radi) {
        this.radi = radi;
        return this;
    }
    public Effect setSimple(boolean isSimple) {
        this.isSimple = isSimple;
        return this;
    }

    public Effect setHeight(double height) {
        this.height = height;
        return this;
    }

    public Effect setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Effect setIntepolation(Interpolation.Type t) {
        this.interpolation = t;
        return this;
    }
    public Effect setEase(Interpolation.Ease t) {
        this.ease = t;
        return this;
    }

    public Effect circularAddAtFor(int v) {
        this.quality = v;
        return this;
    }

    public enum Type {
        CIRCULAR, WAVE
    }
}
