package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.Message;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MessageSender {
    private final ExecutorService executorService;
    private MqttClient mqttClient;

    private MessageSender(String serverURI, String clientId, ExecutorService executorService) {
        this.executorService = executorService;
        try {
            mqttClient = new MqttClient(serverURI, clientId, null);
            mqttClient.connect(createConnectOptions());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    void sendMessage(Message msg) {
        executorService.execute(() -> {
            msg.setTimestamp();
            try {
                mqttClient.publish(msg.getTopic(), msg.getPayload(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
    }

    static MessageSender getInstance(String serverURI, String clientId) {
        return new MessageSender(serverURI, clientId, Executors.newSingleThreadExecutor());
    }

    void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            mqttClient.disconnectForcibly();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
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
