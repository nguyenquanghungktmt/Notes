package com.example.product_notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    int SPLASH_TIME_OUT = 2000;
    Button btn_getStart;
    private Handler handler;

    private Runnable goToHomeRunnable = () -> {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        handler = new Handler();
        handler.postDelayed(goToHomeRunnable, SPLASH_TIME_OUT);
        btn_getStart = findViewById(R.id.btn_getStart);
        btn_getStart.setOnClickListener(this);

        // hide action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }

    @Override
    public void onClick(View view) {
        if (handler != null) {
            handler.removeCallbacks(goToHomeRunnable);
        }
        Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
