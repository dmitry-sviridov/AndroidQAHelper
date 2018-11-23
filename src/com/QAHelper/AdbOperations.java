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

    public void uninstallApk(List<String> deviceIdList, String pkgName, String deviceId, boolean isAll) {
        if (isAll) {
            for (String device : deviceIdList) {
                uninstall(device, pkgName);
            }
        } else {
            uninstall(deviceId, pkgName);
        }
    }

    public void clearApkData(List<String> deviceIdList, String pkgName, String deviceId, boolean isAll) {
        if (isAll) {
            for (String device : deviceIdList) {
                clearData(device, pkgName);
            }
        } else {
            clearData(deviceId, pkgName);
        }
    }

    public void installApk(List<String> deviceIdList, String pathToApk, String deviceId, boolean isAll) {
        if (isAll) {
            for (String device: deviceIdList) {
                install(device, pathToApk);
            }
        } else {
            install(deviceId, pathToApk);
        }
    }

    public void reinstallApk(List<String> deviceIdList, String pathToApk, String deviceId, boolean isAll) {
        if (isAll) {
            for (String device: deviceIdList) {
                reinstall(device, pathToApk);
            }
        } else {
            reinstall(deviceId, pathToApk);
        }
    }

    public void installObb(List<String> deviceIdList, String appPackageName, String pathToObb, String obbName) {
        for (String deviceId: deviceIdList) {
            Thread thread = new Thread(new InstallationObbProcess(deviceId, appPackageName, pathToObb, obbName));
            thread.start();
        }
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


    private void uninstall(String deviceId, String pkgName) {
        new UninstallationProcess(deviceId, pkgName);
    }

    private void install(String deviceId, String pathToApk) {
        new InstallationProcess(deviceId, pathToApk, false);
    }

    private void reinstall(String deviceId, String pathToApk) {
        new InstallationProcess(deviceId, pathToApk, true);
    }

    private void clearData(String deviceId, String pkgname) {
        new ClearDataProcess(deviceId, pkgname);
    }

}

