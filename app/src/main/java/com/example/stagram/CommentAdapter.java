package com.example.stagram;

import android.content.Context;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<Comment> commentList ;
    private Context adapterContext;

    CommentAdapter(){
    }

    CommentAdapter(ArrayList<Comment> data){
        this.commentList = data;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String userAddress=commentList.get(position).getUser();
        holder.userName.setText(commentList.get(position).getUser());
        holder.comment.setText(commentList.get(position).getComment());
        holder.postedTime.setText(commentList.get(position).getPostedTime());

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("member").child(userAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member temp = snapshot.getValue(Member.class);
                if(temp==null)
                    holder.userName.setText(userAddress);
                else
                    holder.userName.setText(temp.getNickname());
                try {
                    //경로가 아닌 bitmap으로 image를 수정함.
                    ImageFile imageFile = new ImageFile();
                    Bitmap bitmap = imageFile.hexStringToBitmap(temp.getImg());
                    holder.userImg.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName ;
        TextView comment;
        TextView postedTime ;
        ImageView userImg;

        public CommentViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            userName = itemView.findViewById(R.id.commentUser) ;
            postedTime = itemView.findViewById(R.id.postedTime) ;
            comment = itemView.findViewById(R.id.comment);
            userImg = itemView.findViewById(R.id.commentUserImg);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(commentList.get(pos).getUser().equals(MainActivity.getMyAddress()))
                        {
                            //Comment listTarget=commentList.get(pos);
                            String baseTarget = commentList.get(pos).getPostedTime()+commentList.get(pos).getUser();
                            FeedInfoActivity.feedPath.child("Comment").child(baseTarget).setValue(null);
                            //commentList.remove(listTarget);
                            Toast.makeText(itemView.getContext(), "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            //notifyDataSetChanged();
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
