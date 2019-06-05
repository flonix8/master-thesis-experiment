package de.flonix.master;

import java.util.LinkedList;

public class LoadGenerator {
    private final long interval;
    private final String topic;
    private long lastTick = System.nanoTime();
    private MessageSender messageSender;
    private LinkedList<Message> messages = new LinkedList<>();
    private int runtime;
    private int payloadSize;

    public LoadGenerator(String topic, long interval, int runtime, int payloadSize, MessageSender messageSender) {
        this.interval = interval;
        this.topic = topic;
        this.messageSender = messageSender;
        this.runtime = runtime;
        this.payloadSize = payloadSize;
    }

    public void trigger(long currentTick) {
        if (checkTrigger(currentTick) && hasMessages()) {
            messageSender.sendMessage(messages.removeFirst());
        }
    }

    public boolean hasMessages() {
        return messages.size() > 0;
    }

    private Boolean checkTrigger(long currentTick) {
        boolean shouldTrigger = currentTick - lastTick >= interval;
        if (shouldTrigger) {
            lastTick = currentTick;
            return true;
        } else {
            return false;
        }
    }

    public void prepareMessages() {
        long numberOfMessages = (runtime * 1000000000L) / interval;
        for (long i = 0; i < numberOfMessages; i++) {
            messages.add(new Message(topic, payloadSize));
        }
    }

    public void shutdown() {
        messageSender.shutdown();
    }
}
