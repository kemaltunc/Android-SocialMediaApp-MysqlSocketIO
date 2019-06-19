package com.example.kemal.seniorproject.Model;

public class Company {
    private String companyId;
    private String name;
    private String sector;
    private String image_url;


    private Double point;

    public Company(String companyId, String name,String sector, String image_url, Double point) {
        this.companyId = companyId;
        this.name = name;
        this.sector = sector;
        this.image_url = image_url;
        this.point = point;
    }

    public Company(String companyId, String name, String sector, String image_url) {
        this.companyId = companyId;
        this.name = name;
        this.sector = sector;
        this.image_url = image_url;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }
}


