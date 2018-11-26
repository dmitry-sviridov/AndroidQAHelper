package com.QAHelper;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RemoveTask extends Task<Void> {

    private final String deviceId;
    private final String pkg;

    public RemoveTask(String deviceId, String pkg) {
        this.deviceId = deviceId;
        this.pkg = pkg;
    }

    @Override
    protected Void call() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", deviceId, "uninstall", pkg)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.println(deviceId + " Uninstall failed");
        }
        return null;
    }
}
