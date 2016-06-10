package com.clare.javmanager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable{
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


    public void initialize(URL url, ResourceBundle rb) {


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
        BufferedReader in = null;
        try {
            Connection.Response response = Jsoup.connect(url).execute();
            System.out.println(response.statusCode() + " : " + response.url());
            result = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fillTheFields(result);
    }

    public String getNumberOfFile(File file) {
        String fileNameWithoutExt = file.getName().substring(0, file.getName().lastIndexOf("."));
        String numbers = new String();
        if (fileNameWithoutExt.toLowerCase().trim().contains("carib")) {
            Pattern p;
            int startChar = 0, endChar = 0;
            p = Pattern.compile("\\d+-\\d+");
            Matcher m = p.matcher(fileNameWithoutExt);
            if (m.find()) {
                fileNameWithoutExt = m.group(0);
                numberField.setText(fileNameWithoutExt);
            } else {
                fileNameWithoutExt = "-1";
            }
        }

        return fileNameWithoutExt;
    }

    public void fillTheFields(String results) {
        //初始化同时加载识别码
        Pattern p=Pattern.compile("<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">\\d+-\\d+</span></p>");
        String temp=new String();
        Matcher matcher=p.matcher(results);
        if(matcher.find()){
            temp=matcher.group(0).toString();
            String temp2="<p><span class=\"header\">識別碼:</span> <span style=\"color:#CC0000;\">";
            temp=temp.replace(temp2,"").replace("</span></p>","");
            System.out.println(temp);
            numberField.setText(temp);
            temp=new String();
            matcher.reset();
        }
        //处理公司名
        p=Pattern.compile("<p><span class=\"header\">製作商:</span> <a href=\"(.+?)\">(.+?)</a></p>");
        matcher=p.matcher(results);
        if(matcher.find()){
            temp=matcher.group(0).toString();
            String temp2="<p><span class=\"header\">製作商:</span> <a href=\"(.+?)\">";
            temp=temp.replaceAll(temp2,"").replace("</a></p>","");
            System.out.println(temp);
            compamyField.setText(temp);
            temp=new String();
            matcher.reset();
        }
        p=Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher=p.matcher(results);
        if(matcher.find()){
            temp=matcher.group(0).toString();
            String temp2="<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"";
            temp=temp.replaceAll(temp2,"").replace(">","").replaceAll("\\s"," ");
            System.out.println(temp);
            NameField.setText(temp);
            temp=new String();
            matcher.reset();
        }

        p=Pattern.compile("<meta name=\"description\" content=\"【發行日期】(.+?|(.+?)\n(.+?))>");
        matcher=p.matcher(results);
        if(matcher.find()){
            temp=matcher.group(0).toString();
            String temp2="<meta name=\"description\" content=\"【發行日期】";
            temp=temp.replaceAll(temp2,"").replaceAll("，【長度】((.+?)|(.+?)\n(.+?))>","");
            System.out.println(temp);
            rDateField.setText(temp);
            temp=new String();
            matcher.reset();
        }

        p=Pattern.compile("<a class=\"bigImage\" href=\"(.+?)\"><img src=\"(.+?)\" title=\"(.+?|.+?\\n.+?)\">");
        matcher=p.matcher(results);
        if(matcher.find()){
            temp=matcher.group(0).toString();
            String temp2="<a class=\"bigImage\" href=\"(.+?)\"><img src=\"";
            temp=temp.replaceAll(temp2,"").replaceAll("\" title(.+?|.+?\\n.+?)\">","").replaceAll("\\s"," ");
            System.out.println(temp);
            Image image=new Image(temp);
            coverP.setImage(image);
            temp=new String();
            matcher.reset();
        }




    }

}
