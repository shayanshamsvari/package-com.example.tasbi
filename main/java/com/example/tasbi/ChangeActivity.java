package com.example.tasbi;

import android.animation.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeActivity extends AppCompatActivity {

    private ImageView btnFrame1, btnFrame2, dove, voice_bg;
    private MediaPlayer mp;

    private static final String PREFS_NAME = "TasbiPrefs";
    private static final String KEY_MUTED  = "bg_music_muted";

    private boolean isMuted = false;

    private float dp(float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_activity);

        // موزیک پس‌زمینه (res/raw/bg_music.* باید وجود داشته باشد)
        mp = MediaPlayer.create(this, R.raw.bg_music);
        mp.setLooping(true);
        mp.setVolume(1f, 1f);

        final View root = findViewById(R.id.root);
        dove      = findViewById(R.id.doveFrame);
        btnFrame1 = findViewById(R.id.btnFrame1);
        btnFrame2 = findViewById(R.id.btnFrame2);
        voice_bg  = findViewById(R.id.voice_bg); // باید ImageView باشد

        if (root == null || dove == null) return;

        // وضعیت قبلی میوت را لود کن و UI/پخش را هماهنگ کن
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isMuted = prefs.getBoolean(KEY_MUTED, false);
        applyMuteUi();

        // کلیک: آیکن جابه‌جا می‌شود و پخش موزیک pause/start می‌شود
        voice_bg.setOnClickListener(v -> {
            isMuted = !isMuted;
            prefs.edit().putBoolean(KEY_MUTED, isMuted).apply();
            applyMuteUi();
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        });

        // دکمه‌ها
        if (btnFrame1 != null) {
            btnFrame1.setOnClickListener(v ->
                    animatePress(v, this::openMainActivity));
        }
        if (btnFrame2 != null) {
            btnFrame2.setOnClickListener(v ->
                    animatePress(v, this::openTormajaziActivity));
        }

        // انیمیشن ورود و شناوری
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);

                float startY = root.getHeight() + dove.getHeight();
                dove.setTranslationY(startY);
                dove.setAlpha(0f);
                dove.setScaleX(0.9f);
                dove.setScaleY(0.9f);

                AnimatorSet enter = new AnimatorSet();
                enter.playTogether(
                        ObjectAnimator.ofFloat(dove, View.TRANSLATION_Y, 0f),
                        ObjectAnimator.ofFloat(dove, View.ALPHA, 1f),
                        ObjectAnimator.ofFloat(dove, View.SCALE_X, 1f),
                        ObjectAnimator.ofFloat(dove, View.SCALE_Y, 1f)
                );
                enter.setDuration(900);
                enter.setStartDelay(200);
                enter.setInterpolator(new OvershootInterpolator(0.8f));

                enter.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        float bob = dp(6);
                        ObjectAnimator bobAnim =
                                ObjectAnimator.ofFloat(dove, View.TRANSLATION_Y, -bob, bob);
                        bobAnim.setDuration(2600);
                        bobAnim.setRepeatCount(ValueAnimator.INFINITE);
                        bobAnim.setRepeatMode(ValueAnimator.REVERSE);
                        bobAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                        bobAnim.start();
                    }
                });

                enter.start();
                return true;
            }
        });
    }

    /** آیکن را با وضعیت میوت هماهنگ می‌کند و پخش را کنترل می‌کند */
    private void applyMuteUi() {
        if (voice_bg != null) {
            voice_bg.setImageResource(isMuted ? R.drawable.ic_volume_off
                    : R.drawable.ic_volume_on);
        }
        if (mp != null) {
            if (isMuted && mp.isPlaying()) mp.pause();
            else if (!isMuted && !mp.isPlaying()) mp.start();
        }
    }

    @Override protected void onStart() {
        super.onStart();
        if (mp != null && !isMuted && !mp.isPlaying()) mp.start();
    }

    @Override protected void onPause() {
        super.onPause();
        if (mp != null && mp.isPlaying()) mp.pause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (mp != null) { mp.release(); mp = null; }
    }

    /** دکمه ۱ → MainActivity */
    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    /** دکمه ۲ → Tormajazi */
    private void openTormajaziActivity() {
        startActivity(new Intent(this, Tormajazi.class));
    }

    /** افکت فشردن دکمه، بعد اجرای اکشن مقصد */
    private void animatePress(final View target, final Runnable then) {
        target.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80)
                .withEndAction(() ->
                        target.animate().scaleX(1f).scaleY(1f)
                                .setDuration(120).withEndAction(then).start()
                ).start();
    }
}
