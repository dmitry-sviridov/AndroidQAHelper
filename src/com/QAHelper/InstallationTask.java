package com.QAHelper;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InstallationTask extends Task<Void> {

    private final String deviceId;
    private final String pathToApk;
    private final boolean reinstall;

    public InstallationTask(String deviceId, String pathToApk, boolean reinstall) {
        this.deviceId = deviceId;
        this.pathToApk = pathToApk;
        this.reinstall = reinstall;
    }

    @Override
    protected Void call() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        if (!reinstall) {
            pb.command("adb", "-s", deviceId, "install", pathToApk)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE);
        } else {
            pb.command("adb", "-s", deviceId, "install", "-r", "-d", pathToApk)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE);
        }
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.println(deviceId + " Installation failed");
        }
        return null;
    }

}
