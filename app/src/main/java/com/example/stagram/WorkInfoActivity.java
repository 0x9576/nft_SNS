package com.example.stagram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class WorkInfoActivity extends AppCompatActivity {
    String Link = "";
    BigInteger KLAY_MAX = new BigInteger("10000000000");
    BigInteger NFC_MAX = new BigInteger("9000000000000");
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    HashMap<String, String> keyTokenNumSell = new HashMap<>();
    HashMap<String, String> keyTokenNumNormal = new HashMap<>();
    HashMap<String, PostingItem> tokenNumPostingItemMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_info);
        Intent intent = getIntent();

        Blockchain b = new Blockchain();
        ImageFile imageFile = new ImageFile();
        String time = intent.getExtras().getString("time");
        String tokenID = intent.getExtras().getString("userDetail");
        String price = intent.getExtras().getString("price");
        String userId = intent.getExtras().getString("userName");
        String priceToToken = intent.getExtras().getString("tokenPrice");

        ImageView img = findViewById(R.id.workImageView);

        ref.child("NormalPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post = data.getValue(PostingItem.class);
                    if (tokenID.equals(post.getUserDetail())) {
                        try {
                            //경로가 아닌 bitmap으로 image를 수정함.
                            ImageFile imageFile = new ImageFile();
                            Bitmap bitmap = imageFile.hexStringToBitmap(post.getPathImage());
                            img.setImageBitmap(bitmap);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}