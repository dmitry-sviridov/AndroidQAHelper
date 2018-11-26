package com.QAHelper;


import java.io.File;
import java.io.IOException;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;



public class Controller {

    private ObservableList<String> detectedDevices;

    private String sdeviceID;
    private String pathToApk;
    private String pathToObb;
    private String obbName;
    private String pkg;

    boolean isAll = true;
    boolean reinstall = false;

    AdbOperations ops = new AdbOperations();

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

    @FXML
    private ListView<String> deviceList;

    @FXML
    private Label deviceNameLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private TextField appPackageField;

    @FXML
    private TextField appObbPath;

    @FXML
    private TextField appPackagePath;

    @FXML
    private RadioButton uninstallRBtn;

    @FXML
    private RadioButton installRBtn;

    @FXML
    private RadioButton installObbApkBtn;

    @FXML
    private RadioButton reinstallRBtn;

    @FXML
    private RadioButton removeDataRBtn;

    @FXML
    private CheckBox all_devices;

    @FXML
    private ToggleGroup rbgroup;

    @FXML
    void initialize() {
        try {
            Runtime.getRuntime().exec("adb start-server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        detectedDevices = null;
        deviceNameLabel.setText("");
        pkg = appPackageField.getText();
        getDevices();
        if (detectedDevices.size() != 0) sdeviceID = detectedDevices.get(0);
        isAll = all_devices.isSelected();

        // Get changes in radiobutton group
        rbgroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                reinstall = reinstallRBtn.isSelected();
            }
        });
    }

    // Is_All checkbox monitoring
    @FXML
    void changeIsAll() {
        isAll = all_devices.isSelected();
    }


    // App-package field monitoring
    @FXML
    void onPkgChanged() {
        pkg = appPackageField.getText();
        System.out.println(pkg);
    }

    // Updating info by clicking on device in list
    @FXML
    void updateSelectedDeviceInfo() {
        sdeviceID = null;
        deviceNameLabel.setText("");
        sdeviceID = deviceList.getSelectionModel().getSelectedItem();
        try {
            deviceNameLabel.setText(ops.getDeviceModel(sdeviceID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        versionLabel.setText("");
        runUpdateDeviceInfoTask();
    }

    void runUpdateDeviceInfoTask() {
        UpdateDeviceInfoTask task = new UpdateDeviceInfoTask(sdeviceID, pkg);
        task.setOnSucceeded(event -> {
            versionLabel.setText(task.getValue());
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task);
        executorService.shutdown();
    }

    @FXML
    void onScanDeviceBtnClick() {
        getDevices();
    }

    @FXML
    private void runAction() {
        if (detectedDevices.size() != 0) {
            if (rbgroup.getSelectedToggle() == installRBtn || rbgroup.getSelectedToggle() == reinstallRBtn) {
                if (pathToApk != null) {
                    installApk(detectedDevices, pathToApk, sdeviceID, isAll);
                } else
                    errorAlert("Incorrect path to apk");
            }
            if (rbgroup.getSelectedToggle() == uninstallRBtn) {
                if (pkg.length() > 3) {
                    uninstallApk(detectedDevices, pkg, sdeviceID, isAll);
                } else
                    errorAlert("Enter correct bundleID");
            }
            if (rbgroup.getSelectedToggle() == removeDataRBtn) {
                if (pkg.length() > 3) {
                    clearApkData(detectedDevices, pkg, sdeviceID, isAll);
                } else
                    errorAlert("Enter correct bundleID");
            }
            if (rbgroup.getSelectedToggle() == installObbApkBtn) {
                if (pathToObb != null || pkg.length() > 3) {
                    installObb(detectedDevices, pkg, sdeviceID, pathToObb, obbName);
                } else
                    errorAlert("Enter correct bundleID and path to Obb file");
            }

        } else errorAlert("Device list is empty");
    }


    // File-opening dialog by clicking on text-area 'path to apk'
    @FXML
    void openApkFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set apk under installing");
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Apk files (*.apk)", "*.apk");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pathToApk = file.getAbsolutePath();
            appPackagePath.setText(pathToApk);
        }
    }

    @FXML
    void openObbFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set Obb under installing");
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Obb files (*.obb)", "*.obb");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            obbName = file.getName();
            pathToObb = file.getAbsolutePath();
            appObbPath.setText(pathToObb);
        }
    }

    // Scan adb-devices
    private void getDevices() {
        try {
            detectedDevices = FXCollections.observableArrayList(ops.getDeviceList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        deviceList.setItems(detectedDevices);
    }



    void uninstallApk(List<String> deviceIdList, String pkgName, String sdeviceId, boolean isAll) {
        if (isAll) {
            deviceIdList.forEach(device -> uninstall(device, pkgName));
        } else {
            uninstall(sdeviceId, pkgName);
        }
    }

    private void uninstall(String deviceId, String pkg) {
        RemoveTask task = new RemoveTask(deviceId, pkg);
        task.setOnSucceeded((event -> {
            runUpdateDeviceInfoTask();
        }));
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task);
        executorService.shutdown();
    }

    void clearApkData(List<String> deviceIdList, String pkgName, String sdeviceId, boolean isAll) {
        if (isAll) {
            deviceIdList.forEach(device -> clearData(device, pkgName));
        } else {
            clearData(sdeviceId, pkgName);
        }
    }

    private void clearData(String deviceID, String pkg) {
        ClearDataTask task = new ClearDataTask(deviceID, pkg);
        task.setOnSucceeded(event -> {
            System.out.println("Clear data on " + deviceID +" Success");
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task);
        executorService.shutdown();
    }

    void installApk(List<String> deviceIdList, String pathToApk, String sdeviceId, boolean isAll) {
        if (isAll) {
            deviceIdList.forEach(device -> install(device, pathToApk));
        } else {
            install(sdeviceId, pathToApk);
        }
    }

    private void install(String deviceId, String pathToApk) {
        InstallationTask task = new InstallationTask(deviceId, pathToApk, reinstall);
        task.setOnSucceeded(event -> {
            if (deviceId.equals(sdeviceID)) {
                runUpdateDeviceInfoTask();
            }
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task);
        executorService.shutdown();
    }


    void installObb(List<String> deviceIdList, String appPackageName, String sdeviceID, String pathToObb, String obbName) {
        if (isAll) {
            deviceIdList.forEach(device -> installObbfile(device, appPackageName, pathToObb, obbName));
        } else
            installObbfile(sdeviceID, appPackageName, pathToObb, obbName);

    }

    private void installObbfile(String deviceId, String appPackageName, String pathToObb, String obbName) {
        InstallObbTask task = new InstallObbTask(deviceId, appPackageName, pathToObb, obbName);
        task.setOnSucceeded(event -> {
            System.out.println("Install obb" + deviceId +" Success");
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task);
        executorService.shutdown();
    }

    /**
     * Clipboard
     */

    @FXML
    void copyToClipboard(MouseEvent event) {
        content.putString(versionLabel.getText());
        clipboard.setContent(content);
    }

    /**
     * Alerts
     */

    static void errorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("WTF?");
        alert.setContentText(text);
        alert.showAndWait();
    }

    static void warningAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Hm...");
        alert.setContentText(text);
        alert.showAndWait();
    }

}
