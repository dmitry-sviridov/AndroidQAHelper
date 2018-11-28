package com.QAHelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public Stage primaryStage;
    public Stage logStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/projOverview.fxml"));
        primaryStage.setTitle("Android QAHelper");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("view/icon.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 700, 250));
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

