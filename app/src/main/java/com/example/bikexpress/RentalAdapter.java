package com.example.bikexpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.VH> {

    public static class RentalModel {
        public int rentalId;
        public String bikeName, bookingRef, startTime, endTime, status;
        public double totalCost;

        public RentalModel(int rentalId, String bikeName, String bookingRef,
                           String startTime, String endTime, double totalCost, String status) {
            this.rentalId = rentalId;
            this.bikeName = bikeName;
            this.bookingRef = bookingRef;
            this.startTime = startTime;
            this.endTime = endTime;
            this.totalCost = totalCost;
            this.status = status;
        }
    }

    private final Context ctx;
    private final List<RentalModel> rentals;

    public RentalAdapter(Context ctx, List<RentalModel> rentals) {
        this.ctx = ctx;
        this.rentals = rentals;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_rental_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RentalModel r = rentals.get(pos);
        h.bikeName.setText(r.bikeName);
        h.bookingId.setText(r.bookingRef);
        h.start.setText(r.startTime);
        h.end.setText(r.endTime);
        h.cost.setText("₹" + (int) r.totalCost);

        boolean active = "active".equals(r.status);
        h.status.setText(active ? "Active" : "Completed");
        h.status.setTextColor(ctx.getColor(active ? R.color.success : R.color.text_secondary));
        h.status.setBackgroundResource(active ?
                R.drawable.bg_available_badge : R.drawable.bg_chip_unselected);
    }

    @Override
    public int getItemCount() {
        return rentals.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView bikeName, bookingId, start, end, cost, status;

        VH(View v) {
            super(v);
            bikeName = v.findViewById(R.id.rental_bike_name);
            bookingId = v.findViewById(R.id.rental_booking_id);
            start = v.findViewById(R.id.rental_start);
            end = v.findViewById(R.id.rental_end);
            cost = v.findViewById(R.id.rental_cost);
            status = v.findViewById(R.id.rental_status);
        }
    }
}