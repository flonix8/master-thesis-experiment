package de.flonix.master.benchmark;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

public class Message {
    private static final byte DELIMITER_BYTE = ";".getBytes(StandardCharsets.US_ASCII)[0];
    private static final String DELIM = ";";
    private static final byte[] randomString = RandomStringUtils.randomAlphanumeric(10000000).getBytes(StandardCharsets.US_ASCII);
    private String id;
    private String topic;
    private long sentTimestamp = 0;
    private long receivedTimestamp = 0;
    private int payloadSize;
    private MqttMessage mqttMessage;

    public Message(String topic, int payloadSize) {
        if (payloadSize < 58 || payloadSize > 10000000)
            throw new IllegalArgumentException("Payload must be between 58 and 10,000,000!");

        this.topic = topic;
        this.id = UUID.randomUUID().toString();
        this.payloadSize = payloadSize;
    }

    public Message(String topic, MqttMessage msg) {
        this.topic = topic;
        this.mqttMessage = msg;
        this.payloadSize = msg.getPayload().length;
    }

    private static long getEpochMicro() {
        Instant now = Instant.now();
        return (now.getEpochSecond() * 1000000) + (now.getNano() / 1000);
    }

    public void parseMqttPayload() {
        String[] payloadFields = new String(mqttMessage.getPayload(), StandardCharsets.US_ASCII).split(DELIM);
        this.id = payloadFields[0];
        this.sentTimestamp = Long.valueOf(payloadFields[1]);
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getMqttPayload() {
        if (sentTimestamp == 0) {
            throw new NullPointerException("Payload has not been set yet due to missing sentTimestamp!");
        }
        String timestamp = String.valueOf(sentTimestamp);
        ByteBuffer payload = ByteBuffer.allocate(payloadSize);
        payload.put(id.getBytes(StandardCharsets.US_ASCII));
        payload.put(DELIMITER_BYTE);
        payload.put(timestamp.getBytes(StandardCharsets.US_ASCII));
        payload.put(DELIMITER_BYTE);
        payload.put(randomString, 0, payloadSize - 2 - 36 - timestamp.length());

        return payload.array();
    }

    public void setSentTimestamp() {
        this.sentTimestamp = getEpochMicro();
    }

    public void setReceivedTimestamp() {
        this.receivedTimestamp = getEpochMicro();
    }

    @Override
    public String toString() {
        return "Message(id=" + id + ", topic=" + topic + ", sentTimestamp=" + sentTimestamp + ", receivedTimestamp=" + receivedTimestamp + ", payloadSize=" + payloadSize + ")";
    }

    public String toLogEntry() {
        return id + DELIM + topic + DELIM + sentTimestamp + DELIM + receivedTimestamp + DELIM + payloadSize;
    }

}
