package com.QAHelper;

import javafx.concurrent.Task;

import java.io.File;

public class InstallObbTask extends Task<Void> {

    private final String deviceId;
    private final String appPackageName;
    private final String pathToObb;
    private final String obbName;

    public InstallObbTask(String deviceId, String appPackageName, String pathToObb, String obbName) {
        this.deviceId = deviceId;
        this.appPackageName = appPackageName;
        this.pathToObb = pathToObb;
        this.obbName = obbName;
    }

    @Override
    protected Void call() throws Exception {
        String obbDeviceDirectory = "/sdcard/Android/obb/" + appPackageName;
        String bundleVersion = obbName.split("\\.")[1];
        String obbWithBundleIdPath = String.format("main.%s.%s.obb", bundleVersion, appPackageName);

        File obbSource = new File(pathToObb);
        String path = obbSource.getParent() + "/";
        System.out.println(path);
        File obbTarget = new File(path + obbWithBundleIdPath);

        if (obbSource.renameTo(obbTarget)) {
            System.out.println("Rename success");
            ;
        } else {
            System.out.println("Rename fail");
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", deviceId, "shell", "mkdir", obbDeviceDirectory)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.print(deviceId + " Dir creating failed");
        } else {
            ProcessBuilder pbi = new ProcessBuilder();
            pbi.command("adb", "-s", deviceId, "push", obbTarget.getAbsolutePath(), obbDeviceDirectory)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE);
            Process process1 = pbi.start();
            int exitValue1 = process.waitFor();
            if (exitValue1 != 0) {
                System.err.print(deviceId + " Obb pushing failed");
            } else {
                System.out.println("Obb pushing success");
            }
        }
        return null;
    }
}
