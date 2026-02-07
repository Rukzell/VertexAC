package ac.anticheat.vertex.config;

import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;

public final class Messages {
    public final String PREFIX;
    public final String ALERT_MESSAGE;
    public final String NO_PERMISSION;
    public final String ALERTS_ENABLED;
    public final String ALERTS_DISABLED;
    public final String CHECKS_HEADER;
    public final String CHECK;
    public final String CONFIG_RELOAD;
    public final String CONFIG_RELOADED;
    public final String COMMANDS;

    private Messages() {
        PREFIX = Config.getString("vertex.prefix", "vertex.prefix");
        ALERT_MESSAGE = Config.getString("alerts.message", "alerts.message");
        NO_PERMISSION = Config.getString("messages.no-permission", "messages.no-permission");
        ALERTS_ENABLED = Config.getString("messages.alerts.alerts-enabled", "messages.alerts.alerts-enabled");
        ALERTS_DISABLED = Config.getString("messages.alerts.alerts-disabled", "messages.alerts.alerts-disabled");
        CHECKS_HEADER = Config.getString("messages.checks.header", "messages.checks.header");
        CHECK = Config.getString("messages.checks.check", "messages.checks.check");
        CONFIG_RELOAD = Config.getString("messages.config.config-reload", "messages.config.config-reload");
        CONFIG_RELOADED = Config.getString("messages.config.config-reloaded", "messages.config.config-reloaded");
        COMMANDS = Config.getString("messages.commands", "messages.commands");
    }

    public static Messages load() {
        return new Messages();
    }

    private static String color(String path) {
        return Hex.translateHexColors(Config.getString(path, path));
    }
}