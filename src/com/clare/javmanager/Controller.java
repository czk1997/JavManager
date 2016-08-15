package com.clare.javmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    public TextField pathField;
    @FXML
    public TableView<Avideo> infoTable;
    @FXML
    public TableColumn<Avideo, String> numberT;
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
    public String Name,Company,ReleaseDate,Path,Number;
    public StringBuilder Actors;
    public String code, path;
    public ArrayList<Avideo> avList = new ArrayList<>();
    public ObservableList<Avideo> dataOfTable = FXCollections.observableArrayList();


    public void initialize(URL url, ResourceBundle rb) {
        dbms = new Dbmsystem();
        mosicStatus.setItems(FXCollections.observableArrayList(
                "uncensored", "censored"));
        numberT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("videoCode"));
        companyT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("company"));
        NameT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("videoName"));
        rDateT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("rdataString"));
        ActorT.setCellValueFactory(new PropertyValueFactory<Avideo, String>("actors"));
        infoTable.setItems(dataOfTable);
        updateTable();


    }

    public void clickPathField() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        pathField.setText(file.toPath().toString());
        getNumberOfFile(file);
    }

    public Avideo clickScanButton() {
        String url = "https://www.javbus.com/" + code;
        System.out.println(url);
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
                System.out.println(url);
                Connection.Response response = Jsoup.connect(url).execute();
                System.out.println(response.statusCode() + " : " + response.url());
                result = response.body();
                code = code.replaceAll("-", "_");
            } catch (IOException e3) {
                e3.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
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
        Name=new String();
        Company=new String();
        Actors=new StringBuilder();
        ReleaseDate=new String();
        Path=pathField.getText();
        Number=new String();
        imagePath=new String();
        //初始化同时加载识别码
        Pattern p = Pattern.compile("<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">\\d+-\\d+</span></p>");
        String temp = new String();
        Matcher matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">";
            temp = temp.replace(temp2, "").replace("</span></p>", "");
            System.out.println(temp);
            avideo.setVideoCode(temp);
            Number=temp;
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
            System.out.println(temp);
            avideo.setCompany(temp);
            Company=temp;
            temp = new String();
            matcher.reset();
        }
        p = Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"";
            temp = temp.replaceAll(temp2, "").replace("\">", "").replaceAll("\\s", " ");
            System.out.println(temp);
            avideo.setVideoName(temp);
            Name=temp;
            temp = new String();
            matcher.reset();
        }

        p = Pattern.compile("<meta name=\"description\" content=\"【發行日期】(.+?|(.+?)\n(.+?))>");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<meta name=\"description\" content=\"【發行日期】";
            temp = temp.replaceAll(temp2, "").replaceAll("，【長度】((.+?)|(.+?)\n(.+?))>", "");
            System.out.println(temp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                avideo.setReleaseDate(sdf.parse(temp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ReleaseDate=temp;
            temp = new String();
            matcher.reset();
        }

        p = Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher = p.matcher(resultOfSpider);
        if (matcher.find()) {
            temp = matcher.group(0).toString();
            String temp2 = "<a class=\"bigImage\" href=\"(.+?)\"><img src=\"";
            temp = temp.replaceAll(temp2, "").replaceAll("\" title(.+?|.+?\\n.+?)\">", "").replaceAll("\\s", " ");
            System.out.println(temp);
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
            System.out.println(temp);
            String temp2 = "<div class=\"star-name\"><a href=\"(.+?)\" title=\"(.+?)\">";
            temp = temp.replaceAll(temp2, "").replaceAll("</a></div>", "").replaceAll("\\s", " ");
            System.out.println(temp);

            Actors.append(temp+" , ");
            temp = new String();
        }
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

    }

    public void addToDatabase() {
        boolean flag = (mosicStatus.getSelectionModel().isSelected(0) == true);
        int a = dbms.insertNewMovie(NameField.getText(), compamyField.getText(), pathField.getText(), rDateField.getText(), mosicStatus.getSelectionModel().getSelectedIndex(), actorField.getText(), numberField.getText(), imagePath);
    }
    public void listFilesForFolder(){
        File folder=new File(folderPath.getText());
        File[] listOfFiles = folder.listFiles();
        ArrayList<Avideo> avList2 = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                pathField.setText(listOfFiles[i].getName());
                getNumberOfFile(listOfFiles[i]);
                Avideo tempavdeio = clickScanButton();
                path = folderPath.getText() + "\\" + listOfFiles[i].getName();
                tempavdeio.setFilepath(path);
                addToDatabase(tempavdeio);
            }

        }
        updateTable();
    }

    public void clickFolderPathField(){
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(null);
        folderPath.setText(dir.getAbsolutePath());

    }

    public void updateTable() {
        System.out.println("cleared");
        dataOfTable.clear();
        ResultSet resultSet = null;
        resultSet = dbms.getAllMovies();
        if (resultSet != null) {
            System.out.println("Its not null");
        }
        try {
            while (resultSet.next()) {

                System.out.println("Nice!");
                resultSet.getString(1);
                dataOfTable.add(new Avideo(resultSet.getString("Numbers"), resultSet.getString("movieName"), resultSet.getString("Company"), resultSet.getString("Actors"), resultSet.getString("ReleaseDate")));

            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}




