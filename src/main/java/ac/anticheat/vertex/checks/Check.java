package ac.anticheat.vertex.checks;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.beauty.PunishEffect;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;
import ac.anticheat.vertex.utils.MessageUtils;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public abstract class Check {
    public final APlayer aPlayer;
    private final String name;
    private final String displayName;
    private final boolean experimental;
    private final String punishCommand;
    private final Plugin plugin;
    private boolean enabled;
    private boolean alert;
    private int violations;
    private int decay = Config.getInt(getConfigPath() + ".decay", 1);
    private int maxViolations;
    private String prefix = Config.getString("vertex.prefix", "vertex.prefix");
    private String rawMessage = Config.getString("alerts.message", "alerts.message");
    private long lastFlagTime = 0;
    private boolean data;

    private long delayTaskRawDelay = Config.getInt(getConfigPath() + ".remove-violations-after", 300);
    private long delayTaskDelay = delayTaskRawDelay * 20;

    private BukkitTask decayTask;

    public Check(String name, String type, APlayer aPlayer, boolean data) {
        this.name = name + type.replace("(", "").replace(")", "");
        this.displayName = name + " " + type;
        this.aPlayer = aPlayer;
        this.enabled = Config.getBoolean("checks." + name + ".enabled", true);
        this.experimental = name.contains("*");
        this.punishCommand = Config.getString("checks." + name + ".punish-command", "kick {player} #ff7b42Unfair Advantage");
        this.alert = Config.getBoolean("checks." + name + ".alert", true);
        this.maxViolations = Config.getInt("checks." + name + ".max-violations", 10000);
        this.data = data;
        this.plugin = VertexAC.get();

        startDecayTask();
    }

    public void flag() {
        flag("");
    }

    public void flag(String verbose) {
        if (!aPlayer.bukkitPlayer.isOnline()) return;

        long now = System.currentTimeMillis();

        if (now - lastFlagTime < 1000) {
            return;
        }

        lastFlagTime = now;

        if (violations < maxViolations) {
            violations++;
            aPlayer.globalVl++;
            aPlayer.kaNpcVl++;
        }

        Component message = MessageUtils.parseMessage(
                rawMessage
                        .replace("{prefix}", prefix)
                        .replace("{player}", aPlayer.bukkitPlayer.getName())
                        .replace("{check}", displayName)
                        .replace("{violations}", String.valueOf(violations))
        ).hoverEvent(
                HoverEvent.showText(
                        MessageUtils.parseMessage(verbose)
                )
        );
        if (alert && violations <= maxViolations) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("vertex.alerts")) {
                    APlayer targetData = PlayerDataManager.get(online);
                    if (targetData != null && targetData.sendAlerts()) {
                        online.sendMessage(message);
                    }
                }
            }
        }

        if (getViolations() >= getMaxViolations() && !aPlayer.bukkitPlayer.isOp()) {
            runSync(() -> PunishEffect.start(aPlayer.bukkitPlayer));
            dispatchCommand(Hex.translateHexColors(punishCommand));
        }
    }

    private void dispatchCommand(String command) {
        String finalCommand = Hex.translateHexColors(command);

        runSync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.replace("{player}", aPlayer.bukkitPlayer.getName()));
            resetViolations();
        });
    }

    private void startDecayTask() {
        this.decayTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (violations > 0) {
                    violations -= decay;
                    if (violations < 0) violations = 0;
                }
            }
        }.runTaskTimer(VertexAC.get(), delayTaskDelay, delayTaskDelay);
    }

    public void runSync(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void cancelDecayTask() {
        if (decayTask != null) {
            decayTask.cancel();
        }
    }

    public void resetViolations() {
        this.violations = 0;
        aPlayer.globalVl = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getConfigPath() {
        return "checks." + name;
    }

    public boolean alert() {
        return alert;
    }

    public int getViolations() {
        return violations;
    }

    public int getMaxViolations() {
        return maxViolations;
    }

    public boolean isData() {
        return data;
    }

    protected double probability(double value, double min, double max, double sensitivity, boolean inverted) {
        if (max <= min) return 0.0;

        double x = (value - min) / (max - min);
        x = Math.max(0.0, Math.min(1.0, x));

        if (inverted) x = 1.0 - x;

        return 1.0 / (1.0 + Math.exp(-sensitivity * (x - 0.5)));
    }

    public void reload() {
        this.enabled = Config.getBoolean(getConfigPath() + ".enabled", true);
        this.alert = Config.getBoolean(getConfigPath() + ".alert", true);
        this.maxViolations = Config.getInt(getConfigPath() + ".max-violations", 10000);
        this.decay = Config.getInt(getConfigPath() + ".decay", 1);
        this.delayTaskRawDelay = Config.getInt(getConfigPath() + ".remove-violations-after", 300);
        this.delayTaskDelay = delayTaskRawDelay * 20;
        this.prefix = Config.getString("vertex.prefix", "vertex.prefix");
        this.rawMessage = Config.getString("alerts.message", "alerts.message");
    }
}