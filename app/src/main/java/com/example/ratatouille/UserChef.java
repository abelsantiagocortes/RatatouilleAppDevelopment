package com.example.ratatouille;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class UserChef {

    String name;
    int age;
    String photoDownloadURL;
    String dir;
    double lat;
    double longi;
    String description;
    boolean status;
    List<String> tools;
    String userId;


    public UserChef(String name,  String dir,int age) {
        this.name = name;
        this.age = age;
        this.dir = dir;
        this.tools = new ArrayList<>();
        this.status = true;
        this.description = "";
    }

    public UserChef(String name, int age, int photo, String dir, double lat, double longi, String description, List<String> tools) {
        this.name = name;
        this.age = age;
        this.dir = dir;
        this.lat = lat;
        this.longi = longi;
        this.description = description;
        this.tools = tools;
        this.tools = new ArrayList<>();
    }

    public UserChef() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public String getPhotoDownloadURL() {
        return photoDownloadURL;
    }

    public void setPhotoDownloadURL(String photoDownloadURL) {
        this.photoDownloadURL = photoDownloadURL;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
