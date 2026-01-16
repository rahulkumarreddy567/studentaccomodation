package com.pandalodge.model;

public class Admin {
    private int id;
    private String name;
    private String email;
    private String password;

    public Admin(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Admin(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public boolean manageStudent(Student student) {
        return student != null;
    }

    public boolean manageRenter(Renter renter) {
        return renter != null;
    }

    public boolean manageAccommodation(Accommodation accommodation) {
        return accommodation != null;
    }

    public boolean manageFAQ(FAQ faq) {
        return faq != null;
    }

    @Override
    public String toString() {
        return name + " (Admin)";
    }
}

