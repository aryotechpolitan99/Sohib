package com.aryotech.sohib.model;

public class Comments {

    private String myComment;
    private String commenters;

    public Comments(String myComment, String commenters) {
        this.myComment = myComment;
        this.commenters = commenters;
    }

    public Comments(){}

    public String getMyComment() {
        return myComment;
    }

    public void setMyComment(String myComment) {
        this.myComment = myComment;
    }

    public String getCommenters() {
        return commenters;
    }

    public void setCommenters(String commenters) {
        this.commenters = commenters;
    }
}
