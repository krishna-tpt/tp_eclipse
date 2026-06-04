package slf4jLogger;

//Logger Implementation
public class LoggerImpl implements Logger {
    private final String name;
    private final LoggerConfig config;
    private static LoggerImpl instance;

    private LoggerImpl(String name, LoggerConfig config) {
        this.name = name;
        this.config = config;
    }

    public static synchronized Logger getLogger(String name, LoggerConfig config) {
        if (instance == null) {
            instance = new LoggerImpl(name, config);
        }
        return instance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return config.getMinimumLevel().ordinal() <= LogLevel.TRACE.ordinal();
    }

    @Override
    public boolean isDebugEnabled() {
        return config.getMinimumLevel().ordinal() <= LogLevel.DEBUG.ordinal();
    }

    @Override
    public boolean isInfoEnabled() {
        return config.getMinimumLevel().ordinal() <= LogLevel.INFO.ordinal();
    }

    @Override
    public boolean isWarnEnabled() {
        return config.getMinimumLevel().ordinal() <= LogLevel.WARN.ordinal();
    }

    @Override
    public boolean isErrorEnabled() {
        return config.getMinimumLevel().ordinal() <= LogLevel.ERROR.ordinal();
    }

    @Override
    public void trace(String msg) {
        log(LogLevel.TRACE, null, msg);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(LogLevel.TRACE, null, format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg) {
        log(LogLevel.TRACE, marker, msg);
    }

    @Override
    public void debug(String msg) {
        log(LogLevel.DEBUG, null, msg);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(LogLevel.DEBUG, null, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg) {
        log(LogLevel.DEBUG, marker, msg);
    }

    @Override
    public void info(String msg) {
        log(LogLevel.INFO, null, msg);
    }

    @Override
    public void info(String format, Object... arguments) {
        log(LogLevel.INFO, null, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg) {
        log(LogLevel.INFO, marker, msg);
    }

    @Override
    public void warn(String msg) {
        log(LogLevel.WARN, null, msg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(LogLevel.WARN, null, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg) {
        log(LogLevel.WARN, marker, msg);
    }

    @Override
    public void error(String msg) {
        log(LogLevel.ERROR, null, msg);
    }

    @Override
    public void error(String format, Object... arguments) {
        log(LogLevel.ERROR, null, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg) {
        log(LogLevel.ERROR, marker, msg);
    }

    private void log(LogLevel level, Marker marker, String format, Object... args) {
        if (level.ordinal() >= config.getMinimumLevel().ordinal()) {
            for (LogSink sink : config.getSinks()) {
                sink.log(level, format, marker, args);
            }
        }
    }
}