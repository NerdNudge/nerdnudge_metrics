package com.neurospark.nerdnudge.metrics.metrics;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Metronome {
    public static boolean defaultMinMetrics = true;
    public static boolean defaultMaxMetrics = true;
    public static Set< String > minInclusions = new HashSet<>();
    public static Set< String > maxInclusions = new HashSet<>();

    public static void initiateMetrics(int flushFrequency) {
        MetricFlush metricsFlusher = new MetricFlush(flushFrequency);

        ScheduledExecutorService flushExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicFlusher = () -> metricsFlusher.flushMetrics();
        flushExecutor.scheduleAtFixedRate(periodicFlusher, 60, flushFrequency, TimeUnit.MILLISECONDS);
    }
}
