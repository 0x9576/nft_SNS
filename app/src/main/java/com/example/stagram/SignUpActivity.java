package com.example.stagram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.klaytn.caver.wallet.keyring.SingleKeyring;

import java.util.HashSet;

public class SignUpActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);

        EditText nicknameText = findViewById(R.id.nicknameText);
        Button nicknameButton = findViewById(R.id.nicknameButton);
        TextView privateKeyTextView = findViewById(R.id.privateKeyTextView);
        Button privateKeyCopyButton = findViewById(R.id.privateKeyCopyButton);
        TextView addressTextView = findViewById(R.id.addressTextView);
        Button addressCopyButton = findViewById(R.id.addressCopyButton);
        Button faucetButton = findViewById(R.id.faucatButton);
        Button completeSignUpButton = findViewById(R.id.completeSignUpButton);

        Blockchain blockchain = new Blockchain();
        SingleKeyring keyring = blockchain.getNewKeyring();
        String privateKey = keyring.getKey().getPrivateKey();
        String address = keyring.getAddress();

        privateKeyTextView.setText("개인키: "+privateKey);
        addressTextView.setText("지갑 주소: "+address);

        HashSet<String> hashSet = new HashSet<>();
        ref.child("member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    hashSet.add(member.getNickname());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }});


        privateKeyCopyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", privateKey);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SignUpActivity.this, "클립 보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        addressCopyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SignUpActivity.this, "클립 보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        faucetButton.setOnClickListener(new Button.OnClickListener() {
            String link = "https://baobab.wallet.klaytn.foundation/faucet";
            @Override
            public void onClick(View view) {
                Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intentUrl);
            }
        });

        nicknameButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = nicknameText.getText().toString();
                if (hashSet.contains(nickname)){
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignUpActivity.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        completeSignUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = nicknameText.getText().toString();
                if (hashSet.contains(nickname)){
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String img = "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";
                    ref.child("member").child(address).child("img").setValue(img);
                    ref.child("member").child(address).child("address").setValue(address);
                    ref.child("member").child(address).child("nickname").setValue(nicknameText.getText().toString());
                    finish();
                }
            }
        });
    }
}