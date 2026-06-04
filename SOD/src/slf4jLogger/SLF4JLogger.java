package slf4jLogger;

// Enum for Log Levels
	enum LogLevel {
	    TRACE, DEBUG, INFO, WARN, ERROR
	}

	// File Sink Implementation
	// Usage Example
	public class SLF4JLogger {
	    public static void main(String[] args) {
	        // Configure logger
	        LoggerConfig config = new LoggerConfig(LogLevel.TRACE);
	        config.addSink(new ConsoleSink());
	        config.addSink(new FileSink("app.log"));

	        // Create logger
	        Logger logger = LoggerImpl.getLogger("ApplicationLogger", config);

	        // Example logging
	        logger.trace("Trace message");
	        logger.debug("Debug message with param: {}", "value1");
	        logger.info("Info message with params: {} and {}", "value1", "value2");
	        logger.warn("Warning message");

	        // Logging with marker
	        Marker marker = new SimpleMarker("USER_ACTION");
	        logger.info(marker, "User performed action");
	        logger.error("Error occurred: {}", "something went wrong");
	    }
	}