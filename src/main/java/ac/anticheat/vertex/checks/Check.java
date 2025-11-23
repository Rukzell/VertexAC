package ac.anticheat.vertex.checks;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.api.events.impl.ViolationEvent;
import ac.anticheat.vertex.beauty.PunishEffect;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;
import ac.anticheat.vertex.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Check {
    private final String name;
    protected final APlayer aPlayer;
    private boolean enabled;
    private final boolean experimental;
    private final String punishCommand;
    private boolean alert;
    private int violations;
    private int decay = Config.getInt(getConfigPath() + ".decay", 1);
    private int maxViolations;
    public int hitCancelTicks;
    public int hitTicksToCancel;
    private Plugin plugin;
    private String prefix = Config.getString("vertex.prefix", "vertex.prefix");
    private String rawMessage = Config.getString("alerts.message", "alerts.message");

    private long delayTaskRawDelay = Config.getInt(getConfigPath() + ".remove-violations-after", 300);
    private long delayTaskDelay = delayTaskRawDelay * 20;

    private BukkitTask decayTask;

    public Check(String name, APlayer aPlayer) {
        this.name = name;
        this.aPlayer = aPlayer;
        this.enabled = Config.getBoolean("checks." + name + ".enabled", true);
        this.experimental = name.contains("*");
        this.punishCommand = Config.getString("checks." + name + ".punish-command", "kick {player} #ff7b42Unfair Advantage");
        this.alert = Config.getBoolean("checks." + name + ".alert", true);
        this.maxViolations = Config.getInt("checks." + name + ".max-violations", 10);
        this.hitCancelTicks = Config.getInt("checks." + name + ".hit-cancel-ticks", 0);
        this.hitTicksToCancel = 0;
        this.plugin = VertexAC.getInstance();

        startDecayTask();
    }

    protected void flag() {
        flag("");
    }

    protected void flag(String verbose) {
        if (!experimental) {
            this.hitTicksToCancel += hitCancelTicks;
        }

        if (violations < maxViolations) {
            violations++;
            VertexAC.getEventManager().call(new ViolationEvent(aPlayer.bukkitPlayer, this, violations));
            aPlayer.globalVl++;
            aPlayer.kaNpcVl++;
        }

        Component message = MessageUtils.parseMessage(
                rawMessage
                        .replace("{prefix}", prefix)
                        .replace("{player}", aPlayer.bukkitPlayer.getName())
                        .replace("{check}", name)
                        .replace("{violations}", String.valueOf(violations))
        ).hoverEvent(
                HoverEvent.showText(
                        MessageUtils.parseMessage(verbose)
                )
        );
        if (alert && violations <= maxViolations && aPlayer.bukkitPlayer.isOnline()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("vertex.alerts")) {
                    APlayer targetData = PlayerDataManager.get(online);
                    if (targetData != null && targetData.sendAlerts()) {
                        online.sendMessage(message);
                    }
                }
            }
        }

        if (getViolations() >= getMaxViolations()) {
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
        }.runTaskTimer(VertexAC.getInstance(), delayTaskDelay, delayTaskDelay);
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
    
    public void reload() {
        this.enabled = Config.getBoolean(getConfigPath() + ".enabled", true);
        this.alert = Config.getBoolean(getConfigPath() + ".alert", true);
        this.maxViolations = Config.getInt(getConfigPath() + ".max-violations", 10);
        this.hitCancelTicks = Config.getInt(getConfigPath() + ".hit-cancel-ticks", 20);
        this.decay = Config.getInt(getConfigPath() + ".decay", 1);
        this.delayTaskRawDelay = Config.getInt(getConfigPath() + ".remove-violations-after", 300);
        this.delayTaskDelay = delayTaskRawDelay * 20;
        this.prefix = Config.getString("vertex.prefix", "vertex.prefix");
        this.rawMessage = Config.getString("alerts.message", "alerts.message");
    }
}