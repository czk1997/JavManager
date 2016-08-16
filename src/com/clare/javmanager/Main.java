package com.clare.javmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Jav Manager");
        primaryStage.setScene(new Scene(root, 1600, 900));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.show();


    }
}