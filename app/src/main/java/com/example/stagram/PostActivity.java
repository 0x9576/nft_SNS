package com.example.stagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE =200;
    private ImageView Img_PostActivity;
    //PostingItem ItemList[];

    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;
    int tokenNum = 0;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activitiy_post);

        //지갑주소와 개인키 정보 받아오기.
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");

        EditText detail_PostActivity = findViewById(R.id.detail_PostActivity);
        Button bt_PostActivity = findViewById(R.id.bt_PostActivity);
        ToggleButton tg_Tokenization = findViewById(R.id.tokenToggle);

        Img_PostActivity = findViewById(R.id.img_PostActivity);
        Img_PostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryLoadIntent = new Intent(Intent.ACTION_PICK);
                galleryLoadIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(galleryLoadIntent,GET_GALLERY_IMAGE);
            }
        });

        tg_Tokenization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((ToggleButton)view).isChecked())
                    Toast.makeText(PostActivity.this,"게시글이 토큰화됩니다.",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PostActivity.this,"게시글이 토큰화되지 않습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        bt_PostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable getImage = Img_PostActivity.getDrawable();
                BitmapDrawable drawable = (BitmapDrawable) getImage;
                long CurrTime = System.currentTimeMillis();

                String path = null;
                if(getImage==null)
                    Toast.makeText(PostActivity.this,"이미지를 추가해주세요!",Toast.LENGTH_SHORT).show();
                else {
                    ImageFile imagefile = new ImageFile(); // 이미지 변환과 관련된 클래스
                    Blockchain b = new Blockchain(); //블록체인 클래스 불러오기

                    Bitmap bitmap = drawable.getBitmap();
                    String img = imagefile.bitmapToByteString(bitmap);

                    if(tg_Tokenization.isChecked()){
                        ObjectOutputStream postStream = null;
                        FileOutputStream postImg = null;
                        PostingItem post = new PostingItem();
                        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date mDate = new Date(CurrTime);
                        mFormat.format(mDate);

                        Bitmap bitmap_resized = imagefile.getResizeBitmap(bitmap);
                        String img_resized = imagefile.bitmapToByteString(bitmap_resized);
                        try {
                            tokenNum = b.mint_NFT(img_resized,b.private_key, address,b.contract_address);
                            //로그인된 주소로 토큰이 발행됨.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        post.setPostUser(address);
                        post.setImgDetail(detail_PostActivity.getText().toString());
                        post.setUserDetail(String.valueOf(tokenNum));
                        post.setPostedDate(mFormat.format(mDate));
                        post.setPathImage(img); //Bitmap클래스 자체가 String으로 들어가 있음.
                        post.setIsProduct("False");
                        post.setIsToken("True");
                        DatabaseReference userRef = ref.child("NormalPost");
                        userRef.child(post.getPostUser()+post.getPostedDate()).setValue(post);
                        Toast.makeText(PostActivity.this,"NFT 게시 완료!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        PostingItem post = new PostingItem();
                        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date mDate = new Date(CurrTime);
                        mFormat.format(mDate);

                        //Bitmap bitmap = imagefile.getResizeBitmap(path);
                        //String img = imagefile.bitmapToByteString(bitmap);


                        post.setPostUser(address);
                        post.setImgDetail(detail_PostActivity.getText().toString());
                        post.setUserDetail("NOT NFT");
                        post.setPostedDate(mFormat.format(mDate));
                        post.setPathImage(img); //Bitmap클래스 자체가 String으로 들어가 있음.
                        post.setIsProduct("False");
                        post.setIsToken("False");

                        DatabaseReference userRef = ref.child("NormalPost");
                        userRef.child(post.getPostUser()+post.getPostedDate()).setValue(post);
                        Toast.makeText(PostActivity.this,"게시 완료!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            Img_PostActivity.setImageURI(selectedImageUri);
        }
    }
}
