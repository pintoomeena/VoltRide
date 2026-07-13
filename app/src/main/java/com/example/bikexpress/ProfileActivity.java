package com.example.bikexpress;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private ImageView ivAvatar;
    private TextView tvAvatar;

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_STORAGE_PERMISSION = 201;

    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvAvatar = findViewById(R.id.tv_avatar);

        loadUserData();
        loadSavedProfileImage();

        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());
        findViewById(R.id.menu_rentals).setOnClickListener(v ->
                startActivity(new Intent(this, MyRentalsActivity.class)));
        findViewById(R.id.menu_wallet).setOnClickListener(v ->
                startActivity(new Intent(this, WalletActivity.class)));
        findViewById(R.id.menu_logout).setOnClickListener(v -> confirmLogout());
        findViewById(R.id.tv_camera_btn).setOnClickListener(v -> showImagePickerDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    // ── Show picker dialog ───────────────────────────────────
    private void showImagePickerDialog() {
        String currentPath = prefs.getString("profileImagePath", null);
        String[] options;

        if (currentPath != null && new File(currentPath).exists()) {
            options = new String[]{"Take Photo", "Choose from Gallery", "Remove Photo", "Cancel"};
        } else {
            options = new String[]{"Take Photo", "Choose from Gallery", "Cancel"};
        }

        new AlertDialog.Builder(this)
                .setTitle("Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Take Photo")) {
                        checkCameraPermissionAndOpen();
                    } else if (options[which].equals("Choose from Gallery")) {
                        checkStoragePermissionAndOpen();
                    } else if (options[which].equals("Remove Photo")) {
                        removeProfileImage();
                    }
                })
                .show();
    }

    // ── Remove profile image ─────────────────────────────────
    private void removeProfileImage() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Photo")
                .setMessage("Are you sure you want to remove your profile picture?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    String path = prefs.getString("profileImagePath", null);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) file.delete();
                    }
                    prefs.edit().remove("profileImagePath").apply();
                    ivAvatar.setVisibility(View.GONE);
                    tvAvatar.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Profile picture removed",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Camera permission ────────────────────────────────────
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    // ── Storage permission ───────────────────────────────────
    private void checkStoragePermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    // ── Open Camera ──────────────────────────────────────────
    private void openCamera() {
        try {
            File imageFile = new File(getCacheDir(), "profile_temp.jpg");
            cameraImageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            Toast.makeText(this, "Camera error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Open Gallery ─────────────────────────────────────────
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    // ── Permission result ────────────────────────────────────
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CAMERA_PERMISSION) openCamera();
            else if (requestCode == REQUEST_STORAGE_PERMISSION) openGallery();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Activity result ──────────────────────────────────────
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        try {
            Bitmap bitmap = null;
            if (requestCode == REQUEST_CAMERA) {
                InputStream stream = getContentResolver()
                        .openInputStream(cameraImageUri);
                bitmap = BitmapFactory.decodeStream(stream);
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedUri = data.getData();
                InputStream stream = getContentResolver()
                        .openInputStream(selectedUri);
                bitmap = BitmapFactory.decodeStream(stream);
            }
            if (bitmap != null) {
                Bitmap circular = makeCircular(bitmap);
                saveProfileImage(circular);
                showProfileImage(circular);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Make bitmap circular ─────────────────────────────────
    private Bitmap makeCircular(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(
                Bitmap.createScaledBitmap(bitmap, size, size, true),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawOval(new RectF(0, 0, size, size), paint);
        return output;
    }

    // ── Save image ───────────────────────────────────────────
    private void saveProfileImage(Bitmap bitmap) {
        try {
            int userId = prefs.getInt("userId", -1);
            File file = new File(getFilesDir(), "profile_" + userId + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            resized.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
            prefs.edit().putString("profileImagePath",
                    file.getAbsolutePath()).apply();
            Toast.makeText(this, "Profile picture updated!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Load saved image ─────────────────────────────────────
    private void loadSavedProfileImage() {
        String path = prefs.getString("profileImagePath", null);
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                showProfileImage(bitmap);
            }
        }
    }

    // ── Show image ───────────────────────────────────────────
    private void showProfileImage(Bitmap bitmap) {
        ivAvatar.setImageBitmap(bitmap);
        ivAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setVisibility(View.GONE);
    }

    // ── Load user data ───────────────────────────────────────
    private void loadUserData() {
        String email = prefs.getString("userEmail", "");
        Cursor c = db.getUserByEmail(email);
        if (c.moveToFirst()) {
            String name = c.getString(
                    c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            int rides = c.getInt(
                    c.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL_RIDES));
            double km = c.getDouble(
                    c.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL_KM));
            double hours = c.getDouble(
                    c.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL_HOURS));

            ((TextView) findViewById(R.id.tv_profile_name)).setText(name);
            ((TextView) findViewById(R.id.tv_profile_email)).setText(email);
            ((TextView) findViewById(R.id.stat_rides)).setText(String.valueOf(rides));
            ((TextView) findViewById(R.id.stat_km)).setText(
                    String.format("%.0f", km));
            ((TextView) findViewById(R.id.stat_hours)).setText(
                    String.format("%.0f", hours));

            String badge;
            if (rides >= 50) badge = "Legend Rider";
            else if (rides >= 20) badge = "Pro Rider";
            else if (rides >= 5) badge = "Regular";
            else badge = "Newcomer";
            ((TextView) findViewById(R.id.tv_member_badge)).setText(badge);

            String initial = name.length() > 0
                    ? String.valueOf(name.charAt(0)).toUpperCase() : "R";
            tvAvatar.setText(initial);
        }
        c.close();
    }

    // ── Logout ───────────────────────────────────────────────
    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefs.edit()
                            .putBoolean("isLoggedIn", false)
                            .remove("userId")
                            .remove("userName")
                            .remove("userEmail")
                            .apply();
                    startActivity(new Intent(this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}