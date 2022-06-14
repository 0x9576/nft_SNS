package com.example.stagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.klaytn.caver.wallet.keyring.SingleKeyring;

public class LoginActivity extends AppCompatActivity {

    static String userAddress;
    static String userPrivateKey;
    /**
     * Test code*/
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    /** */


    @Override
    protected void onCreate(Bundle savedInstanceState) {;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button bt_Login=findViewById(R.id.bt_Login);
        Button btSignUp=findViewById(R.id.signUpButton);
        EditText editText = (EditText)findViewById(R.id.editTextPrivateKey);
        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String privateKey = editText.getText().toString();
                Blockchain b = new Blockchain();
                String address = null;
                try {
                    //keyring 이 정상적으로 생기면 intent 생성후 주소와 개인키를 저장함.
                    SingleKeyring keyring= (SingleKeyring) b.caver.wallet.keyring.createFromPrivateKey(privateKey);
                    address = keyring.getAddress();
                } catch (Exception e) {
                    //keyring 이 정상적으로 생성되지 않았을 때, 토스트 메시지
                    Toast.makeText(LoginActivity.this,"올바르지 않은 비밀키입니다.",Toast.LENGTH_SHORT).show();
                }
                if(address != null){
                    Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
                    loginIntent.putExtra("privateKey",privateKey);
                    loginIntent.putExtra("address",address);
                    ( (LoginInfo) getApplication() ).setPrivateKey(privateKey);
                    ( (LoginInfo) getApplication() ).setAddress(address);
                    userAddress=address;
                    userPrivateKey=privateKey;
                    Log.e("LoginAddress : ",userAddress);
                    startActivity(loginIntent);
                    finish();
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}