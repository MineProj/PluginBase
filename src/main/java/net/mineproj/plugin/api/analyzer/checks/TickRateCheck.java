package net.mineproj.plugin.api.analyzer.checks;

import net.mineproj.plugin.api.analyzer.AnalyzerManager;
import net.mineproj.plugin.api.analyzer.modules.AnalyzerVL;
import net.mineproj.plugin.api.analyzer.modules.TimerBalancer;
import net.mineproj.plugin.api.events.template.PlayerTickEvent;
import net.mineproj.plugin.millennium.math.Simplification;
import org.bukkit.entity.Player;

public class TickRateCheck {

    public static void check(PlayerTickEvent event) {
        Player p = event.protocol.getPlayer();
        long delay = event.getDelay();
        TimerBalancer timerBalancer = event.protocol.getTimerBalancer();
        timerBalancer.pushDelay(delay);
        double multiply = Simplification.scaleVal((double) 50L / delay, 2);

        AnalyzerVL analyzerVL = event.protocol.getAnalyzerVL();
        if (timerBalancer.isNegativeTimer() && delay > 50) {
            if (multiply < 1.0) {
                punish(analyzerVL, p, "type: negative NET timer value, multiply: " + multiply);
            }
        } else if (delay < 50 && timerBalancer.getResult() > 100) {
            if (multiply >= 1.1) {
                punish(analyzerVL, p, "type: positive NET timer value (balance), multiply: " + multiply);
            }
        } else if (delay < 50 && timerBalancer.getForced() > 8) {
            if (multiply >= 1.1) {
                punish(analyzerVL, p, "type: positive NET timer value (forced), multiply: " + multiply);
            }
        } else if (delay < 50 && timerBalancer.getLatency() > 40) {
            if (multiply >= 1.1) {
                punish(analyzerVL, p, "type: positive NET timer value (latency), multiply: " + multiply);
            }
        } else if (analyzerVL.netTimerVL > 0) {
            analyzerVL.netTimerVL -= 2;
        }
    }

    private static void punish(AnalyzerVL aVl, Player player, String info) {
        aVl.netTimerVL += 8;
        if (aVl.netTimerVL > 100) {
            AnalyzerManager.punish(player, info);
            aVl.netTimerVL = 0;
        } else if (aVl.netTimerVL > 0) {
            aVl.netTimerVL--;
        }
    }
}
