package ac.anticheat.vertex.config;


public final class MessagesConfig {
    private static MessagesConfig instance;

    public final Messages messages;

    private MessagesConfig() {
        this.messages = Messages.load();
    }

    public static void load() {
        instance = new MessagesConfig();
    }

    public static MessagesConfig get() {
        if (instance == null) {
            throw new IllegalStateException("Configuration not loaded");
        }
        return instance;
    }
}