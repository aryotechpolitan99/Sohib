package com.aryotech.sohib.model;

import android.widget.EditText;

public class Users {

    private String idUsers;
    private String userName;
    private String fullName;
    private String password;
    private String imageUrl;
    private String bio;

    public Users(String idUsers, String userName, String fullName, String password, String imageUrl, String bio) {
        this.idUsers = idUsers;
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public Users () {}

    public String getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(String idUsers) {
        this.idUsers = idUsers;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}