package com.example.stagram;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateGallery extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE =200;
    private ImageView galleryImageView;
    //PostingItem ItemList[];

    /** DB initialize */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String privateKey;
    String address;
    String nickname;
    int tokenNum = 0;
    boolean check=false;

    @Override
    protected void onCreate(Bundle savedInstances) {
        Blockchain b = new Blockchain();
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_creategallery);

        //지갑주소와 개인키 정보 받아오기.
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");
        nickname=bundle.getString("nickname");

        EditText galleryNameEditText = findViewById(R.id.galleryNameEditText);
        Button generateButton = findViewById(R.id.generateButton);
        //CheckBox ticketCheckBox = findViewById(R.id.ticketCheckBox);
        EditText ticketPriceEditText = findViewById(R.id.ticketEditText);

        galleryImageView = findViewById(R.id.galleryImageView);
        galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryLoadIntent = new Intent(Intent.ACTION_PICK);
                galleryLoadIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(galleryLoadIntent,GET_GALLERY_IMAGE);
            }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(CreateGallery.this);
                dlg.setMessage("잠시만 기다려주세요.\n갤러리 생성중입니다."); // 메시지
                AlertDialog alertDialog = dlg.create();
                alertDialog.show();


                Drawable getImage = galleryImageView.getDrawable();
                BitmapDrawable drawable = (BitmapDrawable) getImage;
                long CurrTime = System.currentTimeMillis();

                String path = null;
                if (getImage == null) {
                    alertDialog.dismiss();
                    Toast.makeText(CreateGallery.this, "이미지를 추가해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {


                    ImageFile imagefile = new ImageFile(); // 이미지 변환과 관련된 클래스
                    Blockchain b = new Blockchain(); //블록체인 클래스 불러오기

                    Bitmap bitmap = drawable.getBitmap();
                    String img = imagefile.bitmapToByteString(bitmap);

                    ObjectOutputStream postStream = null;
                    FileOutputStream postImg = null;
                    GalleryItem gallery = new GalleryItem();
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date mDate = new Date(CurrTime);
                    mFormat.format(mDate);

                    Bitmap bitmap_resized = imagefile.getResizeBitmap(bitmap);
                    String img_resized = imagefile.bitmapToByteString(bitmap_resized);

                    String contractAddress= null;
                    try {
                        contractAddress = createContract();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gallery.setUserAddress(address);
                    gallery.setContractAddress(contractAddress);
                    gallery.setGalleryName(galleryNameEditText.getText().toString());
                    gallery.setPostedDate(mFormat.format(mDate));
                    gallery.setImg(img); //Bitmap클래스 자체가 String으로 들어가 있음.
                    gallery.setTicketPrice(ticketPriceEditText.getText().toString());
                    gallery.setCheck(check);
                    gallery.setOpen(false);

                    //DatabaseReference userRef = ref.child("Users").child("User1");
                    DatabaseReference userRef = ref.child("Users").child(nickname);
                    userRef.child(gallery.getGalleryName()).setValue(gallery);

                    //해당 갤러리 입장권 자신에게 발행
                    PostingItem post = new PostingItem();
                    try {
                        tokenNum = b.mint_NFT(img_resized,b.private_key, address,contractAddress);
                        //로그인된 주소로 토큰이 발행됨.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    post.setPostUser(address);
                    post.setImgDetail("");
                    post.setUserDetail(String.valueOf(tokenNum));
                    post.setPostedDate(mFormat.format(mDate));
                    post.setPathImage(img); //Bitmap클래스 자체가 String으로 들어가 있음.
                    post.setIsProduct("False");

                    DatabaseReference userRef2 = ref.child("ticket").child(contractAddress);
                    userRef2.child(post.getPostUser()+post.getPostedDate()).setValue(post);
                    //위까지 갤러리 입장권 발행임


                    alertDialog.dismiss();
                    Toast.makeText(CreateGallery.this, "갤러리 생성 완료!", Toast.LENGTH_SHORT).show();
                    finish();

                    //}
                }
            }
        });

        /*ticketCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : process the click event.
                check=!check;
                Log.e("check클릭","check클릭");
            }
        }) ;*/

    }


    public String createContract() throws InterruptedException {
        Blockchain bc=new Blockchain();
        String contractAddress=bc.contract(bc.private_key,"CHAERO","CAH");
        return contractAddress;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            galleryImageView.setImageURI(selectedImageUri);
        }
    }

    public String getNickName(String userAddress){
        final String[] nickName = new String[1];
        ref.child("member").child(userAddress).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Member userMember = data.getValue(Member.class);
                    if(userAddress.equals(userMember.getAddress()))
                    {
                        nickName[0] =userMember.getNickname();
                        break;
                    }
                }
                //Toast.makeText(getActivity(),"해당 유저가 존재하지 않거나 개장한 갤러리가 없습니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return nickName[0];
    }
}
