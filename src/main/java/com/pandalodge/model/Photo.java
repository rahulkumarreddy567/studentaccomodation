package com.pandalodge.model;

public class Photo {
    private int id;
    private int accommodationId;
    private String imageUrl;
    private String caption;
    private boolean isPrimary;

    public Photo(int id, int accommodationId, String imageUrl) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl;
        this.isPrimary = false;
    }

    public Photo(int id, int accommodationId, String imageUrl, String caption, boolean isPrimary) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.isPrimary = isPrimary;
    }

    public int getId() { return id; }
    public int getAccommodationId() { return accommodationId; }
    public String getImageUrl() { return imageUrl; }
    public String getCaption() { return caption; }
    public boolean isPrimary() { return isPrimary; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    // Upload and delete methods
    public boolean uploadPhoto(String img) {
        this.imageUrl = img;
        return true;
    }

    public boolean deletePhoto() {
        this.imageUrl = null;
        return true;
    }

    @Override
    public String toString() {
        return caption != null ? caption : "Photo " + id;
    }
}











