package com.clare.javmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Avideo {
    public String videoName;
    public String company,filepath,imagePath;
    public boolean mosicStatus;
    public StringBuilder actors;
    public String videoCode = new String();
    public Date releaseDate = new Date();
    public Avideo(){


    }

    public String getVideoCode() {
        return videoCode;
    }

    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date ReleaseDate) {
        this.releaseDate = ReleaseDate;
    }

    public String getStringDate() {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(releaseDate);
    }

    public String getVideoName(){
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getCompany(){
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFilepath(){
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setMosicStatus(boolean mosicStatus){
        this.mosicStatus=mosicStatus;
    }
    public boolean getMoiscStatus(){
        return mosicStatus;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath){
        this.imagePath=imagePath;
    }

    public String getActors() {
        return actors.toString();
    }

    public void setActors(StringBuilder actors) {
        this.actors=actors;
    }

    public int getIntStatus() {
        if (mosicStatus) {
            return 1;
        } else {
            return 0;
        }
    }
}
