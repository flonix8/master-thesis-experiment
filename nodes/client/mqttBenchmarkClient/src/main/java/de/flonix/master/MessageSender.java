package de.flonix.master;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageSender {
    final ExecutorService executorService;

    private MessageSender(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void sendMessage(Message msg) {
        executorService.execute(() -> {
            msg.setTimestamp();
            System.out.println(msg);
        });
    }

    public static MessageSender getInstance() {
        return new MessageSender(Executors.newSingleThreadExecutor());
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
