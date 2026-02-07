package ac.anticheat.vertex.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class RunningMode {

    private final int capacity;
    private final LinkedList<Double> window;

    public RunningMode(int capacity) {
        this.capacity = capacity;
        this.window = new LinkedList<>();
    }

    public void add(double value) {
        if (window.size() >= capacity) {
            window.removeFirst();
        }
        window.add(value);
    }

    public int size() {
        return window.size();
    }

    public ModeResult getMode() {
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : window) {
            freq.put(v, freq.getOrDefault(v, 0) + 1);
        }

        double mode = 0;
        int count = 0;

        for (Map.Entry<Double, Integer> entry : freq.entrySet()) {
            if (entry.getValue() > count) {
                count = entry.getValue();
                mode = entry.getKey();
            }
        }

        return new ModeResult(mode, count);
    }

    public static class ModeResult {
        public final double value;
        public final int count;

        public ModeResult(double value, int count) {
            this.value = value;
            this.count = count;
        }
    }
}