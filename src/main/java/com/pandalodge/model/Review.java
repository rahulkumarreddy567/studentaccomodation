package com.pandalodge.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Review {
    private int id;
    private int studentId;
    private int accommodationId;
    private String reviewData;
    private String reviewDate;
    private int rating;
    private String studentName;

    public Review(int id, int studentId, int accommodationId, String reviewData, String reviewDate, int rating) {
        this.id = id;
        this.studentId = studentId;
        this.accommodationId = accommodationId;
        this.reviewData = reviewData;
        this.reviewDate = reviewDate;
        this.rating = rating;
    }

    public Review(int id, int studentId, int accommodationId, String reviewData, String reviewDate, int rating, String studentName) {
        this(id, studentId, accommodationId, reviewData, reviewDate, rating);
        this.studentName = studentName;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getAccommodationId() { return accommodationId; }
    public String getReviewData() { return reviewData; }
    public String getReviewDate() { return reviewDate; }
    public int getRating() { return rating; }
    public String getStudentName() { return studentName; }

    public void setReviewData(String reviewData) { this.reviewData = reviewData; }
    public void setRating(int rating) { this.rating = rating; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getReview() {
        return reviewData;
    }

    public void setReview(String text) {
        this.reviewData = text;
    }

    public String getFormattedDate() {
        try {
            LocalDate date = LocalDate.parse(reviewDate);
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return reviewDate;
        }
    }

    public String getStarRating() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    @Override
    public String toString() {
        return getStarRating() + " - " + reviewData;
    }
}

