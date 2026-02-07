package ac.anticheat.vertex.config;

import ac.anticheat.vertex.utils.Config;

import java.util.HashMap;
import java.util.Map;

public final class ChecksConfig {
    private static ChecksConfig instance;

    private final Map<String, CheckSettings> checks = new HashMap<>();

    private ChecksConfig() {
        loadChecks();
    }

    private void loadChecks() {
        for (String check : Config.getSection("checks")) {
            checks.put(check.toLowerCase(), new CheckSettings("checks." + check));
        }
    }

    public static void load() {
        instance = new ChecksConfig();
    }

    public static ChecksConfig get() {
        if (instance == null) {
            throw new IllegalStateException("ChecksConfig not loaded");
        }
        return instance;
    }

    public CheckSettings get(String checkName) {
        return checks.get(checkName.toLowerCase());
    }
}