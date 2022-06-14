package com.example.stagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private ArrayList<GalleryItem> dataList ;
    private Context adapterContext;

    GalleryAdapter(){
    }

    GalleryAdapter(ArrayList<GalleryItem> data){
        this.dataList = data;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_item,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        try {
            //경로가 아닌 bitmap으로 image를 수정함.
            ImageFile imageFile = new ImageFile();
            Bitmap bitmap = imageFile.hexStringToBitmap(dataList.get(position).getImg());
            holder.galleryImage.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.galleryName.setText(dataList.get(position).getGalleryName());
        holder.isOpen=dataList.get(position).isOpen();
        holder.isTicket=dataList.get(position).isCheck();
        holder.postedTime.setText(dataList.get(position).getPostedDate());
        holder.galleryContractAddress=dataList.get(position).getContractAddress();
        holder.userAddress=dataList.get(position).getUserAddress();
        //holder.isToken=dataList.get(position).getIsProduct();
        /*if(dataList.get(position).getIsProduct().equals("True"))
            holder.price.setText(dataList.get(position).getPrice());
        else
            holder.price.setText("");*/
    }

    @Override
    public int getItemCount() {

        return dataList.size();
    }
}

class GalleryViewHolder extends RecyclerView.ViewHolder {
    ImageView galleryImage;
    TextView galleryName ;
    TextView postedTime ;
    boolean isOpen;
    boolean isTicket;
    String galleryContractAddress;
    String userAddress;

    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    public GalleryViewHolder(View itemView) {
        super(itemView) ;
        // 뷰 객체에 대한 참조. (hold strong reference)
        galleryName = itemView.findViewById(R.id.galleryName) ;
        postedTime = itemView.findViewById(R.id.timePost) ;
        galleryImage = itemView.findViewById(R.id.galleryImage) ;
        //isTicket = itemView.findViewById(R.id.ticketAvailabilityTextView) ;
        //isOpen = itemView.findViewById(R.id.openAvailabilityTextView) ;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition(); //포지션 가져와서 아이템있으면 인텐트 활용해서 userDetail의 텍스트 전송
                if(pos!=RecyclerView.NO_POSITION){

                    ref.child("ticket").child(galleryContractAddress).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot data : snapshot.getChildren()) {
                                PostingItem post = data.getValue(PostingItem.class);
                                if(post.getPostUser().equals(LoginActivity.userAddress))
                                {
                                    Log.e("nft입장권잇음","클릭");
                                    Bundle bundle = new Bundle();
                                    Fragment fragmentParGallery = new FragmentParticularGallery();
                                    //bundle.putString("privateKey", privateKey);
                                    bundle.putString("address", userAddress);
                                    bundle.putString("galleryContractAddress",galleryContractAddress);
                                    FragmentTransaction transaction = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                                    fragmentParGallery.setArguments(bundle);
                                    transaction.replace(R.id.container,fragmentParGallery);
                                    transaction.commitAllowingStateLoss();
                                    //transaction.commit();   //이걸로하면 가끔 튕김
                                    return;
                                }
                            }
                            Log.e("nft입장권없음","클릭");

                            Intent emptyIntent = new Intent(view.getContext().getApplicationContext(), TicketBuyActivity.class);//이거 작성해야겠네...
                            emptyIntent.putExtra("galleryName",galleryName.getText().toString());
                            emptyIntent.putExtra("galleryContractAddress",galleryContractAddress);
                            emptyIntent.putExtra("nickname",FragmentGallery.tempNickname);
                            emptyIntent.putExtra("address", userAddress);
                            view.getContext().startActivity(emptyIntent);
                            //Toast.makeText(FragmentGroupGallery(), "갤러리에 NFT 추가 완료!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    /*Intent emptyIntent = new Intent(view.getContext().getApplicationContext(), GalleryInfoActivity.class);//이거 작성해야겠네...
                    //emptyIntent.putExtra("userDetail",userDetail.getText().toString());
                    emptyIntent.putExtra("galleryName",galleryName.getText().toString());
                    emptyIntent.putExtra("isTicket",isTicket);
                    emptyIntent.putExtra("isOpen",isOpen);
                    view.getContext().startActivity(emptyIntent);*/



                    /*Log.e("postingItem클릭","클릭");
                    Bundle bundle = new Bundle();
                    Fragment fragmentParGallery = new FragmentParticularGallery();
                    //bundle.putString("privateKey", privateKey);
                    bundle.putString("address", userAddress);
                    bundle.putString("galleryContractAddress",galleryContractAddress);
                    FragmentTransaction transaction = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    fragmentParGallery.setArguments(bundle);
                    transaction.replace(R.id.container,fragmentParGallery);
                    transaction.commit();*/
                }
            }
        });

    }
}