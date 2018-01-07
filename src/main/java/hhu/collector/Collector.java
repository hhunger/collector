package hhu.collector;

import hhu.collector.model.Sensor;
import hhu.collector.model.TemperatureMeasurement;
import hhu.collector.services.ConfigService;
import hhu.collector.services.InfluxDBService;
import hhu.collector.services.OneWireService;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Collector {
    private ConfigService conf;
    private OneWireService oneWireService;
    private InfluxDBService influxDBService;

    public Collector(ConfigService conf) {
        System.out.println("Collector starting up...");
        this.conf = conf;
        createOneWireService();
        createInfluxDBService();
        startPolling();
    }

    public void startPolling() {
        System.out.println("Starting polling cycle...");
        while (true) {
            System.out.println("Polling 1-Wire sensors");
            List<TemperatureMeasurement> tempMes = pollOneWire();

            influxDBService.writeMeasurements(tempMes);

            try {
                Thread.sleep(conf.getPollIntervalSecs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createOneWireService() {
        oneWireService = new OneWireService(conf.getOneWireBaseDev());
        for (Map.Entry<String, String> sensorDef : conf.getOneWireSensorDefs().entrySet()) {
            Sensor sensor = new Sensor(sensorDef.getKey(), sensorDef.getValue());
            oneWireService.addSensor(sensor);
        }
    }

    private void createInfluxDBService() {
        influxDBService = new InfluxDBService(conf);
        influxDBService.connect();
    }

    private List<TemperatureMeasurement> pollOneWire() {
        return oneWireService.getMeasurements();
    }

    public static void main(String[] args) {
        ConfigService conf = new ConfigService();
        Collector collector = new Collector(conf);
        collector.startPolling();
    }
}
