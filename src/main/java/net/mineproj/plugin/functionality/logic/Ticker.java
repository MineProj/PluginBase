package net.mineproj.plugin.functionality.logic;

import net.mineproj.plugin.functionality.ballistics.BallisticsPhys;
import net.mineproj.plugin.functionality.effects.EffectsPhys;

public class Ticker {
    public static void run() {
        BallisticsPhys.tick();
        EffectsPhys.tick();
    }
}