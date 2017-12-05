package hhu.collector.model;

import java.nio.file.Path;

public class Sensor {
    private String name;
    private Path devicePath;

    public Sensor(String name, Path devicePath) {
        this.name = name;
        this.devicePath = devicePath;
    }

    public String getName() {
        return name;
    }

    public Path getDevicePath() {
        return devicePath;
    }

    @Override
    public String toString() {
        return String.format("Sensor '%s', '%s'", name, devicePath);
    }
}
