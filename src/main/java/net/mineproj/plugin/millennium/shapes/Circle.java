package net.mineproj.plugin.millennium.shapes;

import lombok.Data;
import net.mineproj.plugin.millennium.math.BuildSpeed;
import net.mineproj.plugin.millennium.math.GeneralMath;
import net.mineproj.plugin.millennium.vectors.Vec2;

import java.util.HashSet;
import java.util.Set;


/*
Draw a circle to HashSet
 */

@Data
public class Circle {
    private final Set<Vec2> vectors;
    public Circle() {
        this.vectors = new HashSet<>();
    }

    // round
    // 1 - circle
    // 0 - square

    // res
    // 1 - 360
    // 2 - 180
    // 4 - 90
    public void build(BuildSpeed s, double size, double round, int res) {
        vectors.clear();
        Vec2 firstVector = null;
        for (int k = 0; k <= 720; k += res) {
            double x = GeneralMath.sin((float) Math.toRadians(k), s) * res;
            double y = GeneralMath.cos((float) Math.toRadians(k), s) * res;
            double xj = x / Math.max(Math.abs(x), 1) * round;
            double yj = y / Math.max(Math.abs(y), 1) * round;
            Vec2 v2 = new Vec2(xj * size, yj * size);
            if (k == 0) {
                firstVector = v2;
            } else if (v2.compare(firstVector)) {
                break;
            }
            vectors.add(v2);
        }
    }

}
