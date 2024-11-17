package com.neurospark.nerdnudge.metrics.metrics;

import java.util.concurrent.ConcurrentHashMap;

public class MetricAggregator {

    private static MetricAggregator metricAggregator = null;
    public static ConcurrentHashMap< Long, ConcurrentHashMap< String, Double > > threadWiseMetrics = new ConcurrentHashMap<>();
    public static final String COUNT_PREFIX = "__COUNT_";
    public static final String MAX_PREFIX = "__MAX_";

    private MetricAggregator() {
    };

    public static MetricAggregator getInstance() {
        if( metricAggregator == null )
            metricAggregator = new MetricAggregator();

        return metricAggregator;
    }

    public void aggregate( Metric metric ) {
        long id = Thread.currentThread().getId();
        ConcurrentHashMap< String, Double > currentThreadMetrics = threadWiseMetrics.getOrDefault( id, new ConcurrentHashMap<>() );
        switch ( metric.getUnit() ) {
            case COUNT:
            case ERROR:
                currentThreadMetrics.put( metric.getName(), currentThreadMetrics.getOrDefault( metric.getName(), 0.0 ) + metric.getValue() );
                break;
            case MILLISECONDS:
            case BYTES:
                currentThreadMetrics.put( metric.getName(), currentThreadMetrics.getOrDefault( metric.getName(), 0.0 ) + metric.getValue() );
                currentThreadMetrics.put( COUNT_PREFIX + metric.getName(), currentThreadMetrics.getOrDefault( COUNT_PREFIX + metric.getName(), 0.0 ) + 1 );
                double currentMax = currentThreadMetrics.getOrDefault( MAX_PREFIX + metric.getName(), Double.MIN_VALUE );
                if( metric.getValue() > currentMax )
                    currentThreadMetrics.put( MAX_PREFIX + metric.getName(), metric.getValue() );

                break;
            default:
                throw new IllegalStateException( "Unexpected value: " + metric.getUnit() );
        }
        threadWiseMetrics.put( id, currentThreadMetrics );
    }
}
