package com.example.sportwithfriends.pojo;

public class News {
    private String title;
    private String contentDescription;

    public News(String title, String contentDescription) {
        this.title = title;
        this.contentDescription = contentDescription;
    }

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }
}
