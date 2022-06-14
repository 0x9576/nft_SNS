package com.example.stagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private ArrayList<PostingItem> feedList ;
    private Context adapterContext;

    FeedAdapter(){
    }

    FeedAdapter(ArrayList<PostingItem> data){
        this.feedList = data;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_item,parent,false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        try {
            //경로가 아닌 bitmap으로 image를 수정함.
            ImageFile imageFile = new ImageFile();
            Bitmap bitmap = imageFile.hexStringToBitmap(feedList.get(position).getPathImage());
            holder.postImage.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
        //holder.userName.setText(feedList.get(position).getPostUser());
        holder.userDetail.setText(feedList.get(position).getUserDetail());
        holder.postDetail.setText(feedList.get(position).getImgDetail());
        holder.postedTime.setText(feedList.get(position).getPostedDate());
        holder.isToken=feedList.get(position).getIsToken();
        holder.user = feedList.get(position).getPostUser();

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("member").child(holder.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member temp = snapshot.getValue(Member.class);
                if(temp==null)
                    holder.userName.setText(holder.user);
                else
                    holder.userName.setText(temp.getNickname());
                try {
                    //경로가 아닌 bitmap으로 image를 수정함.
                    ImageFile imageFile = new ImageFile();
                    Bitmap bitmap = imageFile.hexStringToBitmap(temp.getImg());
                    holder.postUserImg.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Drawable userImg = feedList.get(position).getPostUser().;
        //holder.postUserImg.setImageDrawable();

        if(feedList.get(position).getIsProduct().equals("True")){
            holder.price.setText(feedList.get(position).getPrice());
            //holder..setText(feedList.get(position).getPrice());
        }
        else
            holder.price.setText("");

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }


    class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        ImageView postUserImg;
        TextView userName ;
        TextView userDetail ; //token_id
        TextView postDetail ;
        TextView postedTime ;
        TextView price ;
        TextView tokenPrice;
        String isToken;
        String user;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            userName = itemView.findViewById(R.id.namePoster) ;
            userDetail = itemView.findViewById(R.id.userDetail) ;
            postDetail = itemView.findViewById(R.id.detailPost) ;
            postedTime = itemView.findViewById(R.id.timePost) ;
            postImage = itemView.findViewById(R.id.img_posted) ;
            postUserImg = itemView.findViewById(R.id.postUserImg);
            price = itemView.findViewById(R.id.price);
            //tokenPrice= itemView.findViewById(R.id.nfcPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition(); //포지션 가져와서 아이템있으면 인텐트 활용해서 userDetail의 텍스트 전송
                    if(pos!=RecyclerView.NO_POSITION){
                        String img = (new ImageFile()).bitmapToByteString(((BitmapDrawable)postImage.getDrawable()).getBitmap());
                        Intent emptyIntent = new Intent(view.getContext().getApplicationContext(), FeedInfoActivity.class);
                        emptyIntent.putExtra("userDetail",userDetail.getText().toString());
                        emptyIntent.putExtra("userName",user);
                        emptyIntent.putExtra("postedTime",postedTime.getText().toString());
                        emptyIntent.putExtra("isToken",isToken);
                        emptyIntent.putExtra("postDetail",postDetail.getText().toString());
                        view.getContext().startActivity(emptyIntent);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(feedList.get(pos).getPostUser().equals(MainActivity.getMyAddress()))
                        {
                            PostingItem listTarget=feedList.get(pos);
                            String baseTarget = feedList.get(pos).getPostUser()+feedList.get(pos).getPostedDate();
                            MainActivity.getRef().child("NormalPost").child(baseTarget).setValue(null);
                            feedList.remove(listTarget);
                            Toast.makeText(itemView.getContext(), "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            //feedList.clear();
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
