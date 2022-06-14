package com.example.stagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class FragmentMarket extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    ArrayList<PostingItem> ItemList = new ArrayList<PostingItem>();
    HashMap<String, String> tokenImageMap = new HashMap<>();
    MarketAdapter marketAdapter = new MarketAdapter(ItemList);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_market,container,false);
        FloatingActionButton fab_sell = layout.findViewById(R.id.fbt_Sell);
        RecyclerView recyclerView = layout.findViewById(R.id.marketRecyclerView);
        EditText search = layout.findViewById(R.id.marketSearch);


        /** edittext 입력시 리스트의 결과를 바꿔주는 리스너*/
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = search.getText().toString();
                if (searchString.equals("")){
                    ref.child("SellPost").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ItemList.clear();
                            for(DataSnapshot data : snapshot.getChildren()){
                                PostingItem post=data.getValue(PostingItem.class);
                                assert post != null;
                                post.setPathImage(tokenImageMap.get(post.getUserDetail()));
                                ItemList.add(post);
                            }
                            Collections.sort(ItemList);
                            marketAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else {
                    ref.child("SellPost").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ItemList.clear();
                            for(DataSnapshot data : snapshot.getChildren()){
                                PostingItem post=data.getValue(PostingItem.class);
                                assert post != null;
                                if (post.getImgDetail().contains(searchString)){
                                    post.setPathImage(tokenImageMap.get(post.getUserDetail()));
                                    ItemList.add(post);
                                }
                            }
                            Collections.sort(ItemList);
                            marketAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TextView info = layout.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(getContext(),tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(marketAdapter);

        fab_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mData = FirebaseDatabase.getInstance().getReference();//////////
                Intent sellIntent = new Intent(getContext(),SellActivity.class);
                sellIntent.putExtra("privateKey",privateKey);
                sellIntent.putExtra("address",address);
                startActivity(sellIntent);
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ItemList.clear();

        ref.child("NormalPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    assert post != null;
                    tokenImageMap.put(post.getUserDetail(),post.getPathImage());
                }
                marketAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        ref.child("SellPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    post.setPathImage(tokenImageMap.get(post.getUserDetail()));
                    ItemList.add(post);
                }
                Collections.sort(ItemList);
                marketAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
