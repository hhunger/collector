package hhu.collector.services;

import hhu.collector.model.Sensor;
import hhu.collector.model.TemperatureMeasurement;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OneWireServiceTest {
    OneWireService service;

    @Before
    public void setup() {
        service = new OneWireService("src/test/resources/%s.data");
    }

    @Test
    public void testGetMeasurementus() {
        String[] sensNames = {"onewire_28-00000876fb53", "onewire_28-00000877ed06", "onewire_28-000008769114"};
        List<Sensor> sensors = new ArrayList<>(sensNames.length);
        for (String sensName : sensNames) {
            Sensor sensor = new Sensor(sensName, sensName);
            sensors.add(sensor);
            service.addSensor(sensor);
        }

        List<TemperatureMeasurement> measurements = service.getMeasurements();

        assertEquals("More than 3 measurement has been returned", 3, measurements.size());

        assertEquals("Measurement values do not match", 23, measurements.get(0).getValue());
        assertEquals("Measurement values do not match", 54, measurements.get(1).getValue());
        assertEquals("Measurement values do not match", 23, measurements.get(2).getValue());
    }
}
