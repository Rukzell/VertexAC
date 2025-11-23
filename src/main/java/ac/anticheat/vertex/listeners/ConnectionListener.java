package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;
import ac.anticheat.vertex.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionListener implements Listener {
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        players.put(player.getEntityId(), player);
        APlayer aPlayer = PlayerDataManager.get(event.getPlayer());
        VertexAC.getCheckManager().registerChecks(event.getPlayer());
        if (aPlayer.toggleAlertsOnJoin() && !aPlayer.sendAlerts() && event.getPlayer().hasPermission("vertex.alerts")) {
            aPlayer.setSendAlerts(true);
            Logger.log(aPlayer.bukkitPlayer, Hex.translateHexColors(Config.getString("messages.alerts.alerts-enabled", "§aAlerts enabled")));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player.getEntityId());
        VertexAC.getCheckManager().unregisterChecks(event.getPlayer());
        PlayerDataManager.remove(event.getPlayer());
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public Player getPlayerById(int id) {
        return players.get(id);
    }
}
