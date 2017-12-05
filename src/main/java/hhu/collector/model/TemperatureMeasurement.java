package hhu.collector.model;

public class TemperatureMeasurement {

    private Sensor sensor;
    private int value;
    private long timestampMillis;
    private static final String MEASUREMENT_NAME = "temperature";

    public TemperatureMeasurement(Sensor sensor, int value, long timestampMillis) {
        this.sensor = sensor;
        this.value = value;
        this.timestampMillis = timestampMillis;
    }

    public String getMeasurementName() {
        return MEASUREMENT_NAME;
    }

    public int getValue() {
        return value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    @Override
    public String toString() {
        return "Temperature Sensor: " + sensor.getName() + ", value: " + value + ", time: " + timestampMillis;
    }
}
