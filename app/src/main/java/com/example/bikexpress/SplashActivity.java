package com.example.bikexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView icon = findViewById(R.id.splash_bike_icon);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView tagline = findViewById(R.id.splash_tagline);
        ProgressBar progress = findViewById(R.id.splash_progress);

        icon.animate().alpha(1f).setDuration(600).setStartDelay(200).start();
        appName.animate().alpha(1f).setDuration(600).setStartDelay(500).start();
        tagline.animate().alpha(1f).setDuration(600).setStartDelay(700).start();
        progress.animate().alpha(1f).setDuration(400).setStartDelay(900).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            boolean hasSeenOnboarding = prefs.getBoolean("hasSeenOnboarding", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(this, HomeActivity.class);
            } else if (!hasSeenOnboarding) {
                intent = new Intent(this, OnboardingActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2200);
    }
}