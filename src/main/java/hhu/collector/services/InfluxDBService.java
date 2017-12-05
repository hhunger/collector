package hhu.collector.services;

import hhu.collector.model.TemperatureMeasurement;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxDBService {
    private InfluxDB influxDB;
    private ConfigService conf;
    private List<BatchPoints> batchBuffer;

    public InfluxDBService(ConfigService conf) {
        this.conf = conf;
        batchBuffer = new ArrayList<>();
    }

    public void connect() {
        String influxURL = String.format("http://%s:%d", conf.getInfluxdbHostname(), conf.getInfluxdbPort());
        influxDB = InfluxDBFactory.connect( influxURL, conf.getInfluxdbUsername(), conf.getInfluxdbPassword());

        String dbName = conf.getInfluxdbDBName();
        if (!influxDB.databaseExists(dbName)) {
            influxDB.createDatabase(dbName);
            influxDB.setRetentionPolicy("autogen");
        }

        influxDB.setDatabase(dbName);
    }

    public void writeMeasurements(List<TemperatureMeasurement> measurements) {
        if (null == influxDB) connect();

        BatchPoints batch = BatchPoints
                .database(conf.getInfluxdbDBName())
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (TemperatureMeasurement measurement : measurements) {
            Point point = Point.measurement(measurement.getMeasurementName())
                    .time(measurement.getTimestampMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", measurement.getValue())
                    .tag("sensor", measurement.getSensor().getName())
                    .build();
            batch.point(point);
        }

        try {
            influxDB.write(batch);
            if (batchBuffer.size() > 0) {
                System.out.println("Writing InfluxDB batch from buffer.");
                int count = 1;
                for (BatchPoints bufferedBatch : batchBuffer) {
                    influxDB.write(bufferedBatch);
                    System.out.println("Wrote buffered InfluxDB batch. Current buffer size: " + String.valueOf(batchBuffer.size() - count++));
                }
                batchBuffer = new ArrayList<>();
            }
        } catch (InfluxDBIOException e) {
            System.err.format("Writing to InfluxDB at host '%s' failed.", conf.getInfluxdbHostname());
            if (!batchBuffer.contains(batch)) {
                batchBuffer.add(batch);
                System.out.println("Adding InfluxDB batch to batch buffer. Current buffer size: " + batchBuffer.size());
            }
        }
    }
}
