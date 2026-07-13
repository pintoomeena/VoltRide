package com.example.bikexpress;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private int userId;
    private TextView tvBalance;
    private TextInputEditText etCustomAmount;
    private TransactionAdapter txAdapter;
    private List<TransactionAdapter.TxModel> txList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        tvBalance = findViewById(R.id.tv_balance);
        etCustomAmount = findViewById(R.id.et_custom_amount);

        RecyclerView recycler = findViewById(R.id.transactions_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        txAdapter = new TransactionAdapter(this, txList);
        recycler.setAdapter(txAdapter);

        refreshBalance();
        loadTransactions();

        findViewById(R.id.btn_back_wallet).setOnClickListener(v -> finish());

        findViewById(R.id.add_100).setOnClickListener(v -> addMoney(100));
        findViewById(R.id.add_200).setOnClickListener(v -> addMoney(200));
        findViewById(R.id.add_500).setOnClickListener(v -> addMoney(500));

        Button btnAdd = findViewById(R.id.btn_add_money);
        btnAdd.setOnClickListener(v -> {
            String input = etCustomAmount.getText().toString().trim();
            if (input.isEmpty()) {
                etCustomAmount.setError("Enter amount");
                return;
            }
            try {
                double amt = Double.parseDouble(input);
                if (amt < 1) {
                    etCustomAmount.setError("Min Rs.1");
                    return;
                }
                addMoney(amt);
                etCustomAmount.setText("");
            } catch (NumberFormatException e) {
                etCustomAmount.setError("Invalid amount");
            }
        });
    }

    private void addMoney(double amount) {
        double current = db.getWalletBalance(userId);
        db.updateWallet(userId, current + amount);
        db.addTransaction(userId, "Wallet Top-up", amount, "credit");
        refreshBalance();
        loadTransactions();
        Toast.makeText(this, "Rs." + (int) amount + " added!", Toast.LENGTH_SHORT).show();
    }

    private void refreshBalance() {
        double bal = db.getWalletBalance(userId);
        tvBalance.setText(String.format("₹%.2f", bal));
    }

    private void loadTransactions() {
        txList.clear();
        Cursor c = db.getTransactionsByUser(userId);
        while (c.moveToNext()) {
            txList.add(new TransactionAdapter.TxModel(
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_TITLE)),
                    c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_AMOUNT)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_TYPE)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_DATE))
            ));
        }
        c.close();
        txAdapter.notifyDataSetChanged();
    }
}