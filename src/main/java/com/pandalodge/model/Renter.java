package com.pandalodge.model;

public class Renter {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;

    public Renter(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Renter(int id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    // Login validation
    public static boolean login(String email, String password) {
        return email != null && password != null;
    }

    // Accommodation management methods
    public boolean addAccommodation(com.pandalodge.model.Accommodation acc) {
        // Implementation to add accommodation
        return true;
    }

    public boolean updateAccommodation(com.pandalodge.model.Accommodation acc) {
        // Implementation to update accommodation
        return true;
    }

    public boolean removeAccommodation(com.pandalodge.model.Accommodation acc) {
        // Implementation to remove accommodation
        return true;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}











