package com.example.stagram;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class PostingItem implements Serializable, Comparable<PostingItem>{

    private String postedDate;
    private String postUser; //현재 소유자 주소
    private String UserDetail; //토큰 번호
    private String ImgDetail;
    private String pathImage; //비트맵 문자열화
    private String isProduct="";
    private String isToken="";
    private String price; //KLAY가격
    private String tokenPrice;


    public PostingItem(){

    }

    public PostingItem(String postedDate,String postUser,String UserDetail, String ImgDetail){
        /*this.postedDate=postedDate;
        this.postUser=postUser;
        this.UserDetail=UserDetail;
        this.ImgDetail=ImgDetail;
        this.pathImage;
        */
    }


    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public String getUserDetail() {
        return UserDetail;
    }

    public void setUserDetail(String userDetail) {
        UserDetail = userDetail;
    }

    public String getImgDetail() {
        return ImgDetail;
    }

    public void setImgDetail(String imgDetail) {
        ImgDetail = imgDetail;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getIsProduct() {
        return isProduct;
    }

    public void setIsProduct(String product) {
        this.isProduct = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTokenPrice() {
        return tokenPrice;
    }

    public void setTokenPrice(String tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public String getIsToken() {
        return isToken;
    }

    public void setIsToken(String isToken) {
        this.isToken = isToken;
    }


    @Override
    public int compareTo(PostingItem p) {
        //날짜 순 정렬
        if (p.getPostedDate().compareTo(this.getPostedDate()) > 0)
            return 1;
        return -1;
    }
}
