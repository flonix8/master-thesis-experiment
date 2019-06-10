package de.flonix.master.benchmark.subscriber;

import de.flonix.master.benchmark.CommandWaiter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SubscribingClient {

    private static final String DELIMITER = ";";
    private static final Logger log;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %3$s - %5$s%6$s%n");
        log = Logger.getLogger(SubscribingClient.class.getSimpleName());
    }

    private CommandWaiter commandWaiter;
    private List<MessageReceiver> messageReceivers = new ArrayList<>();

    private SubscribingClient(File configFile) {
        parseConfigFile(configFile);
        if (!messageReceivers.isEmpty()) {
            commandWaiter = new CommandWaiter(SubscribingClient.class.getSimpleName());

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            commandWaiter.waitForCommand("prepare");
            prepare();

            run();

            shutdown();
        } else {
            log.warning("Did not find any configured message listeners. Exiting...");
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Usage: <config_file_path>");
        File configFile = new File(args[0]);
        if (configFile.exists()) {
            new SubscribingClient(configFile);
        } else {
            throw new IllegalArgumentException("Config file does not exist!");
        }
    }

    private void prepare() {
        messageReceivers.forEach(MessageReceiver::connectAndSubscribe);
        log.info("Preparation done. (Receivers are subscribed)");
    }

    private void run() {
        while (!commandWaiter.hasCommand("shutdown")) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() {
        messageReceivers.forEach(MessageReceiver::shutdown);
        commandWaiter.stop();
    }

    private void parseConfigFile(File configFile) {
        try {
            Files.lines(configFile.toPath()).forEach(configLine -> {
                String[] configParams = configLine.split(DELIMITER);
                if (!configParams[0].equals("subscriber")) return;

                String serverURI = configParams[1];
                String clientId = configParams[2];
                String topic = configParams[3];

                messageReceivers.add(new MessageReceiver(serverURI, clientId, topic));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
