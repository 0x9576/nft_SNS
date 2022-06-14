package com.example.stagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.PostViewHolder> {

    private ArrayList<PostingItem> dataList ;
    private Context adapterContext;

    MarketAdapter(){
    }

    MarketAdapter(ArrayList<PostingItem> data){
        this.dataList = data;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.market_item,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
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
        //holder.tokenNum.setText(dataList.get(position).getImgDetail());
        holder.postedTime.setText(dataList.get(position).getPostedDate());
        holder.isToken=dataList.get(position).getIsProduct();
        holder.title.setText(dataList.get(position).getImgDetail());
        if(dataList.get(position).getIsProduct().equals("True")){
            holder.price.setText(dataList.get(position).getPrice());
            holder.tokenPrice.setText(dataList.get(position).getTokenPrice());
        }
        else
        {
            holder.price.setText("");
            holder.tokenPrice.setText("");
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




    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView userName ;
        TextView userDetail ; //token_id
        TextView tokenNum ;
        TextView postedTime ;
        TextView title;
        TextView price;
        TextView tokenPrice;
        String isToken;
        String userAddress;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            userName = itemView.findViewById(R.id.nameSeller) ;
            userDetail = itemView.findViewById(R.id.tokenNum) ;
            //tokenNum = itemView.findViewById(R.id.tokenNum) ;
            postedTime = itemView.findViewById(R.id.timeSell) ;
            postImage = itemView.findViewById(R.id.img_product) ;
            price = itemView.findViewById(R.id.nftPrice);
            tokenPrice= itemView.findViewById(R.id.nfcPrice);
            title = itemView.findViewById(R.id.titleTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition(); //포지션 가져와서 아이템있으면 인텐트 활용해서 userDetail의 텍스트 전송
                    if(pos!=RecyclerView.NO_POSITION){
                        Intent emptyIntent = new Intent(view.getContext().getApplicationContext(), NftInfoActivity.class);
                        emptyIntent.putExtra("userDetail",userDetail.getText().toString());
                        //emptyIntent.putExtra("userName",userName.getText().toString());
                        emptyIntent.putExtra("userName",userAddress);
                        emptyIntent.putExtra("price",price.getText().toString());
                        emptyIntent.putExtra("isToken",isToken);
                        emptyIntent.putExtra("tokenPrice",tokenPrice.getText().toString());
                        emptyIntent.putExtra("title",title.getText().toString());
                        view.getContext().startActivity(emptyIntent);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(dataList.get(pos).getPostUser().equals(MainActivity.getMyAddress()))
                        {
                            PostingItem listTarget=dataList.get(pos);
                            String baseTarget = dataList.get(pos).getPostUser()+dataList.get(pos).getPostedDate();
                            MainActivity.getRef().child("NormalPost").child(baseTarget).setValue(null);
                            dataList.remove(listTarget);
                            Toast.makeText(itemView.getContext(), "정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(itemView.getContext(), "다른사람의 글은 지울 수 없어요...!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }

}
