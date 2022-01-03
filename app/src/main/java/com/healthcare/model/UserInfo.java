package com.healthcare.model;

public class UserInfo {

    String name, email, gender, password;
    int accountType;// 1medical Stuff // 2- pa

    public UserInfo(String name, String email, String gender, String password, int accountType){
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.accountType = accountType;
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
}
