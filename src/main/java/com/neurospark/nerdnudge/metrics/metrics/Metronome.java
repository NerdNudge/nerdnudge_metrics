package com.neurospark.nerdnudge.metrics.metrics;

import com.fasterxml.jackson.core.JsonProcessingException;

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
        Runnable periodicFlusher = () -> {
            try {
                metricsFlusher.flushMetrics();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
        flushExecutor.scheduleAtFixedRate(periodicFlusher, 60, flushFrequency, TimeUnit.MILLISECONDS);
    }
}
