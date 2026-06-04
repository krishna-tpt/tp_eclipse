package slf4jLogger;

//Logger Interface (SLF4J-like)
public interface Logger {
    String getName();
    boolean isTraceEnabled();
    boolean isDebugEnabled();
    boolean isInfoEnabled();
    boolean isWarnEnabled();
    boolean isErrorEnabled();
    void trace(String msg);
    void trace(String format, Object... arguments);
    void trace(Marker marker, String msg);
    void debug(String msg);
    void debug(String format, Object... arguments);
    void debug(Marker marker, String msg);
    void info(String msg);
    void info(String format, Object... arguments);
    void info(Marker marker, String msg);
    void warn(String msg);
    void warn(String format, Object... arguments);
    void warn(Marker marker, String msg);
    void error(String msg);
    void error(String format, Object... arguments);
    void error(Marker marker, String msg);
}