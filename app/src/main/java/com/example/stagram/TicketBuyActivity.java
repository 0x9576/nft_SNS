package com.example.stagram;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketBuyActivity extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE =200;
    private ImageView nftImageView;
    //PostingItem ItemList[];

    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;
    String galleryContractAddress;
    String nickname;
    int tokenNum = 0;
    boolean check=false;

    String tokenNumber;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_buyticket);

        //지갑주소와 개인키 정보 받아오기.
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");
        galleryContractAddress=bundle.getString("galleryContractAddress");
        nickname=bundle.getString("nickname");

        //Log.e("addActi에서 바로 받은 address : ",address);

        TextView gallNameTextView = findViewById(R.id.gallNameTextView);
        TextView ticketPriceTextView = findViewById(R.id.ticketPriceTextView);
        TextView possessKlaytextView = findViewById(R.id.possessKlaytextView);
        Button ticketBuyButton = findViewById(R.id.deleteButton);
        ImageView gallImageView = findViewById(R.id.gallImageView);

        ref.child("Users").child(nickname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    GalleryItem gallery = data.getValue(GalleryItem.class);
                    if(gallery.getContractAddress().equals(galleryContractAddress))
                    {
                        gallNameTextView.setText(gallery.getGalleryName());
                        ticketPriceTextView.setText(gallery.getTicketPrice()+"(KLAY)");
                        try {
                            //경로가 아닌 bitmap으로 image를 수정함.
                            ImageFile imageFile = new ImageFile();
                            bitmap = imageFile.hexStringToBitmap(gallery.getImg());
                            gallImageView.setImageBitmap(bitmap);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Blockchain blockchain = new Blockchain();
                        try {
                            possessKlaytextView.setText(blockchain.getBalance(LoginActivity.userAddress)+"(KLAY)");
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ticketBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //잔고가 부족하면 전송 안된다.
                String price=ticketPriceTextView.getText().toString();
                String memo = gallNameTextView.getText().toString() + " NFT입장권 수신자: " + address + " 가격 : " + price +"KLAY";
                Log.e("privateKey = ",LoginActivity.userPrivateKey);
                Log.e("destination address = ",address);
                Log.e("memo = ",memo);
                Log.e("price = ",price);

                try {
                    b.send_KLAY(LoginActivity.userPrivateKey, address,memo, price);
                    Log.e("Klay전송 성공 ","입니다");
                    //해당 갤러리 입장권 자신에게 발행
                } catch (Exception e) {
                    Toast.makeText(TicketBuyActivity.this, "금액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ImageFile imagefile = new ImageFile();
                    String img = imagefile.bitmapToByteString(bitmap);
                    Bitmap bitmap_resized = imagefile.getResizeBitmap(bitmap);
                    String img_resized = imagefile.bitmapToByteString(bitmap_resized);

                    //입장권 티켓 발행
                    tokenNum = b.mint_NFT(img_resized,b.private_key, LoginActivity.userAddress,galleryContractAddress);

                    long CurrTime = System.currentTimeMillis();
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date mDate = new Date(CurrTime);
                    mFormat.format(mDate);

                    PostingItem post = new PostingItem();
                    post.setPostUser(LoginActivity.userAddress);
                    post.setImgDetail("");
                    post.setUserDetail(String.valueOf(tokenNum));
                    post.setPostedDate(mFormat.format(mDate));
                    post.setPathImage(img); //Bitmap클래스 자체가 String으로 들어가 있음.
                    post.setIsProduct("False");

                    DatabaseReference userRef2 = ref.child("ticket").child(galleryContractAddress);
                    userRef2.child(post.getPostUser()+post.getPostedDate()).setValue(post);
                    Toast.makeText(TicketBuyActivity.this, "입장권 티켓 구매 성공", Toast.LENGTH_SHORT).show();
                    finish();
                    //위까지 갤러리 입장권 발행임
                    //로그인된 주소로 토큰이 발행됨.

                } catch (InterruptedException e) {
                    Toast.makeText(TicketBuyActivity.this, "입장권 티켓 생성 실패", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
