package com.kutirakushala.models;

import com.google.firebase.firestore.DocumentId;

public class Product {

    @DocumentId
    private String id;
    private String businessId;
    private String name;
    private String description;
    private double bulkPrice;      // Price per unit for bulk orders
    private int minimumOrder;      // Minimum units for bulk order
    private String unit;           // "piece", "dozen", "kg", etc.
    private String imageUrl;
    private String category;
    private int dailyCapacity;     // How many can be made per day
    private boolean isAvailable;
    private long createdAt;

    // Required empty constructor for Firestore
    public Product() {}

    public Product(String businessId, String name, String description,
                   double bulkPrice, int minimumOrder, String unit, int dailyCapacity) {
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.bulkPrice = bulkPrice;
        this.minimumOrder = minimumOrder;
        this.unit = unit;
        this.dailyCapacity = dailyCapacity;
        this.isAvailable = true;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBulkPrice() { return bulkPrice; }
    public void setBulkPrice(double bulkPrice) { this.bulkPrice = bulkPrice; }

    public int getMinimumOrder() { return minimumOrder; }
    public void setMinimumOrder(int minimumOrder) { this.minimumOrder = minimumOrder; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDailyCapacity() { return dailyCapacity; }
    public void setDailyCapacity(int dailyCapacity) { this.dailyCapacity = dailyCapacity; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
