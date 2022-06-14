package com.example.stagram;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteWorkActivity extends AppCompatActivity {
    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;
    String galleryContractAddress;
    int tokenNum = 0;
    boolean check=false;

    String tokenNumber;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_deletework);

        //지갑주소와 개인키 정보 받아오기.
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");
        galleryContractAddress=bundle.getString("galleryContractAddress");

        Log.e("addActi에서 바로 받은 address : ",address);

        EditText tokenNumEditText = findViewById(R.id.tokenNumEditText);
        Button numSearchBT = findViewById(R.id.numSearchBT);
        Button deleteButton = findViewById(R.id.deleteButton);
        ImageView NFTImageView = findViewById(R.id.NFTImageView2);
        TextView tokenNumTextView = findViewById(R.id.tokenNumTextView);

        numSearchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 사용자의 지갑주소와 입력한 번호에 해당하는 NFT의 소유자 지갑주소가 일치하면 해당 번호를 갤러리에 추가
                ref.child("NormalPost").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()) {
                            PostingItem post = data.getValue(PostingItem.class);
                            if (post.getPostUser() != null)
                            {
                                Log.e("post에서 불러온 값은 : ",post.getPostUser());
                                Log.e("address에서 불러온 값은 : ",address);
                                //해당 NFT 소유자 주소가 나의 지갑 주소와 동일한 경우
                                if (address.equals(post.getPostUser())) {
                                    Log.e("post에서 불러온 token값은 : ",post.getUserDetail());
                                    Log.e("입력한 token 값은 : ",tokenNumEditText.getText().toString());
                                    //입력한 NFT 토큰번호가 내가 소유한 NFT 토큰번호와 일치하는 경우
                                    if (tokenNumEditText.getText().toString().equals(post.getUserDetail())) {
                                        String temp = tokenNumEditText.getText().toString();
                                        tokenNumber = temp;
                                        Log.e("tokenNumber 값은 : ",tokenNumber);

                                        try {
                                            //경로가 아닌 bitmap으로 image를 수정함.
                                            ImageFile imageFile = new ImageFile();
                                            Bitmap bitmap = imageFile.hexStringToBitmap(post.getPathImage());
                                            NFTImageView.setImageBitmap(bitmap);
                                            tokenNumTextView.setText("#"+post.getUserDetail());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                        Toast.makeText(DeleteWorkActivity.this, "자신이 소유한 NFT가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tokenNumber==null)
                {
                    Toast.makeText(DeleteWorkActivity.this, "삭제할 NFT를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference userRef = ref.child("Gallery").child(galleryContractAddress);
                    userRef.child(tokenNumber).removeValue();
                    Toast.makeText(DeleteWorkActivity.this, "갤러리에 NFT 삭제 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}


