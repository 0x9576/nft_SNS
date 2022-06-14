package com.example.stagram;

public class Member {
    private String img;
    private String address;
    private String nickname;

    public Member(){}
    public Member(String img, String address, String nickname)
    {
        this.img = img;
        this.address = address;
        this.nickname = nickname;
    }

    public String getImg() {
        return img;
    }

    public String getAddress() {
        return address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setImg(String img) { this.img = img; }

    public void setAddress(String address){
        this.address = address;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
