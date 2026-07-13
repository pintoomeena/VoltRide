package com.example.bikexpress;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private TextInputEditText etStartTime, etEndTime, etLocation;
    private int selectedDurationHrs = 1;
    private String paymentMethod = "Wallet";
    private int bikeId;
    private String bikeName, bikeCategory;
    private double bikePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        bikeId = getIntent().getIntExtra("bikeId", -1);
        bikeName = getIntent().getStringExtra("bikeName");
        bikeCategory = getIntent().getStringExtra("bikeCategory");
        bikePrice = getIntent().getDoubleExtra("bikePrice", 50);

        if (bikeName == null) {
            Cursor c = db.getBikeById(bikeId);
            if (c.moveToFirst()) {
                bikeName = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_NAME));
                bikeCategory = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_CATEGORY));
                bikePrice = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_PRICE));
            }
            c.close();
        }

        etStartTime = findViewById(R.id.et_start_time);
        etEndTime = findViewById(R.id.et_end_time);
        etLocation = findViewById(R.id.et_location);

        ((TextView) findViewById(R.id.booking_bike_name)).setText(bikeName);
        ((TextView) findViewById(R.id.booking_bike_category)).setText(bikeCategory);
        ((TextView) findViewById(R.id.booking_bike_price)).setText("₹" + (int) bikePrice + "/hr");

        setDefaultTimes();
        updateCostBreakdown();
        setupDurationChips();
        setupPaymentChips();

        etStartTime.setOnClickListener(v -> pickDateTime(true));
        etEndTime.setOnClickListener(v -> pickDateTime(false));

        findViewById(R.id.btn_back_booking).setOnClickListener(v -> finish());
        findViewById(R.id.btn_confirm_booking).setOnClickListener(v -> confirmBooking());
    }

    private void setDefaultTimes() {
        Calendar now = Calendar.getInstance();
        etStartTime.setText(formatDateTime(now));
        now.add(Calendar.HOUR_OF_DAY, 1);
        etEndTime.setText(formatDateTime(now));
    }

    private String formatDateTime(Calendar cal) {
        return String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));
    }

    private void pickDateTime(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                new TimePickerDialog(this, (view2, hour, minute) -> {
                    String dt = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d %02d:%02d", day, month + 1, year, hour, minute);
                    if (isStart) etStartTime.setText(dt);
                    else etEndTime.setText(dt);
                    updateCostBreakdown();
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show(),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupDurationChips() {
        int[] chipIds = {R.id.chip_1hr, R.id.chip_2hr, R.id.chip_4hr, R.id.chip_1day};
        int[] durations = {1, 2, 4, 24};

        for (int i = 0; i < chipIds.length; i++) {
            final int dur = durations[i];
            final int chipId = chipIds[i];
            TextView chip = findViewById(chipId);
            chip.setOnClickListener(v -> {
                selectedDurationHrs = dur;
                updateCostBreakdown();
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

    private void setupPaymentChips() {
        TextView payWallet = findViewById(R.id.pay_wallet);
        TextView payCash = findViewById(R.id.pay_cash);

        payWallet.setOnClickListener(v -> {
            paymentMethod = "Wallet";
            payWallet.setBackgroundResource(R.drawable.bg_chip_selected);
            payWallet.setTextColor(getColor(R.color.white));
            payCash.setBackgroundResource(R.drawable.bg_chip_unselected);
            payCash.setTextColor(getColor(R.color.text_secondary));
        });

        payCash.setOnClickListener(v -> {
            paymentMethod = "Cash";
            payCash.setBackgroundResource(R.drawable.bg_chip_selected);
            payCash.setTextColor(getColor(R.color.white));
            payWallet.setBackgroundResource(R.drawable.bg_chip_unselected);
            payWallet.setTextColor(getColor(R.color.text_secondary));
        });
    }

    private void updateCostBreakdown() {
        double base = bikePrice * selectedDurationHrs;
        double fee = Math.round(base * 0.10);
        double total = base + fee;

        ((TextView) findViewById(R.id.tv_base_cost)).setText("₹" + (int) base);
        ((TextView) findViewById(R.id.tv_service_fee)).setText("₹" + (int) fee);
        ((TextView) findViewById(R.id.tv_total_cost)).setText("₹" + (int) total);
    }

    private void confirmBooking() {
        String location = etLocation.getText().toString().trim();
        if (location.isEmpty()) {
            etLocation.setError("Location required");
            return;
        }

        int userId = prefs.getInt("userId", -1);
        double base = bikePrice * selectedDurationHrs;
        double fee = Math.round(base * 0.10);
        double total = base + fee;

        if (paymentMethod.equals("Wallet")) {
            double balance = db.getWalletBalance(userId);
            if (balance < total) {
                Toast.makeText(this, "Insufficient wallet balance. Please add Rs." +
                        (int)(total - balance) + " more.", Toast.LENGTH_LONG).show();
                return;
            }
            db.updateWallet(userId, balance - total);
            db.addTransaction(userId, "Bike Rental - " + bikeName, total, "debit");
        }

        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        String bookingRef = "#BX" + (System.currentTimeMillis() % 100000);

        long rentalId = db.addRental(userId, bikeId, startTime, endTime,
                selectedDurationHrs, total, location, bookingRef);

        if (rentalId != -1) {
            db.updateUserStats(userId, 1, selectedDurationHrs * 5.0, selectedDurationHrs);
            Intent intent = new Intent(this, BookingConfirmActivity.class);
            intent.putExtra("bookingRef", bookingRef);
            intent.putExtra("bikeName", bikeName);
            intent.putExtra("duration", selectedDurationHrs == 24 ?
                    "1 day" : selectedDurationHrs + " hours");
            intent.putExtra("total", total);
            intent.putExtra("rentalId", (int) rentalId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Booking failed, please try again",
                    Toast.LENGTH_SHORT).show();
        }
    }
}