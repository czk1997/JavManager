package com.clare.javmanager;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.sqlite.core.DB;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
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
    public TableView infoTable;
    @FXML
    public TableColumn numberT;
    @FXML
    public TableColumn companyT;
    @FXML
    public TableColumn NameT;
    @FXML
    public TableColumn rDateT;
    @FXML
    public TableColumn ActorT;
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

    public void initialize(URL url, ResourceBundle rb) {
        dbms = new Dbmsystem();
        mosicStatus.setItems(FXCollections.observableArrayList(
                "uncensored", "censored"));


    }

    public void clickPathField() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        pathField.setText(file.toPath().toString());
        getNumberOfFile(file);
    }

    public void clickScanButton() {
        String url = "https://www.javbus.com/" + numberField.getText();
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
                url = "https://www.javbus.com/" + numberField.getText().replaceAll("-", "_");
                System.out.println(url);
                Connection.Response response = Jsoup.connect(url).execute();
                System.out.println(response.statusCode() + " : " + response.url());
                result = response.body();
                numberField.setText(numberField.getText().replaceAll("-", "_"));
            } catch (IOException e3) {
                e3.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        newMovie(result);
    }

    public String getNumberOfFile(File file) {
        String fileNameWithoutExt = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf(".")).replaceAll("_", "-").replace("-hd-", "-").replace("-fhd-", "").replace("-1080p-", "-").replace("-full-", "-").replaceAll("s model", "SMBD-");
        Pattern p2 = Pattern.compile("(\\d+-\\d+)|(\\[a-z]+-\\d+)|([a-z]+-\\d+)|(\\[a-z]+\\d+)|(xxx-av-\\d+)");
        Matcher m2 = p2.matcher(fileNameWithoutExt);
        Pattern p3 = Pattern.compile("(n\\d+)|(k\\d+)");
        Matcher m3 = p3.matcher(fileNameWithoutExt);
        if (m2.find()) {
            fileNameWithoutExt = m2.group(0);
            numberField.setText(m2.group(0));
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
                numberField.setText(fileNameWithoutExt);
            }

        } else if (m3.find()) {
            fileNameWithoutExt = m3.group(0);
            numberField.setText(m3.group(0));
        }


        return fileNameWithoutExt;
    }

    public void newMovie(String resultOfSpider) {
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
            numberField.setText(temp.toUpperCase());
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
            compamyField.setText(temp);
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
            NameField.setText(temp);
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
            rDateField.setText(temp);
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
            actorField.appendText(temp + ", ");
            Actors.append(temp+" , ");
            temp = new String();
        }

        matcher.reset();
        if (resultOfSpider.contains("<li class=\"active\"><a href=\"https://www.javbus.com/\">有碼</a></li>")) {
            mosicStatus.getSelectionModel().select(1);
        } else if (resultOfSpider.contains("<li class=\"active\"><a href=\"https://www.javbus.com/uncensored\">無碼</a></li>")) {
            mosicStatus.getSelectionModel().select(0);

        }

    }

    public void addToDatabase() {
        boolean flag = (mosicStatus.getSelectionModel().isSelected(0) == true);

        int a = dbms.insertNewMovie(NameField.getText(), compamyField.getText(), pathField.getText(), rDateField.getText(), mosicStatus.getSelectionModel().getSelectedIndex(), actorField.getText(), numberField.getText(), imagePath);
    }
    public void listFilesForFolder(){
        File folder=new File(folderPath.getText());
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                pathField.setText(listOfFiles[i].getName());
                getNumberOfFile(listOfFiles[i]);
                clickScanButton();
                addToDatabase();
            }
        }
    }

    public void clickFolderPathField(){
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(null);
        folderPath.setText(dir.getAbsolutePath());

    }
}




