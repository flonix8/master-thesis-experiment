package de.flonix.master.benchmark;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandWaiter {

    private static final String PIPE_NAME = "client_command_input";
    private LinkedBlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public CommandWaiter() {
        createPipe();
        startInputMonitor();
        waitForCommand("ready");
    }

    public void stop() {
        isRunning.set(false);
        deletePipe();
    }

    public void waitForCommand(String command) {
        System.out.println("Waiting for command: " + command);
        String nextCommand = "";
        do {
            try {
                nextCommand = commands.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!nextCommand.equals(command));
        System.out.println("Resuming after command: " + command);
    }

    public boolean hasCommand(String command) {
        String nextCommand = commands.poll();
        return nextCommand != null && nextCommand.equals(command);
    }

    private void createPipe() {
        try {
            (new ProcessBuilder("mkfifo", PIPE_NAME)).start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void deletePipe() {
        try {
            (new ProcessBuilder("rm", PIPE_NAME)).start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void startInputMonitor() {
        new Thread(() -> {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(PIPE_NAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            isRunning.set(true);
            commands.add("ready");

            String line;

            while (isRunning.get()) {
                try {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        commands.add(line);
                        System.out.println("Received command: " + line);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
