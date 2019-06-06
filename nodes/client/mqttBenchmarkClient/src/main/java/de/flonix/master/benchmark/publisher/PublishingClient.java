package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.CommandWaiter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PublishingClient {
    private static final String DELIMITER = ";";
    private List<LoadGenerator> loadGenerators = new ArrayList<>();
    private HashSet<LoadGenerator> finishedLoadGenerators = new HashSet<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private final CommandWaiter commandWaiter;

    private PublishingClient(File configFile) {
        commandWaiter = new CommandWaiter(PublishingClient.class.getSimpleName());

        parseConfigFile(configFile);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        commandWaiter.waitForCommand("prepare");
        prepare();

        commandWaiter.waitForCommand("run");
        run();

        shutdown();
    }

    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Usage: <config_file_path>");
        File configFile = new File(args[0]);
        if (configFile.exists()) {
            new PublishingClient(configFile);
        } else {
            throw new IllegalArgumentException("Config file does not exist!");
        }
    }

    private void prepare() {
        loadGenerators.forEach(LoadGenerator::prepare);
    }

    private void run() {
        isRunning.set(true);
        loadGenerators.forEach(LoadGenerator::start);
        while (isRunning.get() && !commandWaiter.hasCommand("shutdown") && finishedLoadGenerators.size() != loadGenerators.size()) {
            loadGenerators.forEach(gen -> {
                if (gen.hasMessages()) {
                    gen.trigger(System.nanoTime());
                } else {
                    finishedLoadGenerators.add(gen);
                }
            });
        }
    }

    private void parseConfigFile(File configFile) {
        try {
            Files.lines(configFile.toPath()).forEach(configLine -> {
                String[] configParams = configLine.split(DELIMITER);

                String serverURI = configParams[0];
                String clientId = configParams[1];
                String topic = configParams[2];
                long interval = Long.valueOf(configParams[3]);
                int runtime = Integer.valueOf(configParams[4]);
                int payloadSize = Integer.valueOf(configParams[5]);

                loadGenerators.add(new LoadGenerator(topic, interval, runtime, payloadSize, MessageSender.getInstance(serverURI, clientId)));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        isRunning.set(false);
        loadGenerators.forEach(LoadGenerator::shutdown);
        commandWaiter.stop();
    }
}
