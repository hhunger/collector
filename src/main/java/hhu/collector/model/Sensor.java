package hhu.collector.model;

public class Sensor {
    private String name;
    private String id;

    public Sensor(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Sensor '%s', '%s'", name, id);
    }
}
