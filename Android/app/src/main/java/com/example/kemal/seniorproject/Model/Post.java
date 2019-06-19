package com.example.kemal.seniorproject.Model;

public class Post {
    int postId;
    String content;
    String date;
    String companyName;
    String image_url;
    String company_image_url;
    String user_image_url;
    String like;
    String comment;
    Boolean likeControl;
    Boolean favControl;

    Double randomNumber;

    public Post() {
    }

    public Post(int postId, String image_url) {
        this.postId = postId;
        this.image_url = image_url;
    }

    public Post(int postId, Double randomNumber, String image_url) {
        this.postId = postId;
        this.randomNumber = randomNumber;
        this.image_url = image_url;
    }

    public Post(int postId, String content, String date, String companyName, String image_url, String company_image_url, String user_image_url, String like, String comment, Boolean likeControl,Boolean favControl) {
        this.postId = postId;
        this.content = content;
        this.date = date;
        this.companyName = companyName;
        this.image_url = image_url;
        this.company_image_url = company_image_url;
        this.user_image_url = user_image_url;
        this.like = like;
        this.comment = comment;
        this.likeControl = likeControl;
        this.favControl=favControl;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCompany_image_url() {
        return company_image_url;
    }

    public void setCompany_image_url(String company_image_url) {
        this.company_image_url = company_image_url;
    }

    public String getUser_image_url() {
        return user_image_url;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getLikeControl() {
        return likeControl;
    }

    public void setLikeControl(Boolean likeControl) {
        this.likeControl = likeControl;
    }

    public Double getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(Double randomNumber) {
        this.randomNumber = randomNumber;
    }

    public Boolean getFavControl() {
        return favControl;
    }

    public void setFavControl(Boolean favControl) {
        this.favControl = favControl;
    }
}

