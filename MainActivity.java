// app/src/main/java/com/example/tasbi/MainActivity.java
package com.example.tasbi;

import android.media.MediaPlayer;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.widget.Toast;

// ▼ جدیدها
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import ir.tapsell.sdk.nativeads.NativeAdRequestListener;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerViewManager;

public class MainActivity extends AppCompatActivity {

    // ---- Prefs ----
    private static final String PREFS_NAME = "TasbiPrefs";
    private static final String KEY_BG     = "selectedBackground";
    private static final String KEY_RING   = "selected_tasbih";
    private static final String KEY_MUTED  = "muted";                // ▼ جدید

    private MediaPlayer mp;
    public String adtkken = null;

    // ---- UI ----
    private int count = 0;
    private TextView tvCount;
    private ImageView imgTasbih;     // تصویر حلقه تسبیح (قابل تعویض)
    private ImageView imgBg;         // ImageView بک‌گراند (id=imgBg)
    private ImageView ivShop;        // آیکن شاپ
    private ImageView btnMinus;      // دکمه منفی (id=imgBrownOval2)
    private ImageView btnZero;       // دکمه صفر (id=imgBrownOval)
    private LinearLayout sideMenu;
    private boolean isMenuVisible = false;
    private ImageButton btnMore;     // ▼ سه‌نقطه

    TapsellNativeBannerViewManager nativeBannerViewManager = null;
    private ActivityResultLauncher<Intent> shopLauncher;

    // شناسه‌های آیتم‌های منو
    private static final int ID_MUTE = 1, ID_CALL = 2, ID_SOCIAL = 3;

    private MediaPlayer bgPlayer;     // اگر موزیک داری (فعلاً استفاده نمی‌کنیم)
    private boolean isMuted;          // وضعیت صدا
    private SharedPreferences prefs;

    // اطلاعات تماس/شبکه‌های اجتماعی را اینجا تنظیم کن
    private static final String PHONE_NUMBER = "09150772285";
    private static final String SOCIAL_URL   = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prefs
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isMuted = prefs.getBoolean(KEY_MUTED, false);

        // صدای کلیک تسبیح
        mp = MediaPlayer.create(this, R.raw.tasbi_music);
        if (mp != null) {
            mp.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);  // مطابق mute
        }

        ViewGroup adContainer = findViewById(R.id.adContainer);
        nativeBannerViewManager = new TapsellNativeBannerManager
                .Builder()
                .setParentView(adContainer)
                .setContentViewTemplate(R.layout.tapsell_game)
                .inflateTemplate(this);

        requestAd();
        Tapsell.requestAd(getApplicationContext(),
                "68e2436dbfb97a73bda02b93",
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(String adId) {
                        adtkken = adId;
                    }
                    @Override
                    public void onError(String message) { }
                });

        // رجیستر لانچر فروشگاه
        shopLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        refreshSelections();
                    }
                }
        );

        // اتصال ویوها
        tvCount   = findViewById(R.id.tvCount);
        imgTasbih = findViewById(R.id.imgTasbih);
        imgBg     = findViewById(R.id.imgBg);
        ivShop    = findViewById(R.id.ivShop);
        btnMinus  = findViewById(R.id.imgBrownOval2);
        btnZero   = findViewById(R.id.imgBrownOval);
        btnMore   = findViewById(R.id.btnMore);   // ▼ سه‌نقطه

        // چک NPE
        if (tvCount==null || imgTasbih==null || imgBg==null || ivShop==null || btnMinus==null || btnZero==null) {
            Toast.makeText(this,
                    "آیدی‌های activity_main.xml با کد یکی نیستند (tvCount/imgTasbih/imgBg/ivShop/imgBrownOval/imgBrownOval2).",
                    Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Null view: "
                    + "tv="+(tvCount==null)+" tasbih="+(imgTasbih==null)+" bg="+(imgBg==null)
                    +" shop="+(ivShop==null)+" minus="+(btnMinus==null)+" zero="+(btnZero==null));
            return;
        }

        // کلیک حلقه تسبیح (با احترام به Mute)
        imgTasbih.setOnClickListener(v -> {
            if (!isMuted && mp != null) {
                try { mp.start(); } catch (Exception ignored) {}
            }
            count++;
            tvCount.setText(String.valueOf(count));
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            animateTasbih();
            checkMilestone();
        });

        btnMinus.setOnClickListener(v -> {
            if (count > 0) {
                count--;
                tvCount.setText(String.valueOf(count));
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                animateMinus(btnMinus);
            }
        });

        btnZero.setOnClickListener(v -> {
            Tapsell.showAd(getApplicationContext(),
                    "68e2436dbfb97a73bda02b93",
                    adtkken,
                    new TapsellShowOptions(),
                    new TapsellAdShowListener() {
                        @Override public void onOpened() {}
                        @Override public void onClosed() {}
                        @Override public void onError(String message) {}
                        @Override public void onRewarded(boolean completed) {}
                    });
            count = 0;
            tvCount.setText(String.valueOf(count));
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            animateZero(btnZero);

            Tapsell.requestAd(getApplicationContext(),
                    "68e2436dbfb97a73bda02b93",
                    new TapsellAdRequestOptions(),
                    new TapsellAdRequestListener() {
                        @Override public void onAdAvailable(String adId) { adtkken = adId; }
                        @Override public void onError(String message) { }
                    });
        });

        // شاپ
        ivShop.setClickable(true);
        ivShop.bringToFront();
        if (Build.VERSION.SDK_INT >= 21) ivShop.setZ(100f);
        ivShop.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            animateShop(ivShop, () -> {
                try {
                    shopLauncher.launch(new Intent(MainActivity.this, ShopActivity.class));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "ShopActivity در مانیفست ثبت نشده یا وجود ندارد.", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "ShopActivity not found", e);
                }
            });
        });


        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ▼ سه‌نقطه: PopupMenu
                if (btnMore != null) {

                    showOverflow((ImageView) v);
                }

            }
        });




        // همگام‌سازی UI
        refreshSelections();
    }



    // ---------- سه‌نقطه ----------
    private void showOverflow(ImageView anchor) {
        animateShop(anchor, () -> {
            PopupMenu pm = new PopupMenu(this, anchor, Gravity.END);
            Menu menu = pm.getMenu();
            menu.add(0, ID_MUTE,   0, isMuted ? "🔊 صدا دار" : "🔇 بی صدا");
            menu.add(0, ID_CALL,   1, "☎ تلفن");
            menu.add(0, ID_SOCIAL, 2, "🌐 کانال تلگرامی");

            pm.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case ID_MUTE:
                        isMuted = !isMuted;
                        if (mp != null)
                            mp.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);
                        prefs.edit().putBoolean(KEY_MUTED, isMuted).apply();
                        return true;

                    case ID_CALL:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER)));
                        return true;

                    case ID_SOCIAL:
                        Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(SOCIAL_URL));
                        startActivity(Intent.createChooser(view, "Open with"));
                        return true;
                }
                return false;
            });

            pm.show();
        });
    }

    // -----------------------------

    private void checkMilestone() {
        if (count > 0 && count % 100 == 0) {
            String message = "🎉 تبریک! " + count + " صلوات فرستادی.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            Tapsell.showAd(getApplicationContext(),
                    "68e2436dbfb97a73bda02b93",
                    adtkken,
                    new TapsellShowOptions(),
                    new TapsellAdShowListener() {
                        @Override public void onOpened() {}
                        @Override public void onClosed() {}
                        @Override public void onError(String message) {}
                        @Override public void onRewarded(boolean completed) {}
                    });
            Tapsell.requestAd(getApplicationContext(),
                    "68e2436dbfb97a73bda02b93",
                    new TapsellAdRequestOptions(),
                    new TapsellAdRequestListener() {
                        @Override public void onAdAvailable(String adId) { adtkken = adId; }
                        @Override public void onError(String message) { }
                    });

            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(700, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(700);
                }
            }
        }
    }

    /** انیمیشن بالا/پایین + پالس برای تسبیح */
    private void animateTasbih() {
        if (imgTasbih == null) return;
        imgTasbih.animate().cancel();

        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(imgTasbih, "scaleX", 1f, 0.94f);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(imgTasbih, "scaleY", 1f, 0.94f);
        scaleX1.setDuration(70);
        scaleY1.setDuration(70);

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(imgTasbih, "scaleX", 0.94f, 1f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(imgTasbih, "scaleY", 0.94f, 1f);
        scaleX2.setDuration(140);
        scaleY2.setDuration(140);
        scaleX2.setInterpolator(new OvershootInterpolator(2f));
        scaleY2.setInterpolator(new OvershootInterpolator(2f));

        float dy = -28f;
        ObjectAnimator up   = ObjectAnimator.ofFloat(imgTasbih, "translationY", 0f, dy);
        ObjectAnimator down = ObjectAnimator.ofFloat(imgTasbih, "translationY", dy, 0f);
        up.setDuration(80);
        down.setDuration(180);
        down.setInterpolator(new BounceInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX2, scaleY2, up);
        set.play(scaleX1).before(set.getChildAnimations().get(0));
        set.play(down).after(up);
        set.start();
    }

    /** انیمیشن برای دکمه صفر */
    private void animateZero(ImageView target) {
        if (target == null) return;

        ObjectAnimator left  = ObjectAnimator.ofFloat(target, "translationX", 0f, -25f);
        ObjectAnimator right = ObjectAnimator.ofFloat(target, "translationX", -25f, 25f);
        ObjectAnimator back  = ObjectAnimator.ofFloat(target, "translationX", 25f, 0f);

        left.setDuration(80);
        right.setDuration(120);
        back.setDuration(100);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(left, right, back);
        set.start();
    }

    /** انیمیشن برای دکمه منفی */
    private void animateMinus(ImageView target) {
        if (target == null) return;

        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(target, "scaleX", 1f, 0.85f);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(target, "scaleY", 1f, 0.85f);
        scaleX1.setDuration(100);
        scaleY1.setDuration(100);

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(target, "scaleX", 0.85f, 1f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(target, "scaleY", 0.85f, 1f);
        scaleX2.setDuration(150);
        scaleY2.setDuration(150);
        scaleX2.setInterpolator(new OvershootInterpolator(2f));
        scaleY2.setInterpolator(new OvershootInterpolator(2f));

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(scaleX1, scaleX2, scaleY1, scaleY2);
        set.start();
    }

    /** افکت پالس برای دکمه شاپ */
    private void animateShop(ImageView target, Runnable onEnd) {
        if (target == null) { if (onEnd != null) onEnd.run(); return; }

        target.animate().cancel();
        target.setEnabled(false);

        ObjectAnimator sx1 = ObjectAnimator.ofFloat(target, "scaleX", 1f, 0.85f);
        ObjectAnimator sy1 = ObjectAnimator.ofFloat(target, "scaleY", 1f, 0.85f);
        sx1.setDuration(100);  sy1.setDuration(100);

        ObjectAnimator sx2 = ObjectAnimator.ofFloat(target, "scaleX", 0.85f, 1f);
        ObjectAnimator sy2 = ObjectAnimator.ofFloat(target, "scaleY", 0.85f, 1f);
        sx2.setDuration(150);  sy2.setDuration(150);
        sx2.setInterpolator(new OvershootInterpolator(2f));
        sy2.setInterpolator(new OvershootInterpolator(2f));

        AnimatorSet set = new AnimatorSet();
        set.play(sx1).with(sy1);
        set.play(sx2).with(sy2).after(sx1);

        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                target.setEnabled(true);
                if (onEnd != null) onEnd.run();
            }
            @Override public void onAnimationCancel(android.animation.Animator animation) {
                target.setEnabled(true);
            }
        });

        set.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSelections();
    }

    /** بررسی معتبر بودن آیدی drawable */
    private boolean isValidDrawableRes(int resId) {
        if (resId == 0) return false;
        try {
            return "drawable".equals(getResources().getResourceTypeName(resId));
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    /** اعمال انتخاب‌های ذخیره‌شده */
    private void refreshSelections() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int defBg   = R.drawable.bg1;
        int defRing = R.drawable.tasbinew;

        int bgResId   = prefs.getInt(KEY_BG,   defBg);
        int ringResId = prefs.getInt(KEY_RING, defRing);

        if (!isValidDrawableRes(bgResId)) {
            bgResId = defBg;
            prefs.edit().putInt(KEY_BG, defBg).apply();
            Log.w("MainActivity", "Invalid bg drawable in prefs. Reset to default.");
        }
        if (!isValidDrawableRes(ringResId)) {
            ringResId = defRing;
            prefs.edit().putInt(KEY_RING, defRing).apply();
            Log.w("MainActivity", "Invalid ring drawable in prefs. Reset to default.");
        }

        imgBg.setImageResource(bgResId);
        imgTasbih.setImageResource(ringResId);
    }

    private void toggleMenu() {
        if (sideMenu == null) return;
        if (isMenuVisible) {
            sideMenu.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .withEndAction(() -> sideMenu.setVisibility(View.GONE))
                    .start();
        } else {
            sideMenu.setAlpha(0f);
            sideMenu.setVisibility(View.VISIBLE);
            sideMenu.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start();
        }
        isMenuVisible = !isMenuVisible;
    }

    public MediaPlayer getMp() { return mp; }
    public void setMp(MediaPlayer mp) {
        this.mp = mp;
        if (this.mp != null) this.mp.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);
    }

    private void requestAd() {
        TapsellNativeBannerManager.getAd(getApplicationContext(), "68e785d29e100451dc397eda",
                new NativeAdRequestListener() {
                    @Override
                    public void onResponse(String[] strings) {
                        TapsellNativeBannerManager.bindAd(
                                getApplicationContext(),
                                nativeBannerViewManager,
                                "68e785d29e100451dc397eda",
                                strings[0]);
                    }
                    @Override public void onFailed(String s) { }
                });
    }
}
