package slf4jLogger;

public class SimpleMarker implements Marker {
    private final String name;

    public SimpleMarker(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}