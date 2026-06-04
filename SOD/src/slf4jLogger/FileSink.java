package slf4jLogger;

public class FileSink implements LogSink {
    private final String filePath;

    public FileSink(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void log(LogLevel level, String message, Marker marker, Object... args) {
        String formatted = formatMessage(level, message, marker, args);
        // In a real implementation, this would append to a file
        System.out.println("Writing to file [" + filePath + "]: " + formatted);
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