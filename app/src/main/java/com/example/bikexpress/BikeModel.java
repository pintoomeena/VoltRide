package com.example.bikexpress;

public class BikeModel {
    public int id;
    public String name;
    public String description;
    public String category;
    public double pricePerHour;
    public double rating;
    public int reviewCount;
    public String maxRange;
    public String gears;
    public String weight;
    public boolean isAvailable;

    public BikeModel(int id, String name, String description, String category,
                     double pricePerHour, double rating, int reviewCount,
                     String maxRange, String gears, String weight, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.pricePerHour = pricePerHour;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.maxRange = maxRange;
        this.gears = gears;
        this.weight = weight;
        this.isAvailable = isAvailable;
    }
}