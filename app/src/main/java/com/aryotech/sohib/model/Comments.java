package com.aryotech.sohib.model;

public class Comments {

    private String isComments;
    private String commenters;

    public Comments(String isComments, String commenters) {
        this.isComments = isComments;
        this.commenters = commenters;
    }

    public Comments(){}

    public String getIsComments() {
        return isComments;
    }

    public void setIsComments(String myComment) {
        this.isComments = isComments;
    }

    public String getCommenters() {
        return commenters;
    }

    public void setCommenters(String commenters) {
        this.commenters = commenters;
    }
}
