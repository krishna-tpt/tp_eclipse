package slf4jLogger;

//Interface for Log Sink (Output destination)
public interface LogSink {
    void log(LogLevel level, String message, Marker marker, Object... args);
}
