package com.example.scalorie_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    SharedPreferences sp;
    String isLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        sp = getSharedPreferences("checkBox", MODE_PRIVATE);
        isLogin = sp.getString("isLogin", "");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isLogin.equals("true")) {
                      intent = new Intent(MainActivity.this, HomePage.class);
                }
                else {
                    intent = new Intent(MainActivity.this, Login_Activty.class);
                }
                startActivity(intent);
                finish();


            }
        }, 2300);

    }
}