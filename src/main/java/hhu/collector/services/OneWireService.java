package hhu.collector.services;

import hhu.collector.model.Sensor;
import hhu.collector.model.TemperatureMeasurement;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OneWireService {
    private static final Pattern TEMPERATUR_PATTERN = Pattern.compile(".*crc=[0-9a-z]+\\WYES\\n.*\\Wt=(\\d+)$", Pattern.DOTALL);

    private List<Sensor> sensors = new ArrayList<>();

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public List<TemperatureMeasurement> getMeasurements() {
        List<TemperatureMeasurement> measurements = new ArrayList<>();

        for (Sensor sensor : sensors) {
            long timestamp = System.currentTimeMillis();
            long value = parseRawData(readRawData(sensor));
            int intValue = Math.round(value / 1000L);
            TemperatureMeasurement m = new TemperatureMeasurement(sensor, intValue, timestamp);
            measurements.add(m);
        }

        return measurements;
    }

    private long parseRawData(String data) {
        Matcher matcher = TEMPERATUR_PATTERN.matcher(data);

        if (!matcher.matches()) {
            System.out.format("Unexpected sensor response: '" + data + "'");
        }

        return Long.parseLong(matcher.group(1));
    }

    private String readRawData(Sensor sensor) {
        Stream<String> stream = null;
        try {
            stream = Files.lines(sensor.getDevicePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.collect(Collectors.joining("\n"));
    }
}
