package de.flonix.master;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.logging.Logger;

public class BenchmarkPubClient {

    private static final Logger log = Logger.getLogger(BenchmarkPubClient.class.getName());

    MqttClient mqttClient;
    MqttConnectOptions connectOptions;

    public static void main(String[] args) {

        String serverURI = args[0];
        String clientId = args[1];
        new BenchmarkPubClient(serverURI, clientId);
    }

    public BenchmarkPubClient(String serverURI, String clientId) {
        try {
            mqttClient = new MqttClient(serverURI, clientId, null);
            connectOptions = createConnectOptions();

            mqttClient.connect(connectOptions);

            long start = Instant.now().toEpochMilli();
            long currentTime = Instant.now().toEpochMilli();
            int messageCount = 0;

            while (currentTime - start < 5000) {
                currentTime = Instant.now().toEpochMilli();
                mqttClient.publish("test", ("testtest_" + currentTime).getBytes(Charset.forName("UTF-8")), 0, false);
                messageCount++;
            }

            log.info(messageCount + " Messages sent in " + (currentTime - start));

            mqttClient.disconnect();
            mqttClient.close();

        } catch (MqttException e) {
            log.severe(e.toString());
        }

    }

    private static MqttConnectOptions createConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        return options;
    }
}
