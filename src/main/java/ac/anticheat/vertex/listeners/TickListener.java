package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TickListener implements Listener {
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check.hitTicksToCancel > 0) {
                    check.hitTicksToCancel--;
                }
            }
        }
    }
}
