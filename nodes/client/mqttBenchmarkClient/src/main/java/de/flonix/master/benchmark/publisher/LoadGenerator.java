package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.Message;

class LoadGenerator {
    private final long interval;
    private final String topic;
    private long lastTick = System.nanoTime();
    private MessageSender messageSender;
    private long runtime;
    private int payloadSize;
    private long startTime;

    LoadGenerator(String topic, long interval, int runtime, int payloadSize, MessageSender messageSender) {
        this.interval = interval;
        this.topic = topic;
        this.messageSender = messageSender;
        this.runtime = runtime * 1000000000L;
        this.payloadSize = payloadSize;
    }

    void trigger(long currentTick) {
        if (checkTrigger(currentTick) && hasMessages()) {
            messageSender.sendMessage(new Message(topic, payloadSize));
        }
    }

    boolean hasMessages() {
        return System.nanoTime() - startTime < runtime;
    }

    void start() {
        startTime = System.nanoTime();
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

    void shutdown() {
        messageSender.shutdown();
    }
}
