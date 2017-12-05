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
        service = new OneWireService();
    }

    @Test
    public void testGetMeasurementus() throws URISyntaxException {
        String[] sensNames = {"onewire_28-00000876fb53", "onewire_28-00000877ed06", "onewire_28-000008769114"};
        List<Sensor> sensors = new ArrayList<>(sensNames.length);
        for (String sensName : sensNames) {
            Sensor sensor = new Sensor(sensName, Paths.get(ClassLoader.getSystemResource(sensName + ".data").toURI()));
            sensors.add(sensor);
            service.addSensor(sensor);
        }

        List<TemperatureMeasurement> mems = service.getMeasurements();

        assertEquals("More than 3 measurement has been returned", 3, mems.size());

        assertEquals("Measurement values do not match", 22562, mems.get(0).getValue());
        assertEquals("Measurement values do not match", 54250, mems.get(1).getValue());
        assertEquals("Measurement values do not match", 23062, mems.get(2).getValue());
    }
}
