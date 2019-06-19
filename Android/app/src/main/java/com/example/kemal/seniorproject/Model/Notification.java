package com.example.kemal.seniorproject.Model;

public class Notification {
    private String id;
    private String content;
    private String userId;
    private String date;
    private String name;
    private String image;

    public Notification(String id, String content, String userId, String date, String name, String image) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.date = date;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
