package com.clare.javmanager;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Avideo {
    public String videoName;
    public String company,filepath,imagePath;
    public boolean mosicStatus;
    public ArrayList<String> actors;
    public Avideo(){

    }
    public void setVideoName(String videoName){
        this.videoName=videoName;
    }
    public String getVideoName(){
        return videoName;
    }
    public void setCompany(String company){
        this.company=company;
    }
    public String getCompany(){
        return company;
    }
    public void setFilepath(String filepath){
        this.filepath=filepath;
    }
    public String getFilepath(){
        return filepath;
    }
    public void setMosicStatus(boolean mosicStatus){
        this.mosicStatus=mosicStatus;
    }
    public boolean getMoiscStatus(){
        return mosicStatus;
    }
    public void setImagePath(String imagePath){
        this.imagePath=imagePath;
    }
    public void setActors(ArrayList<String> actors){
        this.actors=actors;
    }
    public String getImagePath(){
        return imagePath;
    }
    public ArrayList<String> getActors(){
        return actors;
    }

}
