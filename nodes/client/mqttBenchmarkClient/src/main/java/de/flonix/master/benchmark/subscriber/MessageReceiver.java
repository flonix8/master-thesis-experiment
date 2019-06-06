package de.flonix.master.benchmark.subscriber;

import org.eclipse.paho.client.mqttv3.*;

import java.util.function.BiConsumer;

public class MessageReceiver {
    MqttClient mqttClient;
    String topic;
    String serverURI;
    String clientId;

    public MessageReceiver(String serverURI, String clientId, String topic) {
        this.topic = topic;
        this.serverURI = serverURI;
        this.clientId = clientId;
    }

    private static MqttConnectOptions createConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        return options;
    }

    void connectAndSubscribe() {
        try {
            mqttClient = new MqttClient(serverURI, clientId, null);
            mqttClient.connect(createConnectOptions());
            mqttClient.setCallback(createCallback((msgTopic, msg) -> {
                System.out.println(System.nanoTime() + ";" + msgTopic + ";" + msg.getPayload().length);
            }));
            mqttClient.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void shutdown() {
        try {
            mqttClient.disconnectForcibly();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback createCallback(BiConsumer<String, MqttMessage> consumer) {
        return new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                consumer.accept(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
    }
}
