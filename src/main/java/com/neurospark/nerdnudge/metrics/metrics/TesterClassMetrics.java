package com.neurospark.nerdnudge.metrics.metrics;

import java.util.Random;

public class TesterClassMetrics {

    public static void main(String[] args) throws InterruptedException {
        System.out.println( "Starting the tester !!!" );

        /*Metronome.defaultMinMetrics = false;
        Metronome.defaultMaxMetrics = false;

        //Metronome.minInclusions.add( "E2E_Latency" );
        Metronome.maxInclusions.add( "E2E_Latency" );*/

        Metronome.initiateMetrics(10000);





        int loopCount = 1000;
        for( int i = 0; i < loopCount; i ++ ) {
            Thread.sleep( getRandom( 10 ) + 1000 );

            new Metric.MetricBuilder()
                    .setName( "ABC_COUNTS" )
                    .setUnit( Metric.Unit.COUNT )
                    .setValue( 1 )
                    .build();
            //System.out.println( "ABC Metric added: " + i );

            new Metric.MetricBuilder()
                    .setName( "ERR_COUNTS" )
                    .setUnit( Metric.Unit.ERROR )
                    .setValue( 1 )
                    .build();


            new Metric.MetricBuilder()
                    .setName( "E2E_Latency" )
                    .setUnit( Metric.Unit.MILLISECONDS )
                    .setValue( getRandom( 300 ) )
                    .build();
            //System.out.println( "E2E Metric added: " + i );


            new Metric.MetricBuilder()
                    .setName( "F1_Latency" )
                    .setUnit( Metric.Unit.MILLISECONDS )
                    .setValue( getRandom( 300 ) )
                    .build();
            //System.out.println( "F1 Metric added: " + i );
        }
    }



    private static int getRandom( int bound ) {
        return Math.abs( new Random().nextInt( bound ) );
    }
}
