package ac.anticheat.vertex.utils;

import ac.anticheat.vertex.VertexAC;

public class Config {
    private static final VertexAC plugin = VertexAC.get();

    public static String getString(String path, String def) {
        String str = plugin.getConfig().getString(path);
        if (str == null) {
            return def;
        }
        return str;
    }

    public static boolean getBoolean(String path, boolean def) {
        if (plugin.getConfig().contains(path)) {
            return plugin.getConfig().getBoolean(path);
        }
        return def;
    }

    public static int getInt(String path, int def) {
        if (plugin.getConfig().contains(path)) {
            return plugin.getConfig().getInt(path);
        }
        return def;
    }

    public static double getDouble(String path, double def) {
        if (plugin.getConfig().contains(path)) {
            return plugin.getConfig().getDouble(path);
        }
        return def;
    }
}
