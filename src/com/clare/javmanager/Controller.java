package com.clare.javmanager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    @FXML
    public TextField numberField;
    @FXML
    public TextField compamyField;
    @FXML
    public TextField NameField;
    @FXML
    public TextField actorField;
    @FXML
    public TextField rDateField;
    @FXML
    public Button refershImagebutton;
    @FXML
    public Button updateButton;
    @FXML
    public TextField pathField;
    @FXML
    public TextField codeField;
    @FXML
    public TableView<Avideo> infoTable;
    @FXML
    public TableColumn<Avideo, String> numberT;
    @FXML
    public TableColumn<Avideo, Integer> dbidT;
    @FXML
    public TableColumn<Avideo, String> companyT;
    @FXML
    public TableColumn<Avideo, String> NameT;
    @FXML
    public TableColumn<Avideo, String> rDateT;
    @FXML
    public TableColumn<Avideo, String> ActorT;
    @FXML
    public ImageView coverP;
    @FXML
    public Button scanB;
    public Dbmsystem dbms = null;
    @FXML
    public ChoiceBox mosicStatus;
    @FXML
    public Button addButton;
    public String imagePath = new String();
    @FXML
    public Button scanFolder;
    @FXML
    public TextField folderPath;
    public String Name, Company, ReleaseDate, Path, Number;
    public StringBuilder Actors;
    public String code, path;
    public ArrayList<Avideo> avList = new ArrayList<>();
    public ObservableList<Avideo> dataOfTable = FXCollections.observableArrayList();
    public Thread thread = new Thread();
    public Progress progress;
    @FXML
    GridPane AllContent;
    @FXML
    Pane content;

    public void initialize(URL url, ResourceBundle rb) {
        dbms = new Dbmsystem();
        mosicStatus.setItems(FXCollections.observableArrayList(
                "uncensored", "censored"));
        numberT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("videoCode"));
        companyT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("company"));
        NameT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("videoName"));
        rDateT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("rdataString"));
        ActorT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("actors"));
        dbidT.setCellValueFactory(new PropertyValueFactory<Avideo, Integer>("dbid"));

        infoTable.setItems(dataOfTable);
        updateTable();



    }

    public void clickPathField() {
        Stage stage = (Stage) AllContent.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        pathField.setText(file.toPath().toString());
        getNumberOfFile(file);
    }

    public Avideo clickScanButton() {
        String url = "https://www.javbus.com/" + code;

        String result = "";
        boolean flag = false;
        try {
            Connection.Response response = Jsoup.connect(url).execute();
            System.out.println(response.statusCode() + " : " + response.url());
            result = response.body();
            flag = true;
        } catch (HttpStatusException e2) {

            try {
                url = "https://www.javbus.com/" + code.replaceAll("-", "_");
                Connection.Response response = Jsoup.connect(url).execute();
                System.out.println(response.statusCode() + " : " + response.url());
                result = response.body();
                code = code.replaceAll("-", "_");
            } catch (IOException e3) {
                e3.printStackTrace();
            }


        } catch (Exception e) {
            thread.stop();
            progress.endStage();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went error!");
            alert.setContentText("Something went error, but we donnot what is it!");
            alert.showAndWait();

        }

        Avideo avideo = newMovie(result);
        return avideo;
    }

    public String getNumberOfFile(File file) {
        String fileNameWithoutExt = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf(".")).replaceAll("_", "-").replace("-hd-", "-").replace("-fhd-", "").replace("-1080p-", "-").replace("-full-", "-").replaceAll("s model", "SMBD-").replace("1080p", "").replace("fhd", "");
        Pattern p2 = Pattern.compile("(\\d+-\\d+)|(\\[a-z]+-\\d+)|([a-z]+-\\d+)|(\\[a-z]+\\d+)|(xxx-av-\\d+)");
        Matcher m2 = p2.matcher(fileNameWithoutExt);
        Pattern p3 = Pattern.compile("(n\\d+)|(k\\d+)");
        Matcher m3 = p3.matcher(fileNameWithoutExt);
        if (m2.find()) {
            fileNameWithoutExt = m2.group(0);

            code = m2.group(0);
            if (fileNameWithoutExt.matches("\\[a-z]+\\d+")) {
                Pattern p = Pattern.compile("\\[a-z]+");
                Matcher m = p.matcher(fileNameWithoutExt);
                if (m.find()) {
                    String words = m.group(0);
                    p = Pattern.compile("\\d+");
                    m.reset();
                    m = p.matcher(fileNameWithoutExt);
                    m.find();
                    String numbers = m.group(0);
                    fileNameWithoutExt = words + "-" + numbers;
                }

                code = fileNameWithoutExt;
            }

        } else if (m3.find()) {
            fileNameWithoutExt = m3.group(0);
            code = m3.group(0);

        }


        return fileNameWithoutExt;
    }

    public Avideo newMovie(String resultOfSpider) {
        Avideo avideo = new Avideo();
        Name = new String();
        Company = new String();
        Actors = new StringBuilder();
        ReleaseDate = new String();
        Path = pathField.getText();
        Number = new String();
        imagePath = new String();
        //初始化同时加载识别码
        Pattern p = Pattern.compile("<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">\\d+-\\d+</span></p>");
        String temp = new String();
        Matcher matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">";
            temp = temp.replace(temp2, "").replace("</span></p>", "");
            numberField.setText(temp);
            avideo.setVideoCode(temp);
            Number = temp;
            temp = new String();
            matcher.reset();
        }
        //处理公司名
        p = Pattern.compile("<p><span class=\"header\">製作商:</span> <a href=\"(.+?)\">(.+?)</a></p>");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<p><span class=\"header\">製作商:</span> <a href=\"(.+?)\">";
            temp = temp.replaceAll(temp2, "").replace("</a></p>", "");
            compamyField.setText(temp);
            avideo.setCompany(temp);
            Company = temp;
            temp = new String();
            matcher.reset();
        }
        p = Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"";
            temp = temp.replaceAll(temp2, "").replace("\">", "").replaceAll("\\s", " ");
            NameField.setText(temp);
            avideo.setVideoName(temp);
            Name = temp;
            temp = new String();
            matcher.reset();
        }

        p = Pattern.compile("<meta name=\"description\" content=\"【發行日期】(.+?|(.+?)\n(.+?))>");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<meta name=\"description\" content=\"【發行日期】";
            temp = temp.replaceAll(temp2, "").replaceAll("，【長度】((.+?)|(.+?)\n(.+?))>", "");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                avideo.setReleaseDate(sdf.parse(temp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ReleaseDate = temp;
            rDateField.setText(temp);
            temp = new String();
            matcher.reset();
        }

        p = Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<a class=\"bigImage\" href=\"(.+?)\"><img src=\"";
            temp = temp.replaceAll(temp2, "").replaceAll("\" title(.+?|.+?\\n.+?)\">", "").replaceAll("\\s", " ");
            avideo.setImagePath(temp);
            imagePath = temp;
            Image image = new Image(temp);
            coverP.setImage(image);
            temp = new String();
            matcher.reset();
        }
        p = Pattern.compile("<div class=\"star-name\"><a href=\"(.+?)\" title=\"(.+?)\">(.+?)</a></div>");
        matcher = p.matcher(resultOfSpider);
        actorField.clear();
        while (matcher.find()) {
            temp = matcher.group(3).toString();

            String temp2 = "<div class=\"star-name\"><a href=\"(.+?)\" title=\"(.+?)\">";
            temp = temp.replaceAll(temp2, "").replaceAll("</a></div>", "").replaceAll("\\s", " ");


            Actors.append(temp + " , ");
            temp = new String();
        }
        actorField.setText(Actors.toString());
        avideo.setActors(Actors);
        matcher.reset();
        if (resultOfSpider.contains("<li class=\"active\"><a href=\"https://www.javbus.com/\">有碼</a></li>")) {
            avideo.setMosicStatus(true);
        } else if (resultOfSpider.contains("<li class=\"active\"><a href=\"https://www.javbus.com/uncensored\">無碼</a></li>")) {
            avideo.setMosicStatus(false);

        }
        return avideo;
    }

    public void addToDatabase(Avideo avideo) {
        boolean flag = (mosicStatus.getSelectionModel().isSelected(0) == true);
        int a = dbms.insertNewMovie(avideo.getVideoName(), avideo.getCompany(), avideo.getFilepath(), avideo.getStringDate(), avideo.getIntStatus(), avideo.getActors(), code, imagePath);
        updateTable();
        infoTable.getSelectionModel().selectLast();
        infoTable.scrollTo(infoTable.getItems().size() - 1);
    }

    public void addToDatabase() {
        boolean flag = (mosicStatus.getSelectionModel().isSelected(0) == true);
        int a = dbms.insertNewMovie(NameField.getText(), compamyField.getText(), pathField.getText(), rDateField.getText(), mosicStatus.getSelectionModel().getSelectedIndex(), actorField.getText(), numberField.getText(), imagePath);
        updateTable();
        infoTable.getSelectionModel().selectLast();
        infoTable.scrollTo(infoTable.getItems().size() - 1);
    }

    public void listFilesForFolder() {
        Stage stage = (Stage) AllContent.getScene().getWindow();
        File folder = new File(folderPath.getText());
        File[] listOfFiles = folder.listFiles();
        ArrayList<Avideo> avList2 = new ArrayList<>();


        content.setDisable(true);

        progress = new Progress(content, thread, stage, "Now Progressing");
        Task task = new Task() {
            @Override
            protected Object call() {

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        pathField.setText(listOfFiles[i].getName());
                        getNumberOfFile(listOfFiles[i]);
                        Avideo tempavdeio = clickScanButton();
                        path = folderPath.getText() + "\\" + listOfFiles[i].getName();
                        tempavdeio.setFilepath(path);
                        addToDatabase(tempavdeio);
                        updateTable();
                    }
                    int current = i;
                    Double percent = ((double) i + 1) / (double) listOfFiles.length;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progress.updateProgress(percent);
                            progress.updateLabel(current, listOfFiles.length);
                            infoTable.getSelectionModel().selectLast();
                        }
                    });


                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progress.endStage();
                        progress.secondStage.setAlwaysOnTop(false);
                        progress.secondStage.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Import Complete!");
                        alert.setContentText(listOfFiles.length + " video(s) has been imported successful.");
                        alert.showAndWait();
                        content.setDisable(false);
                    }
                });

                return null;
            }
        };
        thread = new Thread(task);
        progress.PRthread = thread;

        thread.start();



    }

    public void clickFolderPathField() {
        Stage stage = (Stage) AllContent.getScene().getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        folderPath.setText(dir.getAbsolutePath());

    }

    public void updateTable() {

        dataOfTable.clear();
        ResultSet resultSet = null;
        resultSet = dbms.getAllMovies();

        try {
            while (resultSet.next()) {
                dataOfTable.add(new Avideo(resultSet.getInt("UID"), resultSet.getString("Numbers"), resultSet.getString("movieName"), resultSet.getString("Company"), resultSet.getString("Actors"), resultSet.getString("ReleaseDate")));

            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void onTableClicked() {

        String sql = "SELECT * FROM Movies WHERE UID=" + infoTable.getSelectionModel().getSelectedItem().getDbid();
        ResultSet resultSet = dbms.getMovieInfo(infoTable.getSelectionModel().getSelectedItem().getDbid());
        try {
            while (resultSet.next()) {
                codeField.setText(resultSet.getString("UID"));
                numberField.setText(resultSet.getString("Numbers"));
                compamyField.setText(resultSet.getString("Company"));
                if (resultSet.getBoolean("mosic")) {
                    mosicStatus.getSelectionModel().select(1);
                } else {
                    mosicStatus.getSelectionModel().select(0);
                }
                NameField.setText(resultSet.getString("movieName"));
                actorField.setText(resultSet.getString("Actors"));
                rDateField.setText(resultSet.getString("ReleaseDate"));
                pathField.setText(resultSet.getString("Path"));
                imagePath = resultSet.getString("imagePath");
                refershImage();


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void stopCurrentThread() {
        thread.stop();

    }

    public void refershImage() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Image image = new Image(imagePath);
                coverP.setImage(image);

                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();


    }

    public void updateInfo() {
        String sql = "UPDATE Movies SET movieName=\"" + NameField.getText() + "\", Numbers=\"" + numberField.getText() + "\", Actors=\"" + actorField.getText() + "\", Company=\"" + compamyField.getText() + "\", Path=\"" + pathField.getText() + "\", mosic=" + mosicStatus.getSelectionModel().getSelectedIndex() + " WHERE UID=" + codeField.getText();
        System.out.println(sql);
        int result = dbms.constumupdate(sql);
        if (result == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Successful!");
            alert.setContentText("This Item has already been updated.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed!");
            alert.setContentText("This Item has not been updated.");
            alert.showAndWait();
        }
    }



}







