package com.example.sportwithfriends.pojo;

public class Exercise {
    private String title;
    private String description;
    private long dateTime;
    private String place;
    private String type;
    private int countOfPlayer;

    private long dateOfCreate;

    public Exercise(String title, String description, long dateTime, String place, String type, int countOfPlayer, long dateOfCreate) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.place = place;
        this.type = type;
        this.countOfPlayer = countOfPlayer;
        this.dateOfCreate = dateOfCreate;
    }

    public Exercise() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCountOfPlayer() {
        return countOfPlayer;
    }

    public void setCountOfPlayer(int countOfPlayer) {
        this.countOfPlayer = countOfPlayer;
    }

    public long getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(long dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }
}
