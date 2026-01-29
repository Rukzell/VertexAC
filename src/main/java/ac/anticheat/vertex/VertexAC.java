package ac.anticheat.vertex;

import ac.anticheat.vertex.commands.VertexCommand;
import ac.anticheat.vertex.listeners.ConnectionListener;
import ac.anticheat.vertex.listeners.InventoryListener;
import ac.anticheat.vertex.listeners.PacketCheckListener;
import ac.anticheat.vertex.listeners.TickListener;
import ac.anticheat.vertex.managers.CheckManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class VertexAC extends JavaPlugin {
    private static VertexAC instance;
    private ConnectionListener connectionListener;
    private CheckManager checkManager;

    public static VertexAC get() {
        return instance;
    }

    public static ConnectionListener getConnectionListener() {
        return instance.connectionListener;
    }

    public static CheckManager getCheckManager() {
        return instance.checkManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        connectionListener = new ConnectionListener();
        checkManager = new CheckManager();
        saveDefaultConfig();

        registerPacketListeners();

        getLogger().info("Vertex loaded");

        getServer().getOnlinePlayers().forEach(player -> {
            connectionListener.getPlayers().put(player.getEntityId(), player);
            checkManager.registerChecks(player);
        });

        new VertexCommand(this);
        registerBukkitListeners();

        // метрики
        Metrics metrics = new Metrics(this, 27725);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> checkManager.unregisterChecks(player));
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketCheckListener(), PacketListenerPriority.HIGHEST);

        PacketEvents.getAPI().getEventManager().registerListener(
                new InventoryListener(), PacketListenerPriority.NORMAL);
    }

    private void registerBukkitListeners() {
        getServer().getPluginManager().registerEvents(new TickListener(), this);
        getServer().getPluginManager().registerEvents(connectionListener, this);
    }
}
