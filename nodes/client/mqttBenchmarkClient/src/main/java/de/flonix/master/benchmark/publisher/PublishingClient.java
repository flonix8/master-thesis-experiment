package de.flonix.master.benchmark.publisher;

import de.flonix.master.benchmark.CommandWaiter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class PublishingClient {
    private static final String DELIMITER = ";";
    private static final Logger log;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %3$s - %5$s%6$s%n");
        log = Logger.getLogger(PublishingClient.class.getSimpleName());
    }

    private final CommandWaiter commandWaiter;
    private List<LoadGenerator> loadGenerators = new ArrayList<>();
    private HashSet<LoadGenerator> finishedLoadGenerators = new HashSet<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

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
        log.info("Preparation done.");
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
                if (!configParams[0].equals("publisher")) return;

                String serverURI = configParams[1];
                String clientId = configParams[2];
                String topic = configParams[3];
                long interval = Long.valueOf(configParams[4]);
                int runtime = Integer.valueOf(configParams[5]);
                int payloadSize = Integer.valueOf(configParams[6]);

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
