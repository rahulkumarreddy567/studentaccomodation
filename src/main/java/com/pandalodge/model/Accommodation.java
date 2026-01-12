package com.pandalodge.model;

public class Accommodation {
    private int id;
    private String type; // e.g., Room, Studio, Apartment
    private String size; // e.g., 18m2
    private double price;
    private String address;
    private String imageUrl;
    private boolean furnished;
    private String description;
    private String status; // AVAILABLE, BOOKED
    private double latitude;
    private double longitude;
    private int ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

    public Accommodation(int id, String type, double price, String address, String imageUrl, boolean furnished,
            String description, String status, double latitude, double longitude, String size) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.address = address;
        this.imageUrl = imageUrl;
        this.furnished = furnished;
        this.description = description;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.size = size;
    }

    public Accommodation(int id, String type, double price, String address, String imageUrl, boolean furnished,
            String description, String status, double latitude, double longitude,
            int ownerId, String ownerName, String ownerEmail, String ownerPhone, String size) {
        this(id, type, price, address, imageUrl, furnished, description, status, latitude, longitude, size);
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.ownerPhone = ownerPhone;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size != null ? size : "15m²";
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public boolean hasOwner() {
        return ownerName != null && !ownerName.isEmpty();
    }

    public String getOwnerContactInfo() {
        if (!hasOwner())
            return "Contact information not available";
        StringBuilder sb = new StringBuilder();
        sb.append(ownerName);
        if (ownerEmail != null && !ownerEmail.isEmpty())
            sb.append(" | ").append(ownerEmail);
        if (ownerPhone != null && !ownerPhone.isEmpty())
            sb.append(" | ").append(ownerPhone);
        return sb.toString();
    }

    @Override
    public String toString() {
        return type + " - " + address;
    }

    // Backward compatibility for rent vs price
    public double getRent() {
        return price;
    }
}
