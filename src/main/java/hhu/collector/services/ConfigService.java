package hhu.collector.services;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigService {
    private static final String ENV_ONEWIRE_SENSORS     = "COLLECTOR_ONEWIRE_SENSORS_";

    private static final String ENV_POLL_INTERVAL       = "COLLECTOR_POLL_INTERVAL";
    private static final String ENV_INFLUXDB_HOSTNAME   = "COLLECTOR_INFLUXDB_HOSTNAME";
    private static final String ENV_INFLUXDB_PORT       = "COLLECTOR_INFLUXDB_PORT";
    private static final String ENV_INFLUXDB_USERNAME   = "COLLECTOR_INFLUXDB_USERNAME";
    private static final String ENV_INFLUXDB_PASSWORD   = "COLLECTOR_INFLUXDB_PASSWORD";
    private static final String ENV_INFLUXDB_DBNAME     = "COLLECTOR_INFLUXDB_DBNAME";

    private static final String ONE_WIRE_BASE_DEV       = "/sys/bus/w1/devices/%s/w1_slave";

    private static final Pattern OW_DEF_PATTERN         = Pattern.compile("(\\w+),(\\p{XDigit}{2}-\\p{XDigit}{12})", Pattern.UNICODE_CHARACTER_CLASS);

    private Map<String, String> oneWireSensorDefs;
    private long pollIntervalSecs;
    private String influxdbHostname;
    private int influxdbPort;
    private String influxdbUsername;
    private String influxdbPassword;
    private String influxdbDBName;

    public ConfigService() {
        Map<String, String> env = System.getenv();

        parseOneWireConfig(env);
        parsePollInterval(env);
        parseInfluxConfig(env);
    }

    private void parseInfluxConfig(Map<String, String> env) {
        influxdbHostname = env.getOrDefault(ENV_INFLUXDB_HOSTNAME, "localhost");
        String influxdbPortStr = env.getOrDefault(ENV_INFLUXDB_PORT, "8086");
        influxdbUsername = env.getOrDefault(ENV_INFLUXDB_USERNAME, "root");
        influxdbPassword = env.getOrDefault(ENV_INFLUXDB_PASSWORD, "root");
        influxdbDBName = env.getOrDefault(ENV_INFLUXDB_DBNAME, "collector");

        influxdbPort = Integer.valueOf(influxdbPortStr);
    }

    public String getInfluxdbHostname() {
        return influxdbHostname;
    }

    public int getInfluxdbPort() {
        return influxdbPort;
    }

    public String getInfluxdbUsername() {
        return influxdbUsername;
    }

    public String getInfluxdbPassword() {
        return influxdbPassword;
    }

    public String getInfluxdbDBName() {
        return influxdbDBName;
    }

    public Map<String, String> getOneWireSensorDefs() {
        return oneWireSensorDefs;
    }

    public long getPollIntervalSecs() {
        return pollIntervalSecs;
    }

    private void parsePollInterval(Map<String, String> env) {
        String pollIntervalSecsStr = env.getOrDefault(ENV_POLL_INTERVAL, "1800");
        pollIntervalSecs = Long.valueOf(pollIntervalSecsStr) * 1000;
    }

    private void parseOneWireConfig(Map<String, String> env) {
        oneWireSensorDefs = new HashMap<>();
        System.out.println("Parsing OneWire config...");

        int count = 1;
        String defString = ENV_ONEWIRE_SENSORS + String.valueOf(count++);
        String owDefs = env.get(defString);
        while(null != owDefs) {
            Matcher m = OW_DEF_PATTERN.matcher(owDefs);

            if (m.matches()) {
                String dev = String.format(ONE_WIRE_BASE_DEV, m.group(2));
                System.out.format("Found OneWire config for '%s': %s\n", m.group(1), dev);

                oneWireSensorDefs.put(m.group(1), dev);
            } else {
                System.err.format("Invalid OneWire config: %s=%s\n", defString, owDefs);
            }

            defString = ENV_ONEWIRE_SENSORS + String.valueOf(count++);
            owDefs = env.get(defString);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Collector Configuration:\n");
        for (Map.Entry<String, String> ows : oneWireSensorDefs.entrySet()) {
            b.append("  - ").append(ows.toString()).append("\n");
        }
        return b.toString();
    }
}
