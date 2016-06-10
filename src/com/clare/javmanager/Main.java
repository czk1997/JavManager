package com.clare.javmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Jav Manager");
        primaryStage.setScene(new Scene(root, 1011, 811));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
