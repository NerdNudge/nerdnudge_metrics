package com.neurospark.nerdnudge.metrics.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NerdLogger {

	public static final String WARN = "warn";
	public static final String INFO = "info";
	public static Logger logger;


	static {
		Level.forName("TRACE", 600);
		Level.forName("FATAL", 100);
		Level.forName("ERROR", 200);
		Level.forName("WARN", 300);
		Level.forName("INFO", 400);
		Level.forName("DEBUG", 500);
		Level.forName("NERD_RUNTIME", 100);
	}

	public NerdLogger() {
		logger = LogManager.getLogger("com.neurospark.nerdnudge.metrics");
	}

	public void log(String level, String message, Object... params) {
		logger.log(Level.forName(level.toUpperCase(), 400), message, params);
	}

	public void log(String level, String message) {
		logger.log(Level.forName(level.toUpperCase(), 400), message);
	}


	public void log(String level, String message, Exception e) {
		logger.log(Level.forName(level.toUpperCase(), 400), message, e);
	}
}
