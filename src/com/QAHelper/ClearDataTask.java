package com.QAHelper;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClearDataTask extends Task<Void> {

    private final String deviceId;
    private final String pkg;

    public ClearDataTask(String deviceId, String pkg) {
        this.deviceId = deviceId;
        this.pkg = pkg;
    }


    @Override
    protected Void call() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", deviceId, "shell", "pm", "clear", pkg)
                .redirectOutput(ProcessBuilder.Redirect.PIPE) // Redirecting subprocess output to java
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.print(deviceId + " couldn't clean cache and data");
        }
        return null;
    }
}
