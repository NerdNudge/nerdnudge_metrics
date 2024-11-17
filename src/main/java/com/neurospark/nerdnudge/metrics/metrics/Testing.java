package com.neurospark.nerdnudge.metrics.metrics;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class Testing {
    static final Logger ACCESS_LOGGER = LogManager.getLogger("ACCESS_LOGGER");
    public static void main(String[] args) {
        Testing testing = new Testing();
        testing.testlogs();
    }

    private void testlogs() {
        System.out.println("A sysout to begin validation of the logs !!!");
        ACCESS_LOGGER.log(Level.WARN, "This is a sample warn log at: " + new Date());
        ACCESS_LOGGER.log(Level.INFO, "This is a sample info log at: " + new Date());
        ACCESS_LOGGER.log(Level.ERROR, "This is a sample error log at: " + new Date());
        System.out.println("A sysout to valid the logs !!!");
    }
}
