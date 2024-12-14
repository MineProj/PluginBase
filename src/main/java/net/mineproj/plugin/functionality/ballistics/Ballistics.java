package net.mineproj.plugin.functionality.ballistics;

import lombok.Data;
import net.mineproj.plugin.functionality.effects.Effect;
import net.mineproj.plugin.protocol.data.PlayerProtocol;
import org.bukkit.Location;
import org.bukkit.Particle;

@Data
public class Ballistics {
    private double distance;
    private double targetDistance;
    private double speed;
    private double velocityRange;
    private float yaw;
    private float pitch;
    private float accuracy;
    private int damage;
    private float explosive;
    private ExplosionType explosionType;
    private Effect effect, semiEffect;
    private Particle particle;
    private Location location;
    private PlayerProtocol creator;

    private boolean living;
    private boolean heavy;
    private boolean velocityRealisticPostProcessing;

    private double weight, weightInterpolation;

    public Ballistics(double distance, double targetDistance, double speed, float yaw, float pitch, float accuracy, int damage, Particle particle, Location location) {
        this.distance = distance;
        this.targetDistance = targetDistance;
        this.accuracy = accuracy;
        this.speed = speed;
        this.yaw = (float) (yaw + ((Math.random() * accuracy) * 2) - accuracy);
        this.pitch = (float) (pitch + ((Math.random() * accuracy) * 2) - accuracy);
        this.damage = damage;
        this.particle = particle;
        this.location = location.clone();
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
        this.creator = null;
        this.living = true;
        this.weight = 0.004;
        this.weightInterpolation = 0;
        this.heavy = false;
        this.explosive = 0;
        this.explosionType = ExplosionType.VANILLA;
        this.effect = null;
        this.semiEffect = null;
        this.velocityRange = 5;
        this.velocityRealisticPostProcessing = true;
    }

    public Ballistics setCreator(PlayerProtocol player) {
        this.creator = player;
        return this;
    }

    public Ballistics setEffect(Effect effect) {
        this.effect = effect;
        return this;
    }

    public Ballistics setHeavy(boolean heavy) {
        this.heavy = heavy;
        return this;
    }
    public Ballistics setVelocityRange(double velocityRange) {
        this.velocityRange = velocityRange;
        return this;
    }
    public Ballistics setExplosionType(ExplosionType explosionType) {
        this.explosionType = explosionType;
        return this;
    }

    public Ballistics setExplosive(float explosive) {
        this.explosive = explosive;
        return this;
    }

    public Ballistics customWeight(double w) {
        this.weight = w;
        return this;
    }
    public Ballistics setVelocityRPP(boolean velocityRealisticPostProcessing) {
        this.velocityRealisticPostProcessing = velocityRealisticPostProcessing;
        return this;
    }

    public void outOfDistance() {
        if (this.weight < 2.4) {
            this.weight += weightInterpolation;
            if (weightInterpolation < 0.25) weightInterpolation += 0.001;
        }
    }

    public enum ExplosionType {
        VANILLA,
        VELOCITY,
        ATOMIC,
        CHAIN_ATOMIC
    }

}