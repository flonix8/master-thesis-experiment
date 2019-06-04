package de.flonix.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PublishingClient {
    private static final String DELIMITER = ";";
    private final MessageSender messageSender;
    private final ExecutorService executorService;
    private List<LoadGenerator> loadGenerators = new ArrayList<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private PublishingClient(File configFile) {
        executorService = Executors.newFixedThreadPool(2);
        messageSender = new MessageSender(executorService);

        parseConfigFile(configFile);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isRunning.set(false);
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        prepareMessages();

        run();
    }

    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Need path to config file!");
        File configFile = new File(args[0]);
        if (configFile.exists()) {
            PublishingClient client = new PublishingClient(configFile);
        } else {
            throw new IllegalArgumentException("Config file does not exist!");
        }
    }

    private void prepareMessages() {
        loadGenerators.forEach(LoadGenerator::prepareMessages);
    }

    private void run() {
        isRunning.set(true);
        while (isRunning.get()) {
            loadGenerators.removeAll(loadGenerators.stream().filter(gen -> !gen.hasMessages()).collect(Collectors.toList()));
            loadGenerators.forEach(gen -> gen.trigger(System.nanoTime()));
        }
    }

    private void parseConfigFile(File configFile) {
        try {
            Files.lines(configFile.toPath()).forEach(configLine -> {
                String[] configParams = configLine.split(DELIMITER);

                String topic = configParams[0];
                long interval = Long.valueOf(configParams[1]);
                int runtime = Integer.valueOf(configParams[2]);
                int payloadSize = Integer.valueOf(configParams[3]);

                loadGenerators.add(new LoadGenerator(topic, interval, runtime, payloadSize, messageSender));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
