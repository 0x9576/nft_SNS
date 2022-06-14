package com.example.stagram;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentGallery extends Fragment {

    private final int GET_GALLERY_IMAGE =200;

    ArrayList<GalleryItem> ItemList = new ArrayList<GalleryItem>();
    GalleryAdapter galleryAdapter = new GalleryAdapter(ItemList);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;

    private static final String TAG="GalleryActivity.";
    String contract_address = "0xd4f27d65ba5186763584ea9ae6457bb587cfb485";

    Fragment fragmentGroupGallery = new FragmentGroupGallery();

    public static String userAddress=LoginActivity.userAddress;
    static String tempNickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if(bundle!=null) {
            privateKey = bundle.getString("privateKey");
            address = bundle.getString("address");
        }
        Log.e("개인키 뭐로나오냐: ", privateKey);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_gallery, container, false);
        Button searchButton = layout.findViewById(R.id.searchButton);
        EditText galleryEditText = layout.findViewById(R.id.galleryEditText);

        TextView info = layout.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(getContext(),tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String galleryOwner = galleryEditText.getText().toString();
                Log.d("Owner: ", galleryOwner);
                tempNickname=galleryOwner;

                if(galleryOwner.equals(""))
                {
                    Toast.makeText(getActivity(),"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ref.child("Users").child(galleryOwner).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                userAddress=galleryOwner;
                                Log.e("실행: ", "실행되나?");
                                Bundle bundle = new Bundle();
                                bundle.putString("privateKey", privateKey);
                                bundle.putString("address", galleryOwner);
                                bundle.putString("nickname", galleryOwner);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                fragmentGroupGallery.setArguments(bundle);
                                transaction.replace(R.id.container,fragmentGroupGallery);
                                transaction.commitAllowingStateLoss(); //comit대신 이거 쓰면 안 튕김
                                return;
                            }
                            Toast.makeText(getActivity(),"해당 유저가 존재하지 않거나 개장한 갤러리가 없습니다.",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        ImageView createContractButton = (ImageView) layout.findViewById(R.id.createContractButton);
        createContractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("member").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nick=null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Member member = snapshot.getValue(Member.class);
                            if(address.equals(member.getAddress()))
                            {
                                nick =member.getNickname();
                                Log.e("함수내 nickname: ",member.getNickname());
                                break;
                            }
                        }
                        Log.e("클릭","클릭");
                        Intent postIntent = new Intent(getContext(),CreateGallery.class);
                        postIntent.putExtra("privateKey",privateKey);
                        postIntent.putExtra("address",address);
                        postIntent.putExtra("nickname",nick);

                        startActivity(postIntent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {    }
                });

                /*Intent postIntent = new Intent(getContext(),CreateGallery.class);
                postIntent.putExtra("privateKey",privateKey);
                postIntent.putExtra("address",address);

                startActivity(postIntent);*/
                //createContract();
            }
        });

        ImageView myGalleryButton = (ImageView) layout.findViewById(R.id.myGalleryButton);
        myGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabase.child("users").child(userId).child("username").setValue(name);

                ref.child("member").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nick=null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Member member = snapshot.getValue(Member.class);
                            if(address.equals(member.getAddress()))
                            {
                                nick =member.getNickname();
                                tempNickname=nick;
                                Log.e("함수내 nickname: ",member.getNickname());
                                break;
                            }
                        }
                        Log.e("myGallery클릭","클릭");
                        Bundle bundle = new Bundle();
                        bundle.putString("privateKey", privateKey);
                        bundle.putString("address", address);
                        bundle.putString("nickname",nick);
                        Log.e("nickname은: ", nick);
                        //bundle.putString("address", "User1");
                        //bundle.putString("address", LoginActivity.userAddress);  //이거로 수정해야함(닉네임 받도록)
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        fragmentGroupGallery.setArguments(bundle);
                        transaction.replace(R.id.container,fragmentGroupGallery);
                        transaction.commit();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {    }
                });

            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        String temp = "null";

    }

    public void confirm(String contractAddress, String userAddress) throws InterruptedException {
        Blockchain bc=new Blockchain();
        if(bc.search_ticket(contractAddress,userAddress))
        {
            Log.d(TAG, "EnterGallery:success");
            //Toast toast=Toast.makeText(Gallery.this,"입장 성공.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.d(TAG, "EnterGallery:fail");
            //Toast.makeText(Gallery.this,"입장 실패.",Toast.LENGTH_SHORT).show();
        }
    }

    public void createContract()
    {
        Blockchain bc=new Blockchain();
        try {
            bc.contract(privateKey,"CHAERO","CAH");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getNickName(String user_address){
        final String[] nick = new String[1];
        ref.child("member").child(user_address).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    if(user_address.equals(member.getAddress()))
                    {
                        nick[0] =member.getNickname();
                        Log.e("함수내 nickname: ",member.getNickname());
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }});
        Log.e("함수진전 nickname: ",nick[0]);
        return nick[0];
    }

}
