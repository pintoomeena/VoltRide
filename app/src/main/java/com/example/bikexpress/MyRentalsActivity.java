package com.example.bikexpress;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyRentalsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private RentalAdapter adapter;
    private List<RentalAdapter.RentalModel> rentalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rentals);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        RecyclerView recycler = findViewById(R.id.rentals_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RentalAdapter(this, rentalList);
        recycler.setAdapter(adapter);

        TextView tabActive = findViewById(R.id.tab_active);
        TextView tabHistory = findViewById(R.id.tab_history);

        tabActive.setOnClickListener(v -> {
            tabActive.setBackgroundResource(R.drawable.bg_chip_selected);
            tabActive.setTextColor(getColor(R.color.white));
            tabHistory.setBackgroundResource(R.drawable.bg_chip_unselected);
            tabHistory.setTextColor(getColor(R.color.text_secondary));
            loadRentals(true);
        });

        tabHistory.setOnClickListener(v -> {
            tabHistory.setBackgroundResource(R.drawable.bg_chip_selected);
            tabHistory.setTextColor(getColor(R.color.white));
            tabActive.setBackgroundResource(R.drawable.bg_chip_unselected);
            tabActive.setTextColor(getColor(R.color.text_secondary));
            loadRentals(false);
        });

        findViewById(R.id.btn_back_rentals).setOnClickListener(v -> finish());

        loadRentals(true);
    }

    private void loadRentals(boolean activeOnly) {
        int userId = prefs.getInt("userId", -1);
        rentalList.clear();

        Cursor c = db.getRentalsByUser(userId);
        while (c.moveToNext()) {
            String status = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS));
            boolean isActive = "active".equals(status);
            if (activeOnly && !isActive) continue;
            if (!activeOnly && isActive) continue;

            rentalList.add(new RentalAdapter.RentalModel(
                    c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_RENTAL_ID)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BIKE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_REF)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_START_TIME)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_END_TIME)),
                    c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL_COST)),
                    status
            ));
        }
        c.close();

        TextView tvEmpty = findViewById(R.id.tv_empty);
        RecyclerView recycler = findViewById(R.id.rentals_recycler);

        if (rentalList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            tvEmpty.setText(activeOnly
                    ? "No active rentals\nBook a bike now!"
                    : "No rental history yet\nStart your first ride!");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}