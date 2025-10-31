package com.example.tasbi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

public class splashactivity extends AppCompatActivity {

    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);

        // لوگو Scale 0 → 1 (Zoom In)
        imgLogo.setScaleX(0f);
        imgLogo.setScaleY(0f);
        imgLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                })
                .start();

        // بعد از 2 ثانیه به MainActivity برو
        new Handler().postDelayed(() -> {
            startActivity(new Intent(splashactivity.this, ChangeActivity.class));
            finish(); // جلوگیری از برگشت به Splash
        }, 2000);
    }

}
