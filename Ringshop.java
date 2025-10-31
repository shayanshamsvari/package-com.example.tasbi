// app/src/main/java/com/example/tasbi/Ringshop.java
package com.example.tasbi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class Ringshop extends AppCompatActivity {

    private static final String PREFS_NAME = "TasbiPrefs";
    private static final String KEY_RING   = "selected_tasbih";
    public String adtkken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ring_shop);
        Tapsell.requestAd(getApplicationContext(),
                "68e2436dbfb97a73bda02b93",
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(String adId) {

                        adtkken = adId;


                    }

                    @Override
                    public void onError(String message) {
                    }
                });

        ImageView s1 = findViewById(R.id.ivSlot1);
        ImageView s2 = findViewById(R.id.ivSlot2);
        ImageView s3 = findViewById(R.id.ivSlot3);
        ImageView s4 = findViewById(R.id.ivSlot4);
        ImageView s5 = findViewById(R.id.ivSlot5);
        applyRipple(s1);
        applyRipple(s2);
        applyRipple(s3);
        applyRipple(s4);
        applyRipple(s5);
        s1.setOnClickListener(v -> pick(R.drawable.tasbinew));
        s2.setOnClickListener(v -> pick(R.drawable.tasbi2));
        s3.setOnClickListener(v -> pick(R.drawable.tasbi3));
        s4.setOnClickListener(v -> pick(R.drawable.tasbi4));
        s5.setOnClickListener(v -> pick(R.drawable.tasbi5));
    }

    private void pick(int resId) {
        Tapsell.showAd(getApplicationContext(),
                "68e2436dbfb97a73bda02b93",
                adtkken,
                new TapsellShowOptions(),
                new TapsellAdShowListener() {
                    @Override
                    public void onOpened() {
                    }

                    @Override
                    public void onClosed() {
                    }

                    @Override
                    public void onError(String message) {
                    }

                    @Override
                    public void onRewarded(boolean completed) {
                    }
                });
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_RING, resId).apply();

        Intent data = new Intent().putExtra("ring", resId);
        setResult(RESULT_OK, data);
        finish();
        Tapsell.requestAd(getApplicationContext(),
                "68e2436dbfb97a73bda02b93",
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(String adId) {

                        adtkken = adId;


                    }

                    @Override
                    public void onError(String message) {
                    }
                });
    }
    private void applyRipple(View v) {
        v.setClickable(true);
        v.setFocusable(true);

        TypedValue outValue = new TypedValue();
        // اگر حاشیه نمی‌خوای borderless بگذار؛ اگر می‌خوای داخل باکس بماند، selectableItemBackground
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // foreground از API 23 به بالا
            v.setForeground(getDrawable(outValue.resourceId));
        } else {
            // روی نسخه‌های قدیمی‌تر، به عنوان background می‌گذاریم
            v.setBackgroundResource(outValue.resourceId);
        }
    }

}
