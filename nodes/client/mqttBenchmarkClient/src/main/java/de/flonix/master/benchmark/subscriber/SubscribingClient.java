package de.flonix.master.benchmark.subscriber;

import de.flonix.master.benchmark.CommandWaiter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SubscribingClient {

    private static final String DELIMITER = ";";
    private CommandWaiter commandWaiter;
    private List<MessageReceiver> messageReceivers = new ArrayList<>();

    SubscribingClient(File configFile) {
        commandWaiter = new CommandWaiter(SubscribingClient.class.getSimpleName());

        parseConfigFile(configFile);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        commandWaiter.waitForCommand("prepare");
        prepare();

        //commandWaiter.waitForCommand("run");
        run();

        shutdown();
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

                String serverURI = configParams[0];
                String clientId = configParams[1];
                String topic = configParams[2];

                messageReceivers.add(new MessageReceiver(serverURI, clientId, topic));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
