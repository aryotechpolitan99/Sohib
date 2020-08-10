package com.aryotech.sohib.Model;

public class Status {

    String Username;
    String Description;
    Double CountLike;
    int ImageUser;
    int ImageStatus;

    public Status(){}

    public Status(String username, String description, Double countLike, int imageUser, int imageStatus) {
        Username = username;
        Description = description;
        CountLike = countLike;
        ImageUser = imageUser;
        ImageStatus = imageStatus;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Double getCountLike() {
        return CountLike;
    }

    public void setCountLike(Double countLike) {
        CountLike = countLike;
    }

    public int getImageUser() {
        return ImageUser;
    }

    public void setImageUser(int imageUser) {
        ImageUser = imageUser;
    }

    public int getImageStatus() {
        return ImageStatus;
    }

    public void setImageStatus(int imageStatus) {
        ImageStatus = imageStatus;
    }
}
