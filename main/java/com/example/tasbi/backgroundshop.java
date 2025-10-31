// app/src/main/java/com/example/tasbi/backgroundshop.java
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

public class backgroundshop extends AppCompatActivity {

    private static final String PREFS_NAME = "TasbiPrefs";
    private static final String KEY_BG     = "selectedBackground";
    public String adtkken = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_shop);
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
        ImageView iv1 = findViewById(R.id.ivSlot1);
        ImageView iv2 = findViewById(R.id.ivSlot2);
        ImageView iv3 = findViewById(R.id.ivSlot3);
        ImageView iv4 = findViewById(R.id.ivSlot4);
        applyRipple(iv1);
        applyRipple(iv2);
        applyRipple(iv3);
        applyRipple(iv4);
        iv1.setOnClickListener(v -> pick(R.drawable.bg1));
        iv2.setOnClickListener(v -> pick(R.drawable.bg2));
        iv3.setOnClickListener(v -> pick(R.drawable.bg3));
        iv4.setOnClickListener(v -> pick(R.drawable.bg4));
    }

    private void pick(int resId) {
        // ذخیره پایدار
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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_BG, resId).apply();

        // نتیجه فوری برای شاپ → Main
        Intent data = new Intent().putExtra("bg", resId);
        setResult(RESULT_OK, data);
        finish();
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
