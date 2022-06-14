package com.example.stagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.web3j.crypto.CipherException;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GalleryInfoActivity extends AppCompatActivity {
    ArrayList<GalleryItem> ItemList = new ArrayList<GalleryItem>();
    GalleryAdapter galleryAdapter = new GalleryAdapter(ItemList);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();  //번들 받기. getArguments() 메소드로 받음.
        if(bundle != null) {
            privateKey = bundle.getString("privateKey");
            address = bundle.getString("address");
        }
    }
    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_pargallery,container,false);
        FloatingActionButton fab_post = layout.findViewById(R.id.fbt_Post);
        RecyclerView recyclerView = layout.findViewById(R.id.pgRecyclerView);

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mData = FirebaseDatabase.getInstance().getReference();//////////
                /*Intent postIntent = new Intent(getContext(),PostActivity.class);
                postIntent.putExtra("privateKey",privateKey);
                postIntent.putExtra("address",address);
                startActivity(postIntent);*/
            }
        });
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(galleryAdapter);

        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();
        ItemList.clear();


        String temp = "null";


        ref.child("Users").child("User1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    GalleryItem gallery = data.getValue(GalleryItem.class);
                    ItemList.add(gallery);
                }
                galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
