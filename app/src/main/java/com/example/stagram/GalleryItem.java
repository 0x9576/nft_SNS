package com.example.stagram;


import android.graphics.Bitmap;

import java.io.Serializable;

public class GalleryItem {
    private String userAddress;
    private String contractAddress;
    private String galleryName;
    private String img;
    private String postedDate;
    private String ticketPrice;
    private boolean check;
    private boolean open;

    public GalleryItem(){

    };
    public GalleryItem(String userAddress, String contractAddress,String galleryName, String img,
                       String postedDate, String price, boolean check, boolean open){
        this.userAddress=userAddress;
        this.contractAddress=contractAddress;
        this.galleryName=galleryName;
        this.img=img;
        this.postedDate=postedDate;
        this.ticketPrice=price;
        this.check=check;
        this.open=open;
    };

    public String getUserAddress() {
        return userAddress;
    }
    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getGalleryName() {
        return galleryName;
    }
    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public String getPostedDate() {
        return postedDate;
    }
    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }
    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public boolean isCheck() {
        return check;
    }
    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isOpen() {
        return open;
    }
    public void setOpen(boolean open) {
        this.open = open;
    }
}
