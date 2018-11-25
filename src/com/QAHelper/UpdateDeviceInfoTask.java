package com.QAHelper;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UpdateDeviceInfoTask extends Task<String> {

    private final String sdeviceId;
    private final String pkg;

    public UpdateDeviceInfoTask(String sdeviceId, String pkg) {
        this.sdeviceId = sdeviceId;
        this.pkg = pkg;
    }


    @Override
    protected String call() throws Exception {
        String cmd = String.format("adb -s %s shell dumpsys package %s", sdeviceId, pkg);
        Process getVersion = Runtime.getRuntime().exec(cmd);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getVersion.getInputStream()));
        String version;
        while ((version = bufferedReader.readLine()) != null) {
            if (version.contains("versionCode=")) {
                version = version.trim().split(" ")[0].split("=")[1];
                break;
            }
        }
        return version;

    }
}
