package com.clare.javmanager;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by chenz on 8/16/2016.
 */
public class Progress {

    public Label infoLabel;

    public ProgressBar progress = new ProgressBar();
    public Scene secondScene;
    public Stage secondStage, prstage;
    public Button cancelbutton;
    public Thread PRthread;
    public Pane pane;

    public Progress(Pane pane, Thread thread, Stage stage, String labelContent) {
        PRthread = thread;
        prstage = stage;
        infoLabel = new Label(labelContent);
        cancelbutton = new Button("Cancel");
        VBox secondaryLayout = new VBox();
        secondaryLayout.setAlignment(Pos.CENTER);
        secondaryLayout.setSpacing(30);
        secondaryLayout.getChildren().add(infoLabel);
        this.pane = pane;
        cancelbutton.setOnMouseClicked(new onClicked());
        progress.setPrefSize(600, 60);

        secondaryLayout.getChildren().add(progress);
        secondaryLayout.getChildren().add(cancelbutton);


        secondScene = new Scene(secondaryLayout, 800, 200);

        secondStage = new Stage();
        secondStage.initStyle(StageStyle.TRANSPARENT);
        secondStage.setTitle(labelContent);
        secondStage.setScene(secondScene);

        secondStage.show();


    }

    public void updateProgress(double percent) {
        this.progress.setProgress(percent);

    }

    public void updateLabel(int current, int total) {
        infoLabel.setText("Now progress " + current + " / " + total);
    }

    public void endStage() {
        System.out.println("it conmes here");

        secondStage.setAlwaysOnTop(false);
        secondStage.close();

    }

    class onClicked implements EventHandler<MouseEvent> {


        @Override
        public void handle(MouseEvent event) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel the current program");
            alert.setContentText("Are you sure to cancel current task?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                PRthread.stop();
                secondStage.close();
                pane.setDisable(false);
            }


        }
    }


}
