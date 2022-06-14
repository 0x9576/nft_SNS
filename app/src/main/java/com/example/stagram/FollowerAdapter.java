package com.example.stagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FollowerAdapter extends RecyclerView.Adapter<ViewHolderFollower>{
    private ArrayList<FollowerItem> dataList ;
    private Context adapterContext;
    private String address;
    public HashMap<String, String> addressKeyMap = new HashMap<>();
    public HashMap<String, String> addressNickname = new HashMap<>();
    public HashMap<String, String> nicknameAddress = new HashMap<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    FollowerAdapter(){
    }

    FollowerAdapter(ArrayList<FollowerItem> data){
        this.dataList = data;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    @NonNull
    @Override
    public ViewHolderFollower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.follower_item,parent,false);
        return new ViewHolderFollower(view, address, addressKeyMap);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFollower holder, int position) {

        int num = position;
        ref.child("member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);
                    addressNickname.put(member.getAddress(), member.getNickname());
                    nicknameAddress.put(member.getNickname(), member.getAddress());

                    String follow = dataList.get(num).getFollow();
                    String nickname = addressNickname.get(follow);

                    if (nickname == null) {
                        nickname = follow;
                    }

                    holder.follow.setText(nickname);

                    if (member.getNickname() != null) {
                        if (member.getAddress().equals(follow)) {
                            ImageFile imageFile = new ImageFile();
                            Bitmap bitmap = imageFile.hexStringToBitmap(member.getImg());
                            holder.followerImg.setImageBitmap(bitmap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }});
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

class ViewHolderFollower extends RecyclerView.ViewHolder {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    TextView follow;
    ImageView followerImg;

    public String address;
    public HashMap<String, String> addressKeyMap = new HashMap<>();
    public HashMap<String, String> addressNickname = new HashMap<>();
    public HashMap<String, String> nicknameAddress = new HashMap<>();


    public ViewHolderFollower(View itemView, String address, HashMap<String, String> map) {
        super(itemView);

        ref.child("member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    addressNickname.put(member.getAddress(), member.getNickname());
                    nicknameAddress.put(member.getNickname(),member.getAddress());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }});

        addressKeyMap = map;
        this.address = address;
        // 뷰 객체에 대한 참조. (hold strong reference)
        follow = itemView.findViewById(R.id.follow_name);
        followerImg = itemView.findViewById(R.id.followUserImg);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String follow_string = follow.getText().toString();
                String follow_address = nicknameAddress.get(follow_string);

                if (follow_address == null){
                    follow_address = follow_string;
                }
                int pos = getAdapterPosition(); //포지션 가져와서 아이템있으면 인텐트 활용해서 userDetail의 텍스트 전송
                if(pos!=RecyclerView.NO_POSITION){
                    String finalFollow_address = follow_address;
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("팔로잉 삭제")
                            .setMessage("대상을 언팔로우 하시겠습니까?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    delete_db(address+ finalFollow_address,"follower");
                                }})
                            .setNegativeButton("취소", null).show();
                }
            }
        });
    }

    private void delete_db(String target, String dbName){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        System.out.print(addressKeyMap);
        if (addressKeyMap.get(target) != null){
            database.getReference().child(dbName).child(addressKeyMap.get(target)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
}

