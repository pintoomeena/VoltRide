package com.example.bikexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etPassword;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_reg_password);

        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) { etName.setError("Name required"); return; }
        if (email.isEmpty()) { etEmail.setError("Email required"); return; }
        if (phone.isEmpty()) { etPhone.setError("Phone required"); return; }
        if (password.length() < 6) { etPassword.setError("Min 6 characters"); return; }

        if (db.registerUser(name, email, phone, password)) {
            Cursor c = db.getUserByEmail(email);
            if (c.moveToFirst()) {
                int userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                SharedPreferences.Editor editor =
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("userId", userId);
                editor.putString("userName", name);
                editor.putString("userEmail", email);
                editor.apply();
                db.addTransaction(userId, "Welcome Bonus", 500, "credit");
            }
            c.close();
            Toast.makeText(this, "Welcome! Rs.500 added to wallet!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
        }
    }
}