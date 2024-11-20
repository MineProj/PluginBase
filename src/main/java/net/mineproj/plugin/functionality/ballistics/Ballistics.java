package net.mineproj.plugin.functionality.ballistics;

import lombok.Data;
import net.mineproj.plugin.api.data.PlayerProtocol;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

@Data
public class Ballistics {
    private double distance;
    private double targetDistance;
    private double speed;
    private float yaw;
    private float pitch;
    private float accuracy;
    private int damage;
    private float explosive;
    private Particle particle;
    private Location location;
    private PlayerProtocol creator;

    private boolean living;
    private boolean heavy;

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
    }

    public Ballistics setCreator(PlayerProtocol player) {
        this.creator = player;
        return this;
    }

    public Ballistics setHeavy(boolean heavy) {
        this.heavy = heavy;
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

    public void outOfDistance() {
        if (this.weight < 2.4) {
            this.weight += weightInterpolation;
            if (weightInterpolation < 0.25) weightInterpolation += 0.001;
        }
    }

}