package com.example.stagram;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Fragment fragmentFeed;
    Fragment fragmentMarket;
    Fragment fragmentGallery;
    Fragment fragmentProfile;
    String privateKey;
    static String address;

    /** 데이터베이스 가져오도록  static으로 정의 */
    public static String getMyAddress(){
        return address;
    }

    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference ref = database.getReference();

    static public DatabaseReference getRef(){
        return ref;
    }

    private BackPressHandler backPressHandler = new BackPressHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //intent 받아오기
        Bundle bundle = getIntent().getExtras();
        privateKey = bundle.getString("privateKey");
        address = bundle.getString("address");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentFeed = new FragmentFeed();
        fragmentMarket = new FragmentMarket();
        fragmentGallery = new FragmentGallery();
        fragmentProfile = new FragmentProfile();

        //getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentFeed).commit();

        //fragment에 address와 privateKey전달.
        bundle = new Bundle();
        bundle.putString("privateKey", privateKey);
        bundle.putString("address", address);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragmentFeed.setArguments(bundle);
        transaction.replace(R.id.container,fragmentFeed);
        transaction.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle bundle = new Bundle();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.menu_feed:
                        bundle = new Bundle();
                        bundle.putString("privateKey", privateKey);
                        bundle.putString("address", address);
                        fragmentFeed.setArguments(bundle);
                        transaction.replace(R.id.container,fragmentFeed);
                        transaction.commit();
                        return true;
                    case R.id.menu_market:
                        bundle = new Bundle();
                        bundle.putString("privateKey", privateKey);
                        bundle.putString("address", address);
                        fragmentMarket.setArguments(bundle);
                        transaction.replace(R.id.container,fragmentMarket);
                        transaction.commit();
                        return true;
                    case R.id.menu_gallery:
                        bundle = new Bundle();
                        bundle.putString("privateKey", privateKey);
                        bundle.putString("address", address);
                        fragmentGallery.setArguments(bundle);
                        transaction.replace(R.id.container,fragmentGallery);
                        transaction.commit();
                        return true;
                    case R.id.menu_profile:
                        bundle = new Bundle();
                        bundle.putString("privateKey", privateKey);
                        bundle.putString("address", address);
                        fragmentProfile.setArguments(bundle);
                        transaction.replace(R.id.container,fragmentProfile);
                        transaction.commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // Toast, 간격 사용자 지정
        backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 종료", 3000);
    }
}