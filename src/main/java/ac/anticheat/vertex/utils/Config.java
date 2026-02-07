package ac.anticheat.vertex.utils;

import ac.anticheat.vertex.VertexAC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.Set;

public class Config {
    private static FileConfiguration config;

    public static void init(FileConfiguration fileConfig) {
        config = fileConfig;
    }

    public static String getString(String path, String def) {
        String str = config.getString(path);
        if (str == null) {
            return def;
        }
        return str;
    }

    public static boolean getBoolean(String path, boolean def) {
        if (config.contains(path)) {
            return config.getBoolean(path);
        }
        return def;
    }

    public static int getInt(String path, int def) {
        if (config.contains(path)) {
            return config.getInt(path);
        }
        return def;
    }

    public static double getDouble(String path, double def) {
        if (config.contains(path)) {
            return config.getDouble(path);
        }
        return def;
    }

    public static Set<String> getSection(String path) {
        if (config == null) {
            throw new IllegalStateException("Config not initialized");
        }

        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return Collections.emptySet();
        }

        return section.getKeys(false);
    }
}
