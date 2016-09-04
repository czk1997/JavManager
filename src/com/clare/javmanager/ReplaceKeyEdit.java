package com.clare.javmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/**
 * Created by chenz on 8/17/2016.
 */
public class ReplaceKeyEdit {

    @FXML
    public static Button editB;
    @FXML
    public static Button addB;
    @FXML
    public static Button okB;
    @FXML
    public static TableView<Replacekey> rpkTable;
    @FXML
    public static TableColumn<Replacekey, String> orkey;
    @FXML
    public static TableColumn<Replacekey, String> newkey;
    @FXML
    public static TextField orkeyT;
    @FXML
    public static TextField newKeyT;


    public static Dbmsystem dbms = null;
    public static GridPane PrPane = null;
    static Stage stage;
    static ObservableList<Replacekey> dataOfTable = FXCollections.observableArrayList();

    public ReplaceKeyEdit(int i) {

    }

    public ReplaceKeyEdit() {
    }

    public void oKButtonClicked() {


        Stage s2 = (Stage) rpkTable.getScene().getWindow();
        s2.close();
        PrPane.setDisable(false);


    }

    public void setPaneAndDbms(GridPane pane, Dbmsystem dbms2) throws Exception {
        System.out.println(dbms2);
        PrPane = pane;
        dbms = dbms2;
        stage = new Stage();
        Parent root = null;


        root = FXMLLoader.load(getClass().getResource("ReplaceKeyEdit.fxml"));
        rpkTable.setItems(dataOfTable);
        orkey.setCellValueFactory(new PropertyValueFactory<Replacekey, String>("originalKey"));
        newkey.setCellValueFactory(new PropertyValueFactory<Replacekey, String>("newKey"));
        System.out.println("loaded!");
        Scene scene = new Scene(root);
        stage.setTitle("Edit Replace Key");
        stage.setScene(scene);
           /* Platform.runLater(new Runnable() {
                @Override public void run() {

                    dataOfTable.add(new Replacekey("COOl","Nice"));
                }
            });*/


        stage.show();
        PrPane.setDisable(true);

            /*Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Some Thing went error");
            alert.setTitle("It is too hard to open the new window.\nplease contact the author.");
            e.printStackTrace();*/

    }

    public void onClickAddbutton() {
        dbms.addRPkey(orkeyT.getText(), newKeyT.getText());


    }


}
