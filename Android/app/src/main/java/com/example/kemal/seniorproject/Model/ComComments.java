package com.example.kemal.seniorproject.Model;

public class ComComments {

    private int CommentId;
    private String content;
    private float rating;
    private String date;

    private String image;
    private String user;

    private String type;

    public ComComments() {
    }

    public ComComments(int commentId, String content, String date, String image, String user,String type) {
        CommentId = commentId;
        this.content = content;
        this.date = date;
        this.image = image;
        this.user = user;
        this.type=type;
    }

    public ComComments(int commentId, String content, float rating, String date, String image, String user,String type) {
        CommentId = commentId;
        this.content = content;
        this.rating = rating;
        this.date = date;
        this.image = image;
        this.user = user;
        this.type=type;
    }

    public int getCommentId() {
        return CommentId;
    }

    public void setCommentId(int commentId) {
        CommentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
