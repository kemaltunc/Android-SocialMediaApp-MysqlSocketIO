package com.example.kemal.seniorproject.Model;

public class User {
    private String userId;
    private String name;
    private String surname;
    private String image_url;

    private String joinId;
    private String companyId;

    public User() {
    }

    public User(String userId, String name, String surname, String image_url) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.image_url = image_url;
    }

    public User(String joinId,String userId,String name,String surname,String image_url,String companyId){
        this.joinId=joinId;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.image_url = image_url;
        this.companyId = companyId;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getJoinId() {
        return joinId;
    }

    public void setJoinId(String joinId) {
        this.joinId = joinId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
