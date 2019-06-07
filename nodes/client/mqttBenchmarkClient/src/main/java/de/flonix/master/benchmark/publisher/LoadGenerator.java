package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.Message;

import java.util.logging.Logger;

class LoadGenerator {
    private final long interval;
    private final String topic;
    private long lastTick;
    private MessageSender messageSender;
    private long runtime;
    private int payloadSize;
    private long startTime;

    private final Logger log;

    LoadGenerator(String topic, long interval, int runtime, int payloadSize, MessageSender messageSender) {
        this.log = Logger.getLogger(this.getClass().getSimpleName() + "(" + topic + ")");
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

    void prepare() {
        messageSender.connect();
    }

    void start() {
        log.info("Starting to publish " + interval/1000 + "us apart for " + runtime/1000000000L + "s");
        startTime = System.nanoTime();
        lastTick = startTime;
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
        log.info("Finished run. Shutting down.");
        messageSender.shutdown();
    }
}
