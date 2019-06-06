package de.flonix.master.benchmark;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandWaiter {

    private String pipe_name;
    private LinkedBlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public CommandWaiter(String name) {
        this.pipe_name = name + "_command_input";
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
            (new ProcessBuilder("mkfifo", pipe_name)).start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void deletePipe() {
        try {
            (new ProcessBuilder("rm", pipe_name)).start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void startInputMonitor() {
        new Thread(() -> {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(pipe_name);
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
