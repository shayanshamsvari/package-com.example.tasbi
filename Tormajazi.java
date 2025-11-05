package com.example.tasbi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.content.DialogInterface; // NEW
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.appcompat.app.AlertDialog; // NEW
import androidx.core.content.ContextCompat;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class Tormajazi extends AppCompatActivity {

    private ImageView img1, img2, img3, img4;
public String videoid=null;

    TapsellShowOptions showOptions=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tor_majazi);



   showOptions = new TapsellShowOptions();
        showOptions.setBackDisabled(true|false);
        showOptions.setImmersiveMode(true|false);
        showOptions.setShowDialog(true|false);
        showOptions.setRotationMode(TapsellShowOptions.ROTATION_LOCKED_PORTRAIT);


        Tapsell.requestAd(getApplicationContext(),
                "68e77d94a82c28634aaef15d",
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(String adId) {

                        videoid=adId;
                    }

                    @Override
                    public void onError(String message) {
                    }
                });
        // 1) بایند کردن ویوها
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);

        // 2) اگر در XML src نگذاشتی، اینجا می‌تونی ست کنی (اختیاری)
        // img1.setImageResource(R.drawable.your_img_1);
        // img2.setImageResource(R.drawable.your_img_2);
        // img3.setImageResource(R.drawable.your_img_3);
        // img4.setImageResource(R.drawable.your_img_4);

        // 3) اعمال Ripple/اسپلش و اسکیل هنگام فشار
        applyRipple(img1);
        applyRipple(img2);
        applyRipple(img3);
        applyRipple(img4);

        addPressScale(img1);
        addPressScale(img2);
        addPressScale(img3);
        addPressScale(img4);

        // 4) کلیک‌ها (همه با اخطار قبل از ادامه)
        addLinkClickWithConfirm(img1, "https://www.panoman.ir/vt/shiraz/shahe-cheragh/");
        addLinkClickWithConfirm(img2, "https://www.panoman.ir/projects/rasolallah/1.html");
        addLinkClickWithConfirm(img3, "https://vtour.amfm.ir/");
        addLinkClickWithConfirm(img4, "https://najaf.ahlolbait.com/");
    }

    /** روی کلیک، اول اخطار می‌آید و بعد از تایید لینک باز می‌شود */
    private void addLinkClickWithConfirm(View buttonId, String url) {
        buttonId.setOnClickListener(v -> showConfirmThenOpen(url));
    }

    /** دیالوگ تایید بدون لغو؛ فقط دکمه "تایید" */
    private void showConfirmThenOpen(String url) {
        // رنگ‌دهی به عنوان
        SpannableString title = new SpannableString("توجه!");
        title.setSpan(new ForegroundColorSpan(Color.parseColor("#4E342E")), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // قهوه‌ای تیره

        // رنگ‌دهی به متن
        SpannableString msg = new SpannableString(
                "برای حمایت از توسعه‌دهنده لطفاً این تبلیغ ویدیویی را ببینید بعد وارد حرم شوید"
        );
        msg.setSpan(new ForegroundColorSpan(Color.parseColor("#5D4037")), 0, msg.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true) // اجازه لغو با دکمه Back
                .setPositiveButton("مشاهده", (d, which) -> openInAppTab(url))
                .setNegativeButton("لغو", (d, which) -> d.dismiss()) // ← دکمه لغو
                .create();

        dialog.show();
        dialog.setCanceledOnTouchOutside(false); // با لمس بیرون بسته نشه

        // پس‌زمینه کرمی + بوردر + گوشه‌گرد
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(this, R.drawable.alert_bg_cream)
            );
        }

        // استایل دکمه‌ها
        Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (ok != null) {
            ok.setAllCaps(false);
            ok.setTypeface(ok.getTypeface(), Typeface.BOLD); // بولد
            ok.setTextSize(16);
            ok.setTextColor(Color.parseColor("#000000")); // سبز تأیید


            Tapsell.requestAd(getApplicationContext(),
                    "68e77d94a82c28634aaef15d",
                    new TapsellAdRequestOptions(),
                    new TapsellAdRequestListener() {
                        @Override
                        public void onAdAvailable(String adId) {

                            videoid=adId;
                        }

                        @Override
                        public void onError(String message) {
                        }
                    });
            ok.setOnClickListener(v -> {
                Tapsell.showAd(getApplicationContext(),
                        "68e77d94a82c28634aaef15d",
                        videoid,
                        showOptions,
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

                                Toast.makeText(getApplicationContext(),"ممنون بابت مشاهده ویدیو" ,Toast.LENGTH_LONG);

                            }
                        });
                Intent intent = new Intent(Tormajazi.this, Webview.class);
                intent.putExtra("url", url);
                startActivity(intent);

                dialog.dismiss();
            });
        }

        Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (cancel != null) {
            cancel.setAllCaps(false);
            cancel.setTextSize(15);
            cancel.setTextColor(Color.parseColor("#f20808")); // خاکستری لغو
            // اگر بخوای برجسته‌تر باشه:
            // cancel.setTypeface(cancel.getTypeface(), Typeface.BOLD);
        }
    }

    private void openInAppTab(String url) {
        CustomTabsIntent tabs = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build();
        tabs.launchUrl(this, Uri.parse(url));
    }

    /** Ripple روی foreground تا روی خود عکس دیده شود */
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

    /** انیمیشن کوچیک‌شدن هنگام لمس برای حس بهتر */
    private void addPressScale(View v) {
        v.setOnTouchListener((vi, ev) -> {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    vi.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    vi.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false; // حتما false تا کلیک هم کار کند
        });
    }



}
