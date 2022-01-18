package com.health_care.model;

public class UserInfo {

    String id, name, email, gender, password,status,distance;
    int accountType;// 1medical Stuff // 2- pa

    public UserInfo(String id, String name, String email, String gender, String password, int accountType, String status,String distance){
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.accountType = accountType;
        this.status = status;
        this.distance = distance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }
}
