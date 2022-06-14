package com.example.stagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<PostingItem> dataList ;
    private Context adapterContext;

    PostAdapter(){
    }

    PostAdapter(ArrayList<PostingItem> data){
        this.dataList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_gallery_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //경로가 아닌 bitmap으로 image를 수정함.
            ImageFile imageFile = new ImageFile();
            Bitmap bitmap = imageFile.hexStringToBitmap(dataList.get(position).getPathImage());
            holder.postImage.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.userAddress=dataList.get(position).getPostUser();
        holder.userDetail.setText(dataList.get(position).getUserDetail());
        holder.postDetail.setText(dataList.get(position).getImgDetail());
        holder.postedTime.setText(dataList.get(position).getPostedDate());
        holder.isToken=dataList.get(position).getIsProduct();
        if(dataList.get(position).getIsProduct().equals("True"))
        {
            holder.price.setText(dataList.get(position).getPrice());
        }
        else
        {
            holder.price.setText("");
        }

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("member").child(holder.userAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member temp = snapshot.getValue(Member.class);
                if(temp==null)
                    holder.userName.setText(holder.userAddress);
                else
                    holder.userName.setText(temp.getNickname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

class ViewHolder extends RecyclerView.ViewHolder {
    ImageView postImage;
    TextView userName ;
    TextView userDetail ; //token_id
    TextView postDetail ;
    TextView postedTime ;
    TextView price;
    TextView tokenPrice;
    String isToken;
    String userAddress;

    public ViewHolder(View itemView) {
        super(itemView) ;
        // 뷰 객체에 대한 참조. (hold strong reference)
        userName = itemView.findViewById(R.id.galleryName) ;
        userDetail = itemView.findViewById(R.id.ticketAvailabilityTextView) ;
        postDetail = itemView.findViewById(R.id.openAvailabilityTextView) ;
        postedTime = itemView.findViewById(R.id.timePost) ;
        postImage = itemView.findViewById(R.id.galleryImage) ;
        price = itemView.findViewById(R.id.price);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition(); //포지션 가져와서 아이템있으면 인텐트 활용해서 userDetail의 텍스트 전송
                if(pos!=RecyclerView.NO_POSITION){
                    Intent emptyIntent = new Intent(view.getContext().getApplicationContext(), NftInfoActivity.class);
                    emptyIntent.putExtra("userDetail",userDetail.getText().toString());
                    emptyIntent.putExtra("userName",userAddress);
                    //emptyIntent.putExtra("userName",userName.getText().toString());
                    emptyIntent.putExtra("price",price.getText().toString());
                    emptyIntent.putExtra("isToken",isToken);
                    view.getContext().startActivity(emptyIntent);
                }
            }
        });

    }
}
