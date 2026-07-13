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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }

        if (db.loginUser(email, password)) {
            Cursor c = db.getUserByEmail(email);
            if (c.moveToFirst()) {
                int userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                String name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                SharedPreferences.Editor editor =
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("userId", userId);
                editor.putString("userName", name);
                editor.putString("userEmail", email);
                editor.apply();
            }
            c.close();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}