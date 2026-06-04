package slf4jLogger;

public class ConsoleSink implements LogSink {
    @Override
    public void log(LogLevel level, String message, Marker marker, Object... args) {
        String formatted = formatMessage(level, message, marker, args);
        System.out.println(formatted);
    }

    private String formatMessage(LogLevel level, String message, Marker marker, Object... args) {
        String formattedMessage = String.format(message, args);
        return String.format("%s [%s]%s %s",
                new java.util.Date().toString(),
                level,
                marker != null ? " [" + marker.getName() + "]" : "",
                formattedMessage);
    }
}
