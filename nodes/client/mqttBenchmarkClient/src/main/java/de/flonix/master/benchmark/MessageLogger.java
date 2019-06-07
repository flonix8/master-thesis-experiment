package de.flonix.master.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class MessageLogger {
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private File outputFile;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread worker;
    private Function<Message, String> messageConverter;

    public MessageLogger(String clientId, Function<Message, String> messageConverter) {
        this.outputFile = new File(clientId + "_log.csv");
        this.messageConverter = messageConverter;
        run();
    }

    private void run() {
        isRunning.set(true);
        worker = new Thread(() -> {

            List<Message> messagesToDump = new ArrayList<>();
            StringBuilder output = new StringBuilder();

            try (FileWriter fileWriter = new FileWriter(outputFile, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                while (isRunning.get() || !messages.isEmpty()) {
                    messages.drainTo(messagesToDump);

                    messagesToDump.forEach(msg -> {
                        output.append(messageConverter.apply(msg)).append("\n");
                    });

                    bufferedWriter.write(output.toString());

                    messagesToDump.clear();
                    output.setLength(0);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                bufferedWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        worker.start();
    }

    public void log(Message msg) {
        messages.add(msg);
    }

    public void shutdown() {
        isRunning.set(false);
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
