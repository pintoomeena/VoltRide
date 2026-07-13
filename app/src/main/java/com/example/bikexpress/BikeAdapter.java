package com.example.bikexpress;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeVH> {

    private final Context context;
    private final List<BikeModel> bikes;

    public BikeAdapter(Context context, List<BikeModel> bikes) {
        this.context = context;
        this.bikes = bikes;
    }

    @NonNull
    @Override
    public BikeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_bike_card, parent, false);
        return new BikeVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeVH h, int position) {
        BikeModel bike = bikes.get(position);
        h.name.setText(bike.name);
        h.category.setText(bike.category + " Bike");
        h.rating.setText(String.valueOf(bike.rating));
        h.price.setText("₹" + (int) bike.pricePerHour + "/hr");

        if (bike.isAvailable) {
            h.availability.setText("● Available");
            h.availability.setTextColor(context.getColor(R.color.success));
        } else {
            h.availability.setText("● Unavailable");
            h.availability.setTextColor(context.getColor(R.color.error));
        }

        h.btnBook.setEnabled(bike.isAvailable);
        h.btnBook.setAlpha(bike.isAvailable ? 1f : 0.5f);

        h.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, BikeDetailActivity.class);
            intent.putExtra("bikeId", bike.id);
            context.startActivity(intent);
        });

        h.btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("bikeId", bike.id);
            intent.putExtra("bikeName", bike.name);
            intent.putExtra("bikeCategory", bike.category);
            intent.putExtra("bikePrice", bike.pricePerHour);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bikes.size();
    }

    static class BikeVH extends RecyclerView.ViewHolder {
        CardView card;
        TextView name, category, rating, price, availability;
        Button btnBook;

        BikeVH(View v) {
            super(v);
            card = (CardView) v;
            name = v.findViewById(R.id.tv_bike_name);
            category = v.findViewById(R.id.tv_bike_category);
            rating = v.findViewById(R.id.tv_rating);
            price = v.findViewById(R.id.tv_price);
            availability = v.findViewById(R.id.tv_availability);
            btnBook = v.findViewById(R.id.btn_book);
        }
    }
}