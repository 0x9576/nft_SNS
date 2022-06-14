package com.example.stagram;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FeedInfoActivity extends AppCompatActivity {
    String Link = "";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    ArrayList<Comment> commentList = new ArrayList<Comment>();
    CommentAdapter commentAdapter = new CommentAdapter(commentList);

    static DatabaseReference feedPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_info);
        Intent intent = getIntent();


        Blockchain b = new Blockchain();
        ImageFile imageFile = new ImageFile();
        String tokenID = intent.getExtras().getString("userDetail");
        String userId = intent.getExtras().getString("userName");
        String postedTime = intent.getExtras().getString("postedTime");
        String postDetail = intent.getExtras().getString("postDetail");

        RecyclerView commentView = findViewById(R.id.feedCommentRecyclerView);
        EditText commentEdit = findViewById(R.id.commentEditText);
        Button commentButton = findViewById(R.id.feedCommentButton);

        commentView.setLayoutManager(new LinearLayoutManager(this));
        commentView.setAdapter(commentAdapter);

        feedPath = ref.child("NormalPost").child(intent.getExtras().getString("userName")+intent.getExtras().getString("postedTime"));

        /**댓글 게시 버튼의 동작 정의*/
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentEdit.getText().toString().length()!=0){
                    long CurrTime = System.currentTimeMillis();
                    Comment currComment = new Comment();
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date mDate = new Date(CurrTime);
                    currComment.setComment(commentEdit.getText().toString());
                    currComment.setUser(MainActivity.address); /** 임시 방편*/
                    currComment.setPostedTime(mFormat.format(mDate));

                    commentEdit.setText(null);

                    DatabaseReference userRef = ref.child("NormalPost");
                    DatabaseReference commentRef = userRef.child(intent.getExtras().getString("userName")+intent.getExtras().getString("postedTime")).child("Comment");
                    commentRef.child(mFormat.format(mDate)+MainActivity.address).setValue(currComment);

                    Toast.makeText(FeedInfoActivity.this,"댓글 게시완료!",Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(FeedInfoActivity.this,"텍스트를 입력해주세요!",Toast.LENGTH_SHORT).show();
            }
        });


        String hexString = null;
        try {
            if (!tokenID.equals("NOT NFT")) {
                hexString = b.get_NFT_info(Integer.valueOf(tokenID));
                ImageView img = findViewById(R.id.NFT_image);
                Bitmap bitmap = imageFile.hexStringToBitmap(hexString);
                img.setImageBitmap(bitmap);
            }
            else {
                ref.child("NormalPost").child(userId+postedTime).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String postPath=snapshot.getValue(PostingItem.class).getPathImage();
                        ImageView img = findViewById(R.id.NFT_image);
                        Bitmap bitmap = imageFile.hexStringToBitmap(postPath);
                        img.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Link = "https://baobab.scope.klaytn.com/nft/";
        Link += b.contract_address + "/" + tokenID; //해당 주소가 그 NFT가 있는 주소가 된다.




        TextView userIdTextView = findViewById(R.id.feedUserTextView);
        userIdTextView.setText("게시자 : " + userId);

        TextView feedDetail = findViewById(R.id.feedDetailTextView);
        feedDetail.setText("세부내용 : " + postDetail);


        Button explorerButton = findViewById(R.id.explorerButton);
        if(getIntent().getExtras().getString("isToken").equals("False"))
            explorerButton.setVisibility(View.GONE);


        explorerButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(Link));
                startActivity(intentUrl);
            }
        });

        //좋아요버튼 생성 및 설정
        Button likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        commentList.clear();


        DatabaseReference userRef = ref.child("NormalPost");
        DatabaseReference commentRef = userRef.child(getIntent().getExtras().getString("userName")+getIntent().getExtras().getString("postedTime")).child("Comment");

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    Comment post=data.getValue(Comment.class);
                    commentList.add(post);
                }
                Collections.sort(commentList);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
