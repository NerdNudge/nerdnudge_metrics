package com.neurospark.nerdnudge.metrics.metrics;

import com.neurospark.nerdnudge.metrics.logging.NerdLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MetricFlush {
    public static NerdLogger logger = new NerdLogger();

    final static Logger PROCESSING_STATS_REPORT_LOGGER = LogManager.getLogger( "PROCESSING_STATS_REPORT" );
    final static Level PROCESSING_STATS_REPORT = Level.forName( "PROCESSING_STATS_REPORT", 50 );

    /*
    TODO:
    1. Get the configurations, for example, flush frequency.
    2. Add the right logger and level.
    3. Add a max/min latency for metrics that are not of type COUNT unit.
     */

    public static int FLUSH_FREQUENCY = 60000;

    public MetricFlush(int flushFrequency) {
        FLUSH_FREQUENCY = flushFrequency;
    }

    public void flushMetrics() {
        logger.log( "info",  "{} Nerd Metrics: Flusher woke up !!!", new Date() );

        Map< String, Double > accumulatedMetrics = getAccumulatedMetricsAcrossThreads();
        Map< String, Double > finalOutput = getAggregatedMetricsAcrossThreads( accumulatedMetrics );

        if(finalOutput.isEmpty()) {
            logger.log("info", "{} Nerd Metrics: No requests in this frequency !!!", new Date());
            return;
        }

        logger.log("info", "Nerd Metrics: FINAL OUTPUT METRICS STATS: {}", finalOutput);
        PROCESSING_STATS_REPORT_LOGGER.log(PROCESSING_STATS_REPORT, finalOutput);
    }

    private static boolean isMaxMetricRequired( String key ) {
        return Metronome.defaultMaxMetrics || Metronome.maxInclusions.contains( key );
    }


    private Map< String, Double > getAccumulatedMetricsAcrossThreads() {
        Set< Long > threadIds = MetricAggregator.threadWiseMetrics.keySet();
        Iterator< Long > threadIterator = threadIds.iterator();
        Map< String, Double > accumulatedMetrics = new HashMap<>();
        while( threadIterator.hasNext() ) {
            Long currentThreadId = threadIterator.next();
            ConcurrentHashMap< String, Double > currentThreadMetrics = MetricAggregator.threadWiseMetrics.get( currentThreadId );
            Iterator< Map.Entry< String, Double > > metricsIterator = currentThreadMetrics.entrySet().iterator();
            while( metricsIterator.hasNext() ) {
                Map.Entry< String, Double > thisMetric = metricsIterator.next();
                String key = thisMetric.getKey();
                if( key.startsWith( MetricAggregator.COUNT_PREFIX ) || key.startsWith( MetricAggregator.MAX_PREFIX ) )
                    continue;

                accumulatedMetrics.put( key, accumulatedMetrics.getOrDefault( key, 0.0 ) + currentThreadMetrics.get( key ) );
                if( currentThreadMetrics.containsKey( MetricAggregator.COUNT_PREFIX + key ) ) {   // This is not a COUNT type metric.
                    accumulatedMetrics.put(MetricAggregator.COUNT_PREFIX + key, accumulatedMetrics.getOrDefault(MetricAggregator.COUNT_PREFIX + key, 0.0) + currentThreadMetrics.get(MetricAggregator.COUNT_PREFIX + key));

                    if( isMaxMetricRequired( key ) ) {
                        double existingMaxValue = accumulatedMetrics.getOrDefault(MetricAggregator.MAX_PREFIX + key, Double.MIN_VALUE);
                        double currentMax = currentThreadMetrics.getOrDefault(MetricAggregator.MAX_PREFIX + key, Double.MIN_VALUE);
                        if (currentMax >= existingMaxValue)
                            accumulatedMetrics.put(MetricAggregator.MAX_PREFIX + key, currentMax);
                    }
                }
            }
            MetricAggregator.threadWiseMetrics.put( currentThreadId, new ConcurrentHashMap<>() );
        }
        return accumulatedMetrics;
    }


    private Map< String, Double > getAggregatedMetricsAcrossThreads( Map< String, Double > accumulatedMetrics ) {
        Iterator< Map.Entry< String, Double > > accumulatedMetricsIterator = accumulatedMetrics.entrySet().iterator();
        Map< String, Double > finalOutput = new HashMap<>();
        while( accumulatedMetricsIterator.hasNext() ) {
            Map.Entry< String, Double > currentMetric = accumulatedMetricsIterator.next();
            String key = currentMetric.getKey();
            if( key.startsWith( MetricAggregator.COUNT_PREFIX ) || key.startsWith( MetricAggregator.MAX_PREFIX ) )
                continue;

            if( accumulatedMetrics.containsKey( MetricAggregator.COUNT_PREFIX + key ) ) {   // This is not a COUNT type metric.
                finalOutput.put("avg_" + key, ((accumulatedMetrics.get(key) / accumulatedMetrics.get(MetricAggregator.COUNT_PREFIX + key)) * 100 ) / 100);
                finalOutput.put("count_" + key, accumulatedMetrics.get(MetricAggregator.COUNT_PREFIX + key));
                if( isMaxMetricRequired( key ) )
                    finalOutput.put("max_" + key, accumulatedMetrics.get(MetricAggregator.MAX_PREFIX + key));
            }
            else {
                finalOutput.put( key, accumulatedMetrics.get( key ) );
                finalOutput.put( "ops_per_sec_" + key, ( ( accumulatedMetrics.get( key ) / ( FLUSH_FREQUENCY / 1000 ) ) * 100 ) / 100 );
            }
        }

        return finalOutput;
    }
}
