package de.flonix.master.benchmark;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

public class Message {
    private static final String DELIMITER = ";";
    private String id;
    private String topic;
    private long sentTimestamp = 0;
    private String randomPayloadPadding;

    public Message(String topic, int payloadSize) {
        if (payloadSize < 58) throw new IllegalArgumentException("Payload must be larger than 57!");

        this.topic = topic;

        // generate random id
        id = UUID.randomUUID().toString();

        // generate random payload
        randomPayloadPadding = RandomStringUtils.randomAlphanumeric(payloadSize - 2 - 19 - 36);
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
        return (id + DELIMITER + String.format("%019d", sentTimestamp) + DELIMITER + randomPayloadPadding).getBytes(StandardCharsets.US_ASCII);
    }

    public void setSentTimestamp() {
        this.sentTimestamp = getEpochMicro();
    }

    @Override
    public String toString() {
        return "Message(id=" + id + ", topic=" + topic + ", sentTimestamp=" + sentTimestamp + ", payloadSize=" + randomPayloadPadding.length() + ")";
    }

}
