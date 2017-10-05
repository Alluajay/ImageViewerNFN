package com.example.allu.imageviewer.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.allu.imageviewer.R;

public class SplashScreenActivity extends AppCompatActivity {
    static String TAG = SplashScreenActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new CountDownTimer(2500,1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
            }
        }.start();
    }
}
