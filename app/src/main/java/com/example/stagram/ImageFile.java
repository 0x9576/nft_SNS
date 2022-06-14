package com.example.stagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.ipfs.multibase.binary.Base64;


public class ImageFile {
    int targetSize = 56*1024; //사진 파일이 120KB를 넘으면 안된다. 바이트단위
    public Bitmap getResizeBitmap(String file_route){
        Bitmap rawBitmap = BitmapFactory.decodeFile(file_route); //원래의 비트맵
        int rawSize = rawBitmap.getByteCount(); //원래 파일의 사이즈
        if (targetSize < rawSize){
            double mag = Math.sqrt((double) rawSize / (double) targetSize);
            //크기가 10배 줄어들면 용량은 100배 줄어들기 때문에 루트를 씌워야 함.
            int size = (int) Math.ceil(mag); //사이즈조정
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = size;
            Bitmap bitmap = BitmapFactory.decodeFile(file_route, options);
            return bitmap;
        }
        return rawBitmap; //사이즈 조정이 필요없는 경우 그대로를 반환
    }

    public Bitmap getResizeBitmap(Bitmap bitmap){
        int rawSize = bitmap.getByteCount(); //원래 파일의 사이즈
        if (targetSize < rawSize){
            double mag = Math.sqrt((double) rawSize / (double) targetSize);
            //크기가 10배 줄어들면 용량은 100배 줄어들기 때문에 루트를 씌워야 함.
            int size = (int) Math.ceil(mag); //사이즈조정

            int nw = (int) (bitmap.getWidth() / size);
            int nh = (int) (bitmap.getHeight() / size);
            return Bitmap.createScaledBitmap(bitmap,nw, nh, true);
        }
        return bitmap; //사이즈 조정이 필요없는 경우 그대로를 반환
    }

    public Bitmap hexStringToBitmap(String hex){
        byte [] byteArr = Base64StringToByteArray(hex);
        return BitmapFactory.decodeByteArray(byteArr,0, byteArr.length);
    }

    public String bitmapToByteString( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArrayToBase64String(byteArray);
    }

    //private method..
    private String byteArrayToBase64String(byte[] bytes) {
//        StringBuilder builder = new StringBuilder();
//        for (byte data : bytes) {
//            builder.append(String.format("%02X ", data)); }
//        System.out.println("code: "+builder.toString());
//        return builder.toString();
        return new String(Base64.encodeBase64(bytes));
    }


    private  byte[] Base64StringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 2)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
        return Base64.decodeBase64(s);
    }
}
