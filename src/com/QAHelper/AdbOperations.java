package com.QAHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AdbOperations {

    public List<String> getDeviceList() throws IOException {
        Process adbDeviceProc = Runtime.getRuntime().exec("adb devices");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(adbDeviceProc.getInputStream()));
        List<String> deviceList = new ArrayList<String>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.endsWith("device")) {
                deviceList.add(line.split("\\t")[0]);
            }
        }
        if (deviceList.size() == 0) deviceList.add("Not any connected devices");
        return deviceList;
    }

    public String getApkVersion(String pkg, String deviceID) throws IOException {
        String cmd = String.format("adb -s %s shell dumpsys package %s", deviceID, pkg);
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

    public String getDeviceModel(String deviceID) throws IOException {
        String cmd = String.format("adb -s %s shell getprop ro.product.model", deviceID);
        Process getModel = Runtime.getRuntime().exec(cmd);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getModel.getInputStream()));
        String model = bufferedReader.readLine();
        return model;
    }

}

