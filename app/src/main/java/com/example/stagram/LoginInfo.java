package com.example.stagram;

import android.app.Application;

//로그인의 정보를 저장하는 클래스임. 액티비티 간 상관없이 사용가능.
//String privateKey = ( (LoginInfo) getApplication() ).getPrivateKey();
public class LoginInfo extends Application {
    private String privateKey;
    private String address;
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey( String privateKey ) {
        this.privateKey = privateKey;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress( String address ) {
        this.address = address;
    }
}
