package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.Message;

import java.util.logging.Logger;

class LoadGenerator {
    private static final long NANOS_IN_SEC = 1000000000L;
    private final long interval;
    private final String topic;
    private final Logger log;
    private long lastTick;
    private MessageSender messageSender;
    private long runtime;
    private int payloadSize;
    private long startTime;
    private long startOffset;
    private long frequency;

    LoadGenerator(String topic, long frequency, int startOffset, int runtime, int payloadSize, MessageSender messageSender) {
        this.log = Logger.getLogger(this.getClass().getSimpleName() + "(" + topic + ")");
        this.interval = NANOS_IN_SEC / frequency;
        this.topic = topic;
        this.messageSender = messageSender;
        this.runtime = runtime * NANOS_IN_SEC;
        this.startOffset = startOffset * NANOS_IN_SEC;
        this.payloadSize = payloadSize;
        this.frequency = frequency;
    }

    void trigger(long currentTick) {
        if (checkTrigger(currentTick) && hasMessages()) {
            messageSender.sendMessage(new Message(topic, payloadSize));
        }
    }

    boolean hasMessages() {
        return System.nanoTime() - (startTime + startOffset) < runtime && System.nanoTime() > startTime + startOffset;
    }

    boolean isFinished() {
        return System.nanoTime() - startTime + startOffset > runtime;
    }

    void prepare() {
        messageSender.connect();
    }

    void start() {
        log.info("Starting to publish at " + frequency + "Hz for " + runtime / NANOS_IN_SEC + "s (startOffset = " + startOffset / NANOS_IN_SEC + "s)");
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
