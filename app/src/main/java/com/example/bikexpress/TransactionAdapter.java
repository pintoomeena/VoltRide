package com.example.bikexpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    public static class TxModel {
        public String title, date, type;
        public double amount;

        public TxModel(String title, double amount, String type, String date) {
            this.title = title;
            this.amount = amount;
            this.type = type;
            this.date = date;
        }
    }

    private final Context ctx;
    private final List<TxModel> txList;

    public TransactionAdapter(Context ctx, List<TxModel> txList) {
        this.ctx = ctx;
        this.txList = txList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TxModel tx = txList.get(pos);
        h.title.setText(tx.title);
        h.date.setText(tx.date);
        boolean isDebit = "debit".equals(tx.type);
        h.amount.setText((isDebit ? "-" : "+") + "₹" + (int) tx.amount);
        h.amount.setTextColor(ctx.getColor(isDebit ? R.color.error : R.color.success));
        h.icon.setText(isDebit ? "🚴" : "💰");
    }

    @Override
    public int getItemCount() {
        return txList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView icon, title, date, amount;

        VH(View v) {
            super(v);
            icon = v.findViewById(R.id.tx_icon);
            title = v.findViewById(R.id.tx_title);
            date = v.findViewById(R.id.tx_date);
            amount = v.findViewById(R.id.tx_amount);
        }
    }
}