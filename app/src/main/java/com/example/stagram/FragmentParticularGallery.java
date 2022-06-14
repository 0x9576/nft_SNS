package com.example.stagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;

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
import java.util.List;

public class FragmentParticularGallery extends Fragment {
    ArrayList<PostingItem> ItemList = new ArrayList<PostingItem>();
    GalleryWorkAdapter galleryworkAdapter = new GalleryWorkAdapter(ItemList);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference ref2 = database.getReference();
    String privateKey;
    String address;
    String galleryContractAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if(bundle != null) {
            privateKey = bundle.getString("privateKey");
            address = bundle.getString("address");
            galleryContractAddress=bundle.getString("galleryContractAddress");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_pargallery,container,false);
        FloatingActionButton plus_fab_post = layout.findViewById(R.id.plus_fbt_Post);
        FloatingActionButton minus_fab_post = layout.findViewById(R.id.minus_fbt_Post);
        RecyclerView recyclerView = layout.findViewById(R.id.pgRecyclerView);

        //내 소유의 갤러리인 경우
        if(address.equals(LoginActivity.userAddress))
        {
            plus_fab_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mData = FirebaseDatabase.getInstance().getReference();//////////
                    Log.e("particular에서 address는 : ",address);
                    Intent postIntent = new Intent(getContext(),AddWorkActivity.class);
                    postIntent.putExtra("privateKey",privateKey);
                    postIntent.putExtra("address",address);
                    postIntent.putExtra("galleryContractAddress",galleryContractAddress);
                    Log.e("particular에서 address는2 : ",address);
                    startActivity(postIntent);
                }
            });

            minus_fab_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mData = FirebaseDatabase.getInstance().getReference();//////////
                    Intent postIntent = new Intent(getContext(),DeleteWorkActivity.class);
                    postIntent.putExtra("privateKey",privateKey);
                    postIntent.putExtra("address",address);
                    postIntent.putExtra("galleryContractAddress",galleryContractAddress);
                    startActivity(postIntent);
                }
            });
        }
        else //내 소유의 갤러리가 아닌 경우
        {
            plus_fab_post.setVisibility(View.INVISIBLE);
            minus_fab_post.setVisibility(View.INVISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(galleryworkAdapter);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ItemList.clear();
        String temp = "null";

        List<String> list=new ArrayList<String>();

        ref.child("Gallery").child(galleryContractAddress).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String num=data.getValue().toString();
                    list.add(num);
                }

                ref2.child("NormalPost").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        ItemList.clear();
                        for (DataSnapshot data2 : snapshot2.getChildren()) {
                            PostingItem post = data2.getValue(PostingItem.class);
                            Log.e("post 값 : ",post.getUserDetail());

                            for(String str:list){
                                if(str.equals(post.getUserDetail()))
                                {
                                    ItemList.add(post);
                                    Log.e("추가함 : ","추가");
                                    break;
                                }
                            }

                        }
                        galleryworkAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*ref.child("Gallery").child(galleryContractAddress).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String num=data.getValue().toString();
                    list.add(num);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref2.child("NormalPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                for (DataSnapshot data2 : snapshot2.getChildren()) {
                    PostingItem post = data2.getValue(PostingItem.class);
                    Log.e("post 값 : ",post.getUserDetail());

                    for(String str:list){
                        if(str.equals(post.getUserDetail()))
                        {
                            ItemList.add(post);
                            Log.e("추가함 : ","추가");
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


    }
}
