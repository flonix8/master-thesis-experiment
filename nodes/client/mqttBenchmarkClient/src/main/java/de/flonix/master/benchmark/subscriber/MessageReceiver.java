package de.flonix.master.benchmark.subscriber;

import de.flonix.master.benchmark.Message;
import de.flonix.master.benchmark.MessageLogger;
import org.eclipse.paho.client.mqttv3.*;

import java.util.function.BiConsumer;

class MessageReceiver {
    private MqttClient mqttClient;
    private String topic;
    private String serverURI;
    private String clientId;
    private MessageLogger messageLogger;

    MessageReceiver(String serverURI, String clientId, String topic) {
        this.topic = topic;
        this.serverURI = serverURI;
        this.clientId = clientId;
        this.messageLogger = new MessageLogger(clientId, msg -> {
            msg.parseMqttPayload();
            return msg.toLogEntry();
        });
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
                Message message = new Message(msgTopic, msg);
                message.setReceivedTimestamp();
                messageLogger.log(message);
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
        messageLogger.shutdown();
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
