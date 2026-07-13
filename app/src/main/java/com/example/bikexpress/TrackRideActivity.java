package com.example.bikexpress;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TrackRideActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private long startMillis;
    private long totalMillis;
    private boolean running = true;
    private double distanceKm = 0.0;
    private int rentalId;
    private int locationIndex = 0;

    private TextView tvElapsed, tvRemaining, tvDistance, tvLocation;

    private final String[] locations = {
            "Bandra Station, Mumbai",
            "Linking Road, Bandra",
            "Carter Road, Bandra",
            "Bandstand Promenade",
            "Juhu Beach Road",
            "Versova, Andheri",
            "DN Nagar, Andheri"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_ride);

        rentalId = getIntent().getIntExtra("rentalId", -1);
        String durationStr = getIntent().getStringExtra("duration");
        String bikeName = getIntent().getStringExtra("bikeName");

        tvElapsed = findViewById(R.id.tv_elapsed);
        tvRemaining = findViewById(R.id.tv_remaining);
        tvDistance = findViewById(R.id.tv_distance);
        tvLocation = findViewById(R.id.tv_current_location);

        totalMillis = parseDurationMillis(durationStr);
        startMillis = System.currentTimeMillis();

        startTimer();
        startLocationSimulation();

        findViewById(R.id.btn_back_track).setOnClickListener(v -> finish());

        Button btnEnd = findViewById(R.id.btn_end_ride);
        btnEnd.setOnClickListener(v -> endRide());
    }

    private long parseDurationMillis(String durationStr) {
        if (durationStr == null) return 60 * 60 * 1000L;
        if (durationStr.contains("day")) return 24 * 60 * 60 * 1000L;
        try {
            int hrs = Integer.parseInt(durationStr.replaceAll("[^0-9]", ""));
            return (long) hrs * 60 * 60 * 1000L;
        } catch (Exception e) {
            return 60 * 60 * 1000L;
        }
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                long elapsed = System.currentTimeMillis() - startMillis;
                long remaining = totalMillis - elapsed;
                if (remaining < 0) remaining = 0;

                tvElapsed.setText(formatTime(elapsed));
                tvRemaining.setText(formatTime(remaining));

                distanceKm = (elapsed / 1000.0 / 3600.0) * 12.0;
                tvDistance.setText(String.format("%.1f", distanceKm));

                if (remaining > 0) {
                    handler.postDelayed(this, 1000);
                } else {
                    Toast.makeText(TrackRideActivity.this,
                            "Rental time expired! Please return the bike.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startLocationSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                tvLocation.setText("📍 " + locations[locationIndex % locations.length]);
                locationIndex++;
                handler.postDelayed(this, 8000);
            }
        }, 2000);
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void endRide() {
        running = false;
        handler.removeCallbacksAndMessages(null);
        if (rentalId != -1) {
            new DatabaseHelper(this).updateRentalStatus(rentalId, "completed");
        }
        Toast.makeText(this,
                "Ride ended! " + String.format("%.1f", distanceKm) + " km covered!",
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        handler.removeCallbacksAndMessages(null);
    }
}