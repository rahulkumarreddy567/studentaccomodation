package com.pandalodge.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Booking {
    private int id;
    private int studentId;
    private int accommodationId;
    private String startDate;
    private String endDate;
    private String status;
    private String createdAt;

    public Booking(int id, int studentId, int accommodationId, String startDate, String endDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.accommodationId = accommodationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Booking(int id, int studentId, int accommodationId, String startDate, String endDate, String status,
            String createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.accommodationId = accommodationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getAccommodationId() {
        return accommodationId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getDurationDays() {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return ChronoUnit.DAYS.between(start, end);
        } catch (Exception e) {
            return 0;
        }
    }

    public long getDurationMonths() {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return ChronoUnit.MONTHS.between(start, end);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getDurationFormatted() {
        long months = getDurationMonths();
        long days = getDurationDays();

        if (months > 0) {
            long remainingDays = days - (months * 30);
            if (remainingDays > 0) {
                return months + " month" + (months > 1 ? "s" : "") + ", " + remainingDays + " day"
                        + (remainingDays > 1 ? "s" : "");
            }
            return months + " month" + (months > 1 ? "s" : "");
        }
        return days + " day" + (days != 1 ? "s" : "");
    }

    public String getCreatedAtFormatted() {
        if (createdAt == null)
            return "N/A";
        try {
            LocalDateTime dt = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        } catch (Exception e) {
            return createdAt;
        }
    }
}

