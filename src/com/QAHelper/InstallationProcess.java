package com.QAHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

public class InstallationProcess implements Runnable{

    private volatile String deviceId;
    private volatile String pathToApk;
    private volatile boolean reinstall;
    Thread thread;


    public InstallationProcess(String deviceId, String pathToApk, boolean reinstall) {
        this.reinstall = reinstall;
        this.deviceId = deviceId;
        this.pathToApk = pathToApk;
        thread = new Thread(this, "installation thread");
        thread.start();
    }

    @Override
    public void run() {
        try {
            installApkWithConsoleOut(deviceId, pathToApk, reinstall);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void installApkWithConsoleOut(String devId, String pathToApk, boolean reinstall) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        if (!reinstall) {
            pb.command("adb", "-s", devId, "install", pathToApk)
                    .redirectOutput(Redirect.PIPE)
                    .redirectError(Redirect.PIPE);
        } else {
            pb.command("adb", "-s", devId, "install", "-r", "-d", pathToApk)
                    .redirectOutput(Redirect.PIPE)
                    .redirectError(Redirect.PIPE);
        }
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.println(devId + " Installation failed");
        }
    }


}

