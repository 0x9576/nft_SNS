package com.example.stagram;

import java.io.Serializable;

public class Comment implements Serializable, Comparable<Comment>{

    String user;
    String comment;
    String postedTime;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(String postedTime) {
        this.postedTime = postedTime;
    }

    @Override
    public int compareTo(Comment p) {
        //날짜 순 정렬
        if (p.getPostedTime().compareTo(this.getPostedTime()) > 0)
            return 1;
        return -1;
    }
}
