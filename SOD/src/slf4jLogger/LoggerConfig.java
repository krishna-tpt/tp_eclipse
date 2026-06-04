package slf4jLogger;

import java.util.ArrayList;
import java.util.List;

//Logger Configuration
public class LoggerConfig {
    private LogLevel minimumLevel;
    private final List<LogSink> sinks;

    public LoggerConfig(LogLevel minimumLevel) {
        this.minimumLevel = minimumLevel;
        this.sinks = new ArrayList<>();
    }

    public void addSink(LogSink sink) {
        sinks.add(sink);
    }

    public LogLevel getMinimumLevel() {
        return minimumLevel;
    }

    public List<LogSink> getSinks() {
        return sinks;
    }
}