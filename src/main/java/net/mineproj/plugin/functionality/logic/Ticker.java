package net.mineproj.plugin.functionality.logic;

import net.mineproj.plugin.functionality.ballistics.BallisticsPhys;

public class Ticker {
    public static void run() {
        BallisticsPhys.tick();
    }
}