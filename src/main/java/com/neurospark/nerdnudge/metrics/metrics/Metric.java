package com.neurospark.nerdnudge.metrics.metrics;

import lombok.Getter;

public class Metric {
    @Getter private String name;
    @Getter private Unit unit;
    @Getter private double value;

    private Metric( MetricBuilder metricBuilder ) {
        this.name = metricBuilder.name;
        this.unit = metricBuilder.unit;
        this.value = metricBuilder.value;
    }

    public enum Unit {
        MILLISECONDS,
        BYTES,
        COUNT,
        ERROR
    }


    public static class MetricBuilder {
        private String name;
        private Unit unit;
        private double value;

        public MetricBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MetricBuilder setUnit(Unit unit) {
            this.unit = unit;
            return this;
        }

        public MetricBuilder setValue(double value) {
            this.value = value;
            return this;
        }

        public Metric build() {
            if( name == null || unit == null ) {
                throw new IllegalStateException( "Required fields name, unit are not set" );
            }
            Metric thisMetric = new Metric( this );
            MetricAggregator.getInstance().aggregate( thisMetric );
            return thisMetric;
        }
    }
}

