package de.flonix.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PublishingClient {
    private static final String DELIMITER = ";";
    private List<LoadGenerator> activeLoadGenerators = new ArrayList<>();
    private List<LoadGenerator> finishedLoadGenerators = new ArrayList<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private PublishingClient(File configFile) {
        parseConfigFile(configFile);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        prepareMessages();

        run();

        shutdown();
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
        activeLoadGenerators.forEach(LoadGenerator::prepareMessages);
    }

    private void run() {
        isRunning.set(true);
        while (isRunning.get() && !activeLoadGenerators.isEmpty()) {
            activeLoadGenerators.forEach(gen -> {
                if (gen.hasMessages()) {
                    gen.trigger(System.nanoTime());
                } else {
                    finishedLoadGenerators.add(gen);
                }
            });
            activeLoadGenerators.removeAll(finishedLoadGenerators);
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

                activeLoadGenerators.add(new LoadGenerator(topic, interval, runtime, payloadSize, MessageSender.getInstance()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        isRunning.set(false);
        activeLoadGenerators.forEach(LoadGenerator::shutdown);
        finishedLoadGenerators.forEach(LoadGenerator::shutdown);
    }
}
