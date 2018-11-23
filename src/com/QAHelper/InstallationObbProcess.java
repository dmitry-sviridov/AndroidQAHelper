package com.QAHelper;

import java.io.File;
import java.io.IOException;

public class InstallationObbProcess implements Runnable{

    private volatile String deviceId;
    private volatile String appPackageName;
    private volatile String pathToObb;
    private volatile String obbName;

    public InstallationObbProcess(String deviceId, String appPackageName, String pathToObb, String obbName) {
        this.deviceId = deviceId;
        this.appPackageName = appPackageName;
        this.pathToObb = pathToObb;
        this.obbName = obbName;
    }

    public void installObbWithConsoleOut(String devId, String appPackageName, String pathToObb, String obbName) throws IOException, InterruptedException {
        String obbDeviceDirectory = "/sdcard/Android/obb/" + appPackageName;
        String bundleVersion = obbName.split("\\.")[1];
        String obbWithBundleIdPath = String.format("main.%s.%s.obb", bundleVersion, appPackageName);

        File obbSource = new File(pathToObb);
        String path = obbSource.getParent() + "/";
        System.out.println(path);
        File obbTarget = new File(path + obbWithBundleIdPath);

        if(obbSource.renameTo(obbTarget)){
            System.out.println("Rename success");;
        }else{
            System.out.println("Rename fail");
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("adb", "-s", devId, "shell", "mkdir", obbDeviceDirectory)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE);

        Process process = pb.start();

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            System.err.print(devId + " Dir creating failed");
        } else {
            ProcessBuilder pbi = new ProcessBuilder();
            pbi.command("adb", "-s", devId, "push", obbTarget.getAbsolutePath(), obbDeviceDirectory)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE);
            Process process1 = pbi.start();
            int exitValue1 = process.waitFor();
            if (exitValue1 != 0) {
                System.err.print(devId + " Obb pushing failed");
            } else {
                System.out.println("Obb pushing success");
            }
        }

    }

    @Override
    public void run() {
        try {
            installObbWithConsoleOut(deviceId, appPackageName, pathToObb, obbName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
