package com.example.promise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent main = new Intent(LoadingActivity.this,MainActivity.class);
                //startActivity(main);
                Intent login = new Intent(LoadingActivity.this,LoginActivity.class);
                startActivity(login);
                finish();       // 1 초후 이미지를 닫아버림
            }
        }, 1500);

    }

}
