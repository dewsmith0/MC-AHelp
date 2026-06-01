package io.github.dewsmith0.mcahelp;

import java.util.ArrayList;

public class AhelpRateLimit {
    final int threshold;
    private static final long window = 2000L;
    private final ArrayList<Long> log = new ArrayList<>();
    public AhelpRateLimit(int threshold) {
        this.threshold = threshold;
    }
    public boolean tryInvoke() {
        long time = System.currentTimeMillis();
        while (!log.isEmpty() && (time - log.getLast() > window)) {
            log.removeFirst();
        }
        if (log.size() < threshold) {
            log.add(time);
            return true;
        }
        return false;
    }
}