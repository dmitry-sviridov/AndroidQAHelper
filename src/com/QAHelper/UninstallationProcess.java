package com.QAHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.QAHelper.Controller.errorAlert;

public class UninstallationProcess implements Runnable{

    private volatile String devId;
    private volatile String pkgName;
    Thread thread;

    public UninstallationProcess(String devId, String pkgName) {
        this.devId = devId;
        this.pkgName = pkgName;
        thread = new Thread(this, "uninstall thread");
        thread.start();
    }

    @Override
    public void run() {
        try {
            uninstallApkWithConsoleOut(devId, pkgName);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void uninstallApkWithConsoleOut(String devId, String pkgName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", devId, "uninstall", pkgName)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.println(devId + " Uninstall failed");
        }
    }
}
