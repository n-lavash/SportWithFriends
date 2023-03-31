package com.example.sportwithfriends.pojo;

import java.util.List;

public class User {
    private String UID;
    private String name;
    private String email;
    private String avatarUrl;
    private List<User> friends;
    private List<Exercise> exercises;
    private List<Chat> chats;

    public User(String UID, String name, String email, String avatarUrl, List<User> friends, List<Exercise> exercises, List<Chat> chats) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.friends = friends;
        this.exercises = exercises;
        this.chats = chats;
    }

    public User() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }
}
