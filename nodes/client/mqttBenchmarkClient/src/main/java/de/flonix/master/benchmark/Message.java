package de.flonix.master.benchmark;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

public class Message {
    private static final byte DELIMITER_BYTE = ";".getBytes(StandardCharsets.US_ASCII)[0];
    private static final String DELIMITER = ";";
    private static final byte[] randomString = RandomStringUtils.randomAlphanumeric(10000000).getBytes(StandardCharsets.US_ASCII);
    private String id;
    private String topic;
    private long sentTimestamp = 0;
    private String randomPayloadPadding;
    private int payloadSize;

    public Message(String topic, int payloadSize) {
        if (payloadSize < 58 || payloadSize > 10000000) throw new IllegalArgumentException("Payload must be between 58 and 10,000,000!");

        this.topic = topic;
        this.id = UUID.randomUUID().toString();
        this.payloadSize = payloadSize;
    }

    private Message(String id, String topic, long timestamp, String randomPayloadPadding) {
        this.id = id;
        this.topic = topic;
        this.sentTimestamp = timestamp;
        this.randomPayloadPadding = randomPayloadPadding;
    }

    static Message fromRawMessage(String topic, byte[] payload) {
        String s = new String(payload);
        String[] values = s.split(DELIMITER, 3);
        return new Message(values[0], topic, Long.getLong(values[1]), values[2]);
    }

    private static long getEpochMicro() {
        Instant now = Instant.now();
        return (now.getEpochSecond() * 1000000) + (now.getNano() / 1000);
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getPayload() {
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

    @Override
    public String toString() {
        return "Message(id=" + id + ", topic=" + topic + ", sentTimestamp=" + sentTimestamp + ", payloadSize=" + randomPayloadPadding.length() + ")";
    }

}
