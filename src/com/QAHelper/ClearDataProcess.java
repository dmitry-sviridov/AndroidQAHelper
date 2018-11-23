package com.QAHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.QAHelper.Controller.errorAlert;

public class ClearDataProcess implements Runnable {

    private volatile String deviceId;
    private volatile String pkgName;
    Thread thread;

    public ClearDataProcess(String deviceId, String pkgName) {
        this.deviceId = deviceId;
        this.pkgName = pkgName;
        thread = new Thread(this, "clear data thread");
        thread.start();
    }

    @Override
    public void run() {
        try {
            clear(deviceId, pkgName);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear(String devId, String pkgName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", devId, "shell", "pm", "clear", pkgName)
                .redirectOutput(ProcessBuilder.Redirect.PIPE) // Redirecting subprocess output to java
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.print(devId + " couldn't clean cache and data");
        }
    }
}
