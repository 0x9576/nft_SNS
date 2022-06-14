package com.example.stagram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.web3j.crypto.CipherException;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class NftInfoActivity extends AppCompatActivity {
    String Link = "";
    BigInteger KLAY_MAX = new BigInteger("10000000000");
    BigInteger NFC_MAX = new BigInteger("9000000000000");
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    HashMap<String, String> keyTokenNumSell = new HashMap<>();
    HashMap<String, String> keyTokenNumNormal = new HashMap<>();
    HashMap<String, PostingItem> tokenNumPostingItemMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
//        BigDecimal balance = NFC_MAX;
//        BigDecimal div = balance.divide(KLAY_MAX).divide(new BigDecimal(2)).setScale(2, BigDecimal.ROUND_FLOOR);
//        BigDecimal a = div.multiply(new BigDecimal("1"));
//        Toast.makeText(NftInfoActivity.this, a.toString(), Toast.LENGTH_SHORT).show();

        ref.child("SellPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    keyTokenNumSell.put(post.getUserDetail(), data.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("NormalPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    PostingItem post=data.getValue(PostingItem.class);
                    keyTokenNumNormal.put(post.getUserDetail(), data.getKey());
                    tokenNumPostingItemMap.put(post.getUserDetail(), post);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nft_info);
        Intent intent = getIntent();

        Blockchain b = new Blockchain();
        ImageFile imageFile = new ImageFile();
        String time = intent.getExtras().getString("time");
        String tokenID = intent.getExtras().getString("userDetail");
        String price = intent.getExtras().getString("price");
        String userId = intent.getExtras().getString("userName");
        String priceToToken = intent.getExtras().getString("tokenPrice");

        String hexString = null;
        System.out.print(time+userId);

        try {
            if (tokenID.equals("NOT NFT")){
                TextView textView = findViewById(R.id.textViewImgContents);
                textView.setText("DB에 저장된 사진");
                hexString = intent.getExtras().getString("img");
            }
            else{
                hexString = b.get_NFT_info(Integer.valueOf(tokenID));
                TextView tokenPriceTextView = findViewById(R.id.tokenPriceTextView);
                tokenPriceTextView.setText("NFT 가격: " + price+" (KLAY)");
                TextView tokenPriceTextView2 = findViewById(R.id.tokenPriceTextView2);
                tokenPriceTextView2.setText("NFT 가격: " + priceToToken+" (NFC)");
                TextView contractTextView = findViewById(R.id.contractTextView);
                contractTextView.setText("컨트랙트 주소: " + b.contract_address);
                TextView tokenIdTextView = findViewById(R.id.tokenIdTextView) ;
                tokenIdTextView.setText("토큰 ID: " + tokenID);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Link = "https://baobab.scope.klaytn.com/nft/";
        Link += b.contract_address + "/" + tokenID; //해당 주소가 그 NFT가 있는 주소가 된다.
        if (hexString != null){
            Bitmap bitmap = imageFile.hexStringToBitmap(hexString);
            ImageView img = findViewById(R.id.NFT_image);
            img.setImageBitmap(bitmap);
        }

        TextView userIdTextView = findViewById(R.id.userIdTextView) ;
        userIdTextView.setText("게시자 : " + userId);
        Button copyButton = findViewById(R.id.copyButton);
        TextView info = findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(NftInfoActivity.this,tutorialActivity.class);
                startActivity(infoIntent);
            }
        });

        copyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", userId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(NftInfoActivity.this, "클립 보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });


        Button button1 = findViewById(R.id.explorerButton);
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(Link));
                startActivity(intentUrl);
            }
        });
        //거래버튼 생성 및 리스너 지정
        Button purchaseButton = findViewById(R.id.purchaseButton);
        Button purchaseButtonToken = findViewById(R.id.purchaseButtonToken);

        //토큰이 아니라면 거래버튼 비활성화
        if(intent.getExtras().getString("isToken").equals("False"))
            purchaseButton.setVisibility(View.GONE);

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String privateKey = ( (LoginInfo) getApplication() ).getPrivateKey();
                String address = ( (LoginInfo) getApplication() ).getAddress();
                String memo = "NFT 전송자: " + userId+ " NFT 수신자: " + address + " 가격 : " + priceToToken +"KLAY";
                //잔고가 부족하면 전송 안된다.
                try {
                    b.send_KLAY(privateKey, userId,memo, price);
                } catch (Exception e) {
                    Toast.makeText(NftInfoActivity.this, "KLAY 잔액 부족", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    b.send_NFT(b.private_key, address, Integer.parseInt(tokenID));
                } catch (Exception e) {
                    Toast.makeText(NftInfoActivity.this, "NFT 전송 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    //여기서 부터 포인트 지급.
                    // 지급량 = 가격(KLAY) * (남은 지급량 / KLAY 총 발행량) -- 판매자 및 구매자 모두에게 반반 지급.
                    //String result = div.multiply(new BigInteger(price)).toString();
                    b.send_Token(b.private_key, address, "1"); //구매자 지급
                    //b.send_Token(b.private_key, userId, result); //판매자 지급.
                } catch (Exception e) {
                    Toast.makeText(NftInfoActivity.this, "포인트 지급 실패", Toast.LENGTH_SHORT).show();
                }
                delete_db(tokenID, "SellPost");
                modify_db(tokenID, address, "NormalPost");
                finish(); //더 이상 액티비티를 실행할 이유가 없음.
            }
        });

        purchaseButtonToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String privateKey = ( (LoginInfo) getApplication() ).getPrivateKey();
                String address = ( (LoginInfo) getApplication() ).getAddress();
                //잔고가 부족하면 전송 안된다.
                try {
                    b.send_Token(privateKey, userId, priceToToken);
                } catch (Exception e) {
                    Toast.makeText(NftInfoActivity.this, "NFC 금액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    b.send_NFT(b.private_key, address, Integer.parseInt(tokenID));
                } catch (Exception e) {
                    Toast.makeText(NftInfoActivity.this, "NFT 전송 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                delete_db(tokenID, "SellPost");
                modify_db(tokenID, address, "NormalPost");
                finish(); //더 이상 액티비티를 실행할 이유가 없음.
            }
        });
    }

    private void delete_db(String tokenNum, String dbName){
        HashMap<String, String> map = new HashMap<>();
        if (dbName.equals("NormalPost"))
            map = keyTokenNumNormal;
        else
            map = keyTokenNumSell;
        database.getReference().child(dbName).child(Objects.requireNonNull(map.get(tokenNum))).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NftInfoActivity.this, "거래 성공", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NftInfoActivity.this, "거래 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modify_db(String tokenNum, String newAddress, String dbName){
        PostingItem post = tokenNumPostingItemMap.get(tokenNum);
        delete_db(tokenNum,dbName);
        post.setPostUser(newAddress); //주소를 바꾸고 다시 db에 올린다는 뜻.
        DatabaseReference userRef = ref.child(dbName);
        userRef.child(post.getPostUser()+post.getPostedDate()).setValue(post);
    }
}