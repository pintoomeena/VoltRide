package com.example.bikexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private BikeAdapter bikeAdapter;
    private List<BikeModel> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        setupHeader();
        setupRecycler();
        setupCategories();
        setupBottomNav();
        loadBikes("All");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWalletDisplay();
        loadHomeProfileImage();
    }

    // ── Header ───────────────────────────────────────────────
    private void setupHeader() {
        String name = prefs.getString("userName", "Rider");
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvUsername = findViewById(R.id.tv_username);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Good Morning!";
        else if (hour < 17) greeting = "Good Afternoon!";
        else greeting = "Good Evening!";

        tvGreeting.setText(greeting);
        tvUsername.setText(name);

        updateWalletDisplay();

        String email = prefs.getString("userEmail", "");
        Cursor c = db.getUserByEmail(email);
        if (c.moveToFirst()) {
            int rides = c.getInt(
                    c.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL_RIDES));
            ((TextView) findViewById(R.id.tv_total_rides))
                    .setText(String.valueOf(rides));
        }
        c.close();

        // Profile button click → go to ProfileActivity
        findViewById(R.id.profile_btn).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    // ── Load profile image into home button ──────────────────
    private void loadHomeProfileImage() {
        String path = prefs.getString("profileImagePath", null);
        ImageView ivHomeAvatar = findViewById(R.id.iv_home_avatar);
        TextView tvHomeAvatar = findViewById(R.id.tv_home_avatar);

        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    ivHomeAvatar.setImageBitmap(bitmap);
                    ivHomeAvatar.setVisibility(View.VISIBLE);
                    tvHomeAvatar.setVisibility(View.GONE);
                    return;
                }
            }
        }
        // No image — show initials
        String name = prefs.getString("userName", "R");
        String initial = name.length() > 0
                ? String.valueOf(name.charAt(0)).toUpperCase() : "R";
        tvHomeAvatar.setText(initial);
        ivHomeAvatar.setVisibility(View.GONE);
        tvHomeAvatar.setVisibility(View.VISIBLE);
    }

    // ── Wallet display ───────────────────────────────────────
    private void updateWalletDisplay() {
        int userId = prefs.getInt("userId", -1);
        double balance = db.getWalletBalance(userId);
        ((TextView) findViewById(R.id.tv_wallet_balance))
                .setText("₹" + (int) balance);
    }

    // ── RecyclerView ─────────────────────────────────────────
    private void setupRecycler() {
        RecyclerView recycler = findViewById(R.id.bikes_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        bikeAdapter = new BikeAdapter(this, bikeList);
        recycler.setAdapter(bikeAdapter);
    }

    // ── Load bikes ───────────────────────────────────────────
    private void loadBikes(String category) {
        bikeList.clear();
        Cursor c = db.getBikesByCategory(category);
        while (c.moveToNext()) {
            bikeList.add(new BikeModel(
                    c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_ID)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_DESC)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_CATEGORY)),
                    c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_PRICE)),
                    c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_RATING)),
                    c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_REVIEWS)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_RANGE)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_GEARS)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_WEIGHT)),
                    c.getInt(c.getColumnIndexOrThrow(
                            DatabaseHelper.COL_BIKE_AVAILABLE)) == 1
            ));
        }
        c.close();
        bikeAdapter.notifyDataSetChanged();
    }

    // ── Category chips ───────────────────────────────────────
    private void setupCategories() {
        int[] chipIds = {R.id.chip_all, R.id.chip_mountain, R.id.chip_electric,
                R.id.chip_city, R.id.chip_road, R.id.chip_bmx};
        String[] cats = {"All", "Mountain", "Electric", "City", "Road", "BMX"};

        for (int i = 0; i < chipIds.length; i++) {
            final String cat = cats[i];
            final int chipId = chipIds[i];
            TextView chip = findViewById(chipId);
            chip.setOnClickListener(v -> {
                loadBikes(cat);
                for (int id : chipIds) {
                    TextView ch = findViewById(id);
                    if (id == chipId) {
                        ch.setBackgroundResource(R.drawable.bg_chip_selected);
                        ch.setTextColor(getColor(R.color.white));
                    } else {
                        ch.setBackgroundResource(R.drawable.bg_chip_unselected);
                        ch.setTextColor(getColor(R.color.text_secondary));
                    }
                }
            });
        }
    }

    // ── Bottom navigation ────────────────────────────────────
    private void setupBottomNav() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {});
        findViewById(R.id.nav_rentals).setOnClickListener(v ->
                startActivity(new Intent(this, MyRentalsActivity.class)));
        findViewById(R.id.nav_wallet).setOnClickListener(v ->
                startActivity(new Intent(this, WalletActivity.class)));
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}