package com.example.stagram;

public class FollowerItem {
    String follower;
    String follow;

    public FollowerItem(){}
    public FollowerItem(String follower, String follow)
    {
        this.follow = follow;
        this.follower = follower;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollower(String follower) { this.follower = follower; }
}
