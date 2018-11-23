package com.QAHelper;


import java.io.File;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class Controller {

    private ObservableList<String> detectedDevices;
    private String versionCode;
    private String sdeviceID;
    private String pathToApk;
    private String pathToObb;
    private String obbName;

    boolean isAll = true;

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
        getDevices();
        if (detectedDevices.size() != 0) sdeviceID = detectedDevices.get(0);
        isAll = all_devices.isSelected();
    }

    @FXML
    void changeIsAll() {
        isAll = all_devices.isSelected();
    }

    void getVersionCodeApk() {
        versionCode = null;
        versionLabel.setText("");
        try {
            versionCode = ops.getApkVersion(appPackageField.getText(), deviceList.getSelectionModel().getSelectedItem());
            versionLabel.setText(versionCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
        getVersionCodeApk();
    }

    @FXML
    void onScanDeviceBtnClick() {
        getDevices();
    }


    @FXML
    private void runAction() {

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                uiAction();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            System.out.println(sdeviceID);
            updateSelectedDeviceInfo();
        });

        new Thread(task).start();
    }

    private void uiAction() {
        if (detectedDevices.size() != 0) {
            if (uninstallRBtn.isSelected()) {
                if (appPackageField.getText().length() > 1) {
                    ops.uninstallApk(detectedDevices, appPackageField.getText(), sdeviceID, isAll);
                } else {
                    errorAlert("Package name is empty!");
                }
            }
            if (installRBtn.isSelected()) {
                    if (pathToApk != null) {
                        ops.installApk(detectedDevices, pathToApk, sdeviceID, isAll);
                    }
                    else errorAlert("Choose apk file for install!");
            }
            if (installObbApkBtn.isSelected()) {
                        if (pathToApk != null && pathToObb != null) {
                            ops.installObb(detectedDevices, appPackageField.getText(), pathToObb, obbName);
                        } else {
                            errorAlert("Choose apk/obb file for install!");
                        }
            }
            if (reinstallRBtn.isSelected()) {
                if (pathToApk != null) {
                    ops.reinstallApk(detectedDevices, pathToApk, sdeviceID, isAll);
                }
                else errorAlert("Choose apk file for install!");
            }
            if (removeDataRBtn.isSelected()) {
                if (appPackageField.getText().length() > 1) {
                    ops.clearApkData(detectedDevices, appPackageField.getText(), sdeviceID, isAll);
                } else {
                    errorAlert("Package name is empty!");
                }
            }
        } else {
            errorAlert("Device list is empty!");
        }
    }

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

    private void getDevices() {
        try {
            detectedDevices = FXCollections.observableArrayList(ops.getDeviceList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        deviceList.setItems(detectedDevices);
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
}
