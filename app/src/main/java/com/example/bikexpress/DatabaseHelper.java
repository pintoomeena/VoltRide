package com.example.bikexpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bikexpress.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_PASSWORD = "password";
    public static final String COL_WALLET = "wallet_balance";
    public static final String COL_TOTAL_RIDES = "total_rides";
    public static final String COL_TOTAL_KM = "total_km";
    public static final String COL_TOTAL_HOURS = "total_hours";

    public static final String TABLE_BIKES = "bikes";
    public static final String COL_BIKE_ID = "bike_id";
    public static final String COL_BIKE_NAME = "bike_name";
    public static final String COL_BIKE_DESC = "description";
    public static final String COL_BIKE_CATEGORY = "category";
    public static final String COL_BIKE_PRICE = "price_per_hour";
    public static final String COL_BIKE_RATING = "rating";
    public static final String COL_BIKE_REVIEWS = "review_count";
    public static final String COL_BIKE_RANGE = "max_range";
    public static final String COL_BIKE_GEARS = "gears";
    public static final String COL_BIKE_WEIGHT = "weight";
    public static final String COL_BIKE_AVAILABLE = "is_available";

    public static final String TABLE_RENTALS = "rentals";
    public static final String COL_RENTAL_ID = "rental_id";
    public static final String COL_RENTAL_USER_ID = "user_id";
    public static final String COL_RENTAL_BIKE_ID = "bike_id";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";
    public static final String COL_DURATION_HRS = "duration_hrs";
    public static final String COL_TOTAL_COST = "total_cost";
    public static final String COL_LOCATION = "location";
    public static final String COL_STATUS = "status";
    public static final String COL_BOOKING_REF = "booking_ref";

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TX_ID = "tx_id";
    public static final String COL_TX_USER_ID = "tx_user_id";
    public static final String COL_TX_TITLE = "title";
    public static final String COL_TX_AMOUNT = "amount";
    public static final String COL_TX_TYPE = "type";
    public static final String COL_TX_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PHONE + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_WALLET + " REAL DEFAULT 500.0, " +
                COL_TOTAL_RIDES + " INTEGER DEFAULT 0, " +
                COL_TOTAL_KM + " REAL DEFAULT 0, " +
                COL_TOTAL_HOURS + " REAL DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_BIKES + " (" +
                COL_BIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BIKE_NAME + " TEXT, " +
                COL_BIKE_DESC + " TEXT, " +
                COL_BIKE_CATEGORY + " TEXT, " +
                COL_BIKE_PRICE + " REAL, " +
                COL_BIKE_RATING + " REAL DEFAULT 4.5, " +
                COL_BIKE_REVIEWS + " INTEGER DEFAULT 10, " +
                COL_BIKE_RANGE + " TEXT DEFAULT '30km', " +
                COL_BIKE_GEARS + " TEXT DEFAULT '21 Gears', " +
                COL_BIKE_WEIGHT + " TEXT DEFAULT '12 kg', " +
                COL_BIKE_AVAILABLE + " INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE " + TABLE_RENTALS + " (" +
                COL_RENTAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RENTAL_USER_ID + " INTEGER, " +
                COL_RENTAL_BIKE_ID + " INTEGER, " +
                COL_START_TIME + " TEXT, " +
                COL_END_TIME + " TEXT, " +
                COL_DURATION_HRS + " REAL, " +
                COL_TOTAL_COST + " REAL, " +
                COL_LOCATION + " TEXT, " +
                COL_STATUS + " TEXT DEFAULT 'active', " +
                COL_BOOKING_REF + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TX_USER_ID + " INTEGER, " +
                COL_TX_TITLE + " TEXT, " +
                COL_TX_AMOUNT + " REAL, " +
                COL_TX_TYPE + " TEXT, " +
                COL_TX_DATE + " TEXT)");

        insertSampleBikes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RENTALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    private void insertSampleBikes(SQLiteDatabase db) {
        insertBike(db, "Mountain Explorer Pro", "Premium mountain bike with hydraulic disc brakes and full suspension.", "Mountain", 80, 4.8, 124, "40km", "21 Gears", "12 kg");
        insertBike(db, "City Cruiser Elite", "Smooth city commuter with ergonomic seat and carrier rack.", "City", 50, 4.6, 89, "25km", "7 Gears", "10 kg");
        insertBike(db, "Thunder Electric", "Electric pedal-assist bike. 250W motor, 40km battery range.", "Electric", 120, 4.9, 203, "40km", "E-Assist", "18 kg");
        insertBike(db, "Road Racer X", "Lightweight road bike with carbon fork and drop handlebars.", "Road", 90, 4.7, 67, "60km", "18 Gears", "8 kg");
        insertBike(db, "BMX Stunt Beast", "Sturdy BMX bike for tricks and street riding.", "BMX", 60, 4.5, 45, "N/A", "Single", "11 kg");
        insertBike(db, "Hybrid Commuter", "Versatile hybrid for city streets and light trails.", "Hybrid", 70, 4.6, 91, "35km", "24 Gears", "13 kg");
        insertBike(db, "Gravel Crusher", "Gravel adventure bike for mixed terrain exploration.", "Mountain", 95, 4.7, 38, "50km", "22 Gears", "11 kg");
        insertBike(db, "Speed Demon", "High performance road bike built for speed.", "Road", 100, 4.8, 56, "70km", "22 Gears", "7 kg");
    }

    private void insertBike(SQLiteDatabase db, String name, String desc, String cat,
                            double price, double rating, int reviews,
                            String range, String gears, String weight) {
        ContentValues v = new ContentValues();
        v.put(COL_BIKE_NAME, name);
        v.put(COL_BIKE_DESC, desc);
        v.put(COL_BIKE_CATEGORY, cat);
        v.put(COL_BIKE_PRICE, price);
        v.put(COL_BIKE_RATING, rating);
        v.put(COL_BIKE_REVIEWS, reviews);
        v.put(COL_BIKE_RANGE, range);
        v.put(COL_BIKE_GEARS, gears);
        v.put(COL_BIKE_WEIGHT, weight);
        v.put(COL_BIKE_AVAILABLE, 1);
        db.insert(TABLE_BIKES, null, v);
    }

    public boolean registerUser(String name, String email, String phone, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_EMAIL, email);
        v.put(COL_PHONE, phone);
        v.put(COL_PASSWORD, password);
        v.put(COL_WALLET, 500.0);
        long result = db.insert(TABLE_USERS, null, v);
        db.close();
        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password});
        boolean found = c.getCount() > 0;
        c.close();
        db.close();
        return found;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=?", new String[]{email});
    }

    public boolean updateWallet(int userId, double newBalance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_WALLET, newBalance);
        int rows = db.update(TABLE_USERS, v, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public double getWalletBalance(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_WALLET + " FROM " + TABLE_USERS +
                " WHERE " + COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        double bal = 0;
        if (c.moveToFirst()) bal = c.getDouble(0);
        c.close();
        db.close();
        return bal;
    }

    public void updateUserStats(int userId, int ridesAdd, double kmAdd, double hrsAdd) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_USERS + " SET " +
                COL_TOTAL_RIDES + "=" + COL_TOTAL_RIDES + "+" + ridesAdd + ", " +
                COL_TOTAL_KM + "=" + COL_TOTAL_KM + "+" + kmAdd + ", " +
                COL_TOTAL_HOURS + "=" + COL_TOTAL_HOURS + "+" + hrsAdd +
                " WHERE " + COL_USER_ID + "=" + userId);
        db.close();
    }

    public Cursor getAllBikes() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_BIKES, null);
    }

    public Cursor getBikesByCategory(String category) {
        if (category.equals("All"))
            return getAllBikes();
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_BIKES +
                " WHERE " + COL_BIKE_CATEGORY + "=?", new String[]{category});
    }

    public Cursor getBikeById(int bikeId) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_BIKES +
                " WHERE " + COL_BIKE_ID + "=?", new String[]{String.valueOf(bikeId)});
    }

    public long addRental(int userId, int bikeId, String startTime, String endTime,
                          double durationHrs, double totalCost, String location, String bookingRef) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_RENTAL_USER_ID, userId);
        v.put(COL_RENTAL_BIKE_ID, bikeId);
        v.put(COL_START_TIME, startTime);
        v.put(COL_END_TIME, endTime);
        v.put(COL_DURATION_HRS, durationHrs);
        v.put(COL_TOTAL_COST, totalCost);
        v.put(COL_LOCATION, location);
        v.put(COL_STATUS, "active");
        v.put(COL_BOOKING_REF, bookingRef);
        long id = db.insert(TABLE_RENTALS, null, v);
        db.close();
        return id;
    }

    public Cursor getRentalsByUser(int userId) {
        return getReadableDatabase().rawQuery(
                "SELECT r.*, b." + COL_BIKE_NAME + " FROM " + TABLE_RENTALS + " r " +
                        "JOIN " + TABLE_BIKES + " b ON r." + COL_RENTAL_BIKE_ID + "=b." + COL_BIKE_ID +
                        " WHERE r." + COL_RENTAL_USER_ID + "=? ORDER BY r." + COL_RENTAL_ID + " DESC",
                new String[]{String.valueOf(userId)});
    }

    public boolean updateRentalStatus(int rentalId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_STATUS, status);
        int rows = db.update(TABLE_RENTALS, v, COL_RENTAL_ID + "=?",
                new String[]{String.valueOf(rentalId)});
        db.close();
        return rows > 0;
    }

    public boolean addTransaction(int userId, String title, double amount, String type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TX_USER_ID, userId);
        v.put(COL_TX_TITLE, title);
        v.put(COL_TX_AMOUNT, amount);
        v.put(COL_TX_TYPE, type);
        v.put(COL_TX_DATE, new java.text.SimpleDateFormat("dd MMM yyyy",
                java.util.Locale.getDefault()).format(new java.util.Date()));
        long result = db.insert(TABLE_TRANSACTIONS, null, v);
        db.close();
        return result != -1;
    }

    public Cursor getTransactionsByUser(int userId) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_TX_USER_ID + "=? ORDER BY " + COL_TX_ID + " DESC",
                new String[]{String.valueOf(userId)});
    }
}