package net.mineproj.plugin.millennium.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeneralMath {
    public static float sin(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                        ? (float) Math.sin(value)
                        : (s.equals(BuildSpeed.FAST)
                        ? FastMath.sin(value)
                        : FastMath.fastCos(value));
    }
    public static float cos(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                        ? (float) Math.cos(value)
                        : (s.equals(BuildSpeed.FAST)
                        ? FastMath.cos(value)
                        : FastMath.fastCos(value));
    }
}
