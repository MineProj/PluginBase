package net.mineproj.plugin.functionality.effects;

import lombok.Data;
import net.mineproj.plugin.millennium.math.Interpolation;
import net.mineproj.plugin.millennium.shapes.Shape;
import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.protocol.data.Pair;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Data
public class Effect implements Cloneable {

    private boolean living;
    private boolean staticAnim;
    private Type type;
    private Particle particle;
    private final int time;
    private int preTime;
    public int localTime;
    private int quality;
    private float rotationPitch;
    private float rotationYaw;
    public float rotationPitchLocal;
    public float rotationYawLocal;
    private Location location;
    private double chance;
    private boolean direction;
    private double customSpeed;
    private PlayerProtocol protocol;

    private double radi;
    private double radiTo;
    private double radiFrom;
    private double height;
    private Vector motion;

    private boolean isSimple, onlyAtEnd;
    private Particle.DustOptions dustOptions;

    private List<Pair<Shape, Vec2>> millenniumMatrix;

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
        this.staticAnim = false;
        this.rotationPitch = 0;
        this.rotationYaw = 0;
        this.rotationPitchLocal = 0;
        this.rotationYawLocal = 0;
        this.millenniumMatrix = new ArrayList<>();
        this.dustOptions = null;
        this.chance = 1.0;
        this.direction = false;
        this.customSpeed = 0.1;
        this.motion = new Vector(0, 0, 0);
        this.onlyAtEnd = false;
        this.preTime = 0;
        this.radiTo = 0.0;
        this.radiFrom = this.radi;
        this.protocol = null;
    }


    public Effect setRadi(double radi) {
        this.radi = radi;
        return this;
    }
    public Effect setMillenniumMatrix(ArrayList<Pair<Shape, Vec2>> matrix) {
        this.millenniumMatrix = matrix;
        return this;
    }
    public Effect setSimple(boolean isSimple) {
        this.isSimple = isSimple;
        return this;
    }
    public Effect setStaticAnim(boolean staticAnim) {
        this.staticAnim = staticAnim;
        return this;
    }

    public Effect setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
        return this;
    }
    public Effect setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum Type {
        CIRCULAR, WAVE, RING, MILLENNIUM_CUSTOM
    }
}
