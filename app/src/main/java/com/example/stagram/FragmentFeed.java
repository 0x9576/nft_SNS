package com.example.stagram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class FragmentFeed extends Fragment {

    ArrayList<PostingItem> ItemList = new ArrayList<PostingItem>();
    //PostAdapter postAdapter = new PostAdapter(ItemList);

    FeedAdapter feedAdapter = new FeedAdapter(ItemList);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    HashSet<String> followerSet = new HashSet<>();
    String privateKey;
    String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if(bundle != null) {
            privateKey = bundle.getString("privateKey");
            address = bundle.getString("address");
        }
        ref.child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerSet = new HashSet<>();
                followerSet.add(address);
                for(DataSnapshot data : snapshot.getChildren()){
                    FollowerItem followerItem=data.getValue(FollowerItem.class);
                    if (followerItem.getFollower().equals(address)){
                        followerSet.add(followerItem.getFollow());
                    }
                }
                Collections.sort(ItemList);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_feed,container,false);
        FloatingActionButton fab_post = layout.findViewById(R.id.fbt_Post);
        RecyclerView recyclerView = layout.findViewById(R.id.postRecyclerview);
        TextView info = layout.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(getContext(),tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mData = FirebaseDatabase.getInstance().getReference();//////////
                Intent postIntent = new Intent(getContext(),PostActivity.class);
                postIntent.putExtra("privateKey",privateKey);
                postIntent.putExtra("address",address);
                startActivity(postIntent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(feedAdapter);

        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();
        ItemList.clear();

        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setMessage("잠시만 기다려주세요.\n서버에서 데이터를 가져오는 중입니다."); // 메시지
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();

        ref.child("NormalPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    if (followerSet.contains(post.getPostUser())){
                        ItemList.add(post);
                    }
                }
                Collections.sort(ItemList);
                feedAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
