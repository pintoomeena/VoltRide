package com.example.bikexpress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);

        String bookingRef = getIntent().getStringExtra("bookingRef");
        String bikeName = getIntent().getStringExtra("bikeName");
        String duration = getIntent().getStringExtra("duration");
        double total = getIntent().getDoubleExtra("total", 0);
        int rentalId = getIntent().getIntExtra("rentalId", -1);

        TextView checkmark = findViewById(R.id.checkmark);
        checkmark.animate()
                .scaleX(1.2f).scaleY(1.2f).setDuration(300)
                .withEndAction(() ->
                        checkmark.animate().scaleX(1f).scaleY(1f)
                                .setDuration(200).start())
                .start();

        ((TextView) findViewById(R.id.tv_booking_id))
                .setText(bookingRef != null ? bookingRef : "#BX00001");
        ((TextView) findViewById(R.id.tv_confirm_bike))
                .setText(bikeName != null ? bikeName : "Bike");
        ((TextView) findViewById(R.id.tv_confirm_duration))
                .setText(duration != null ? duration : "1 hour");
        ((TextView) findViewById(R.id.tv_confirm_total))
                .setText("₹" + (int) total);

        Button btnTrack = findViewById(R.id.btn_track_ride);
        Button btnHome = findViewById(R.id.btn_go_home);

        btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackRideActivity.class);
            intent.putExtra("rentalId", rentalId);
            intent.putExtra("bikeName", bikeName);
            intent.putExtra("duration", duration);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}