package com.QAHelper;

import javafx.concurrent.Task;

public class LaunchApkTask extends Task<Void> {

    private final String deviceId;
    private final String pkg;

    public LaunchApkTask(String deviceId, String pkg) {
        this.deviceId = deviceId;
        this.pkg = pkg;
    }

    @Override
    protected Void call() throws Exception {
        String cmd = String.format("adb -s %s shell monkey -p \"%s\" -c android.intent.category.LAUNCHER 1", deviceId, pkg);
        Process runApk = Runtime.getRuntime().exec(cmd);

        int exitValue = runApk.waitFor();
        if (exitValue != 0) {
            System.err.println(deviceId + " Couldn't launch apk");
        }
        return null;
    }
}
