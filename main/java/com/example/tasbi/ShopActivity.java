// app/src/main/java/com/example/tasbi/ShopActivity.java
package com.example.tasbi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private ImageView btnTasbih; // id=btnTasbih
    private ImageView btnThemes; // id=btnThemes

    private ActivityResultLauncher<Intent> ringLauncher;
    private ActivityResultLauncher<Intent> bgLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        btnThemes = findViewById(R.id.btnThemes);
        btnTasbih = findViewById(R.id.btnTasbih);

        // لانچر حلقه‌ها
        ringLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setResult(Activity.RESULT_OK, result.getData());
                        finish();
                    }
                });

        // لانچر تم‌ها (بک‌گراندها)
        bgLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setResult(Activity.RESULT_OK, result.getData());
                        finish();
                    }
                });

        btnTasbih.setOnClickListener(v ->
                ringLauncher.launch(new Intent(this, Ringshop.class)));

        btnThemes.setOnClickListener(v ->
                bgLauncher.launch(new Intent(this, backgroundshop.class)));
    }
}
