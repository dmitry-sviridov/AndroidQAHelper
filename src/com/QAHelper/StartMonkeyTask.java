package com.QAHelper;

import javafx.concurrent.Task;

public class StartMonkeyTask extends Task<Void> {

    private final String deviceId;
    private final String pkg;

    public StartMonkeyTask(String deviceId, String pkg) {
        this.deviceId = deviceId;
        this.pkg = pkg;
    }

    @Override
    protected Void call() throws Exception {

        String cmd = String.format("adb -s %s shell monkey -p %s --throttle 200 -v --pct-syskeys 0 10000", deviceId, pkg);
        Process runApk = Runtime.getRuntime().exec(cmd);

        int exitValue = runApk.waitFor();
        if (exitValue != 0) {
            System.err.println(deviceId + " Couldn't start monkey");
        }
        return null;
    }
}
