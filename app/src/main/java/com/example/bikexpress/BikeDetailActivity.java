package com.example.bikexpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BikeDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_detail);

        db = new DatabaseHelper(this);
        int bikeId = getIntent().getIntExtra("bikeId", -1);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        if (bikeId == -1) {
            finish();
            return;
        }

        Cursor c = db.getBikeById(bikeId);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_NAME));
            String desc = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_DESC));
            String cat = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_CATEGORY));
            double price = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_PRICE));
            double rating = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_RATING));
            int reviews = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_REVIEWS));
            String range = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_RANGE));
            String gears = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_GEARS));
            String weight = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_WEIGHT));
            boolean available = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_AVAILABLE)) == 1;

            ((TextView) findViewById(R.id.detail_bike_name)).setText(name);
            ((TextView) findViewById(R.id.detail_bike_category)).setText(cat + " Bike");
            ((TextView) findViewById(R.id.detail_description)).setText(desc);
            ((TextView) findViewById(R.id.detail_price)).setText("₹" + (int) price);
            ((TextView) findViewById(R.id.detail_rating)).setText(String.valueOf(rating));
            ((TextView) findViewById(R.id.detail_review_count)).setText("(" + reviews + " reviews)");
            ((TextView) findViewById(R.id.tv_max_range)).setText(range);
            ((TextView) findViewById(R.id.tv_gear)).setText(gears);
            ((TextView) findViewById(R.id.tv_weight)).setText(weight);

            TextView avail = findViewById(R.id.detail_availability);
            if (available) {
                avail.setText("● Available");
                avail.setTextColor(getColor(R.color.success));
            } else {
                avail.setText("● Unavailable");
                avail.setTextColor(getColor(R.color.error));
            }

            Button btnBook = findViewById(R.id.btn_book_now);
            btnBook.setOnClickListener(v -> {
                if (!available) {
                    Toast.makeText(this, "This bike is currently unavailable",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, BookingActivity.class);
                intent.putExtra("bikeId", bikeId);
                intent.putExtra("bikeName", name);
                intent.putExtra("bikeCategory", cat);
                intent.putExtra("bikePrice", price);
                startActivity(intent);
            });
        }
        c.close();
    }
}