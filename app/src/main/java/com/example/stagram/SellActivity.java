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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

public class SellActivity extends AppCompatActivity {
    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    HashSet<String> hashSet = new HashSet<>();

    private final int GET_GALLERY_IMAGE =200;
    String privateKey;
    String address;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_sell);

        ref.child("SellPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    hashSet.add(post.getUserDetail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");

        EditText detail_SellActivity = findViewById(R.id.detail_SellActivity);
        EditText price_SellActivity = findViewById(R.id.price_SellActivity);
        EditText price_SellActivityToken = findViewById(R.id.price_SellActivityToken);
        EditText tokenIdEditText = findViewById(R.id.tokenIdEditText);
        Button bt_SellActivity = findViewById(R.id.bt_SellActivity);
        TextView info = findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(SellActivity.this,tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        bt_SellActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((price_SellActivity.getText().toString().equals("")) || (price_SellActivityToken.getText().toString().equals(""))||
                        (detail_SellActivity.getText().toString().equals(""))||(tokenIdEditText.getText().toString().equals("")))
                    Toast.makeText(SellActivity.this,"내용을 모두 올바르게 적어주세요.",Toast.LENGTH_SHORT).show();
                else {
                    try {
                        String tokenIdString = tokenIdEditText.getText().toString();
                        BigInteger tokenId = new BigInteger(tokenIdString);
                        long CurrTime = System.currentTimeMillis();
                        String path = null;

                        if(hashSet.contains(tokenIdString)) {
                            Toast.makeText(SellActivity.this, "동일 토큰 중복 게시는 불가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(!b.is_owner(tokenId,address)){
                            Toast.makeText(SellActivity.this,"본인 소유의 토큰만 판매가능합니다.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            try {
                                b.send_NFT(privateKey, b.address, Integer.parseInt(tokenIdString));
                                PostingItem post = new PostingItem();
                                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date mDate = new Date(CurrTime);
                                mFormat.format(mDate);

                                post.setPostUser(address);
                                post.setImgDetail(detail_SellActivity.getText().toString());
                                post.setUserDetail(tokenIdString);
                                post.setPostedDate(mFormat.format(mDate));
                                post.setIsProduct("True");
                                post.setPrice(price_SellActivity.getText().toString());
                                post.setTokenPrice(price_SellActivityToken.getText().toString());

                                DatabaseReference userRef = ref.child("SellPost");
                                userRef.child(post.getPostUser()+post.getPostedDate()).setValue(post);

                                Toast.makeText(SellActivity.this, "게시 완료!", Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
