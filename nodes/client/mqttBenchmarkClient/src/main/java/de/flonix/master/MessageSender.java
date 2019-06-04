package de.flonix.master;

import java.util.concurrent.ExecutorService;

public class MessageSender {
    final ExecutorService executorService;

    public MessageSender(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void sendMessage(Message msg) {
        executorService.execute(() -> {
            System.out.println(msg);
        });
    }
}
