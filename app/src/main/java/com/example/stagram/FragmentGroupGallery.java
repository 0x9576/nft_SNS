package com.example.stagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class FragmentGroupGallery extends Fragment {
    ArrayList<GalleryItem> ItemList = new ArrayList<GalleryItem>();
    GalleryAdapter galleryAdapter = new GalleryAdapter(ItemList);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;
    String nickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if(bundle != null) {
            privateKey = bundle.getString("privateKey");
            address = bundle.getString("address");
            nickname=bundle.getString("nickname");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_subgallery,container,false);
        RecyclerView recyclerView = layout.findViewById(R.id.galleryRecyclerView);

        TextView info = layout.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(getContext(),tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(galleryAdapter);

        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();
        ItemList.clear();


        String temp = "null";


        ref.child("Users").child(nickname).addValueEventListener(new ValueEventListener() {
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