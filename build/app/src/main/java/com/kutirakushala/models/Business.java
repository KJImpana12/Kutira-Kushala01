package com.kutirakushala.models;

import com.google.firebase.firestore.DocumentId;
import java.util.List;

public class Business {

    @DocumentId
    private String id;
    private String ownerName;
    private String businessName;
    private String category;       // Food, Craft, Textile, etc.
    private String location;
    private String phone;
    private String description;
    private String profileImageUrl;
    private String workshopImageUrl;
    private int weeklyCapacity;
    private int availableUnits;    // Capacity meter
    private boolean isAcceptingOrders;
    private List<String> skillTags;
    private long createdAt;

    // Required empty constructor for Firestore
    public Business() {}

    public Business(String ownerName, String businessName, String category,
                    String location, String phone, String description) {
        this.ownerName = ownerName;
        this.businessName = businessName;
        this.category = category;
        this.location = location;
        this.phone = phone;
        this.description = description;
        this.isAcceptingOrders = true;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getWorkshopImageUrl() { return workshopImageUrl; }
    public void setWorkshopImageUrl(String workshopImageUrl) { this.workshopImageUrl = workshopImageUrl; }

    public int getWeeklyCapacity() { return weeklyCapacity; }
    public void setWeeklyCapacity(int weeklyCapacity) { this.weeklyCapacity = weeklyCapacity; }

    public int getAvailableUnits() { return availableUnits; }
    public void setAvailableUnits(int availableUnits) { this.availableUnits = availableUnits; }

    public boolean isAcceptingOrders() { return isAcceptingOrders; }
    public void setAcceptingOrders(boolean acceptingOrders) { isAcceptingOrders = acceptingOrders; }

    public List<String> getSkillTags() { return skillTags; }
    public void setSkillTags(List<String> skillTags) { this.skillTags = skillTags; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
