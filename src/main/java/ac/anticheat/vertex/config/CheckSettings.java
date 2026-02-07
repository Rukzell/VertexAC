package ac.anticheat.vertex.config;

import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;

public final class CheckSettings {
    public final boolean enabled;
    public final boolean alerts;
    public final int maxVl;
    public final int decay;
    public final int removeVlAfter;
    public final int hitCancelTicks;
    public final String punishCommand;
    public final double maxBuffer;
    public final double bufferDecrease;
    public final double probaThreshold;

    public CheckSettings(String path) {
        enabled = Config.getBoolean(path + ".enabled", true);
        alerts = Config.getBoolean(path + ".alerts", true);
        maxVl = Config.getInt(path + ".max-vl", 5);
        decay = Config.getInt(path + ".decay", 1);
        removeVlAfter = Config.getInt(path + ".remove-vl-after", 300);
        hitCancelTicks = Config.getInt(path + ".hit-cancel-ticks", 0);
        punishCommand = color(path + ".punish-command");

        maxBuffer = Config.getDouble(path + ".max-buffer", 0);
        bufferDecrease = Config.getDouble(path + ".buffer-decrease", 0);
        probaThreshold = Config.getDouble(path + ".probability-threshold", 0.8);
    }

    private static String color(String path) {
        return Hex.translateHexColors(Config.getString(path, path));
    }
}