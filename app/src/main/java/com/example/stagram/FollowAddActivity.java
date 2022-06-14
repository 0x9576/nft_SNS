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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.klaytn.caver.Caver;
import com.klaytn.caver.wallet.keyring.SingleKeyring;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FollowAddActivity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE =200;
    private ImageView Img_PostActivity;
    //PostingItem ItemList[];

    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    HashMap<String, String> nicknameAddress = new HashMap<>();
    String privateKey;
    String address;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_add_follow);

        ref.child("member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    nicknameAddress.put(member.getNickname(), member.getAddress());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }});

        //지갑주소와 개인키 정보 받아오기.
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");

        EditText addressEditText = findViewById(R.id.addressEditText);
        Button bt = findViewById(R.id.bt_FollowerActivity);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String follow = addressEditText.getText().toString();
                String addressString = nicknameAddress.get(follow);

                if (addressString != null){
                    follow = addressString;
                }
                Caver caver = new Caver(b.BAO_API);
                boolean existed = true;
                try {
                    caver.wallet.isExisted(follow);
                }catch (Exception e){
                    existed = false;
                }
                if (existed){
                    FollowerItem followerItem = new FollowerItem();
                    followerItem.setFollow(follow);
                    followerItem.setFollower(address);

                    DatabaseReference userRef = ref.child("follower");
                    userRef.child(follow+address).setValue(followerItem);
                    finish();
                }
                else{
                    Toast.makeText(FollowAddActivity.this,"유효하지 않은 주소입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
