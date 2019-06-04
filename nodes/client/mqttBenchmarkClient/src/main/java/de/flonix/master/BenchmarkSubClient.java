package de.flonix.master;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class BenchmarkSubClient {

    private static final Logger log = Logger.getLogger(BenchmarkSubClient.class.getName());

    MqttClient mqttClient;
    MqttConnectOptions connectOptions;
    AtomicInteger messageCount = new AtomicInteger();

    public static void main(String[] args) {

        String serverURI = args[0];
        String clientId = args[1];
        new BenchmarkSubClient(serverURI, clientId);
    }

    private void incrementMessageCount() {
        int current, next;
        boolean success;
        do {
            current = messageCount.get();
            next = current + 1;
            success = messageCount.compareAndSet(current, next);
        } while (!success);
    }

    public BenchmarkSubClient(String serverURI, String clientId) {
        try {
            mqttClient = new MqttClient(serverURI, clientId, null);
            connectOptions = createConnectOptions();

            mqttClient.connect(connectOptions);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.info("Connection lost.");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    incrementMessageCount();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("Delivery complete");
                }
            });
            mqttClient.subscribe("#");

            for (int i = 0; i < 20; i++) {
                Thread.sleep(3000);
                log.info("Received " + messageCount + " messages");
            }

            Thread.yield();

            mqttClient.disconnect();
            mqttClient.close();

        } catch (MqttException | InterruptedException e) {
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
