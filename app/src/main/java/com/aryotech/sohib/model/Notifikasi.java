package com.aryotech.sohib.model;

public class Notifikasi {

    private String idUser;
    private String idPost;
    private String comments;
    private Boolean isPost;

    public Notifikasi(String idUser, String idPost, String comments, Boolean isPost) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.comments = comments;
        this.isPost = isPost;
    }

    public Notifikasi(){}

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getisPost() {
        return isPost;
    }

    public void setPost(Boolean isPost) {
        isPost = isPost;
    }
}
