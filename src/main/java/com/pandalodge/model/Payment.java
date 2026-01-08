package com.pandalodge.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    private int id;
    private int studentId;
    private int bookingId;
    private double amount;
    private String paymentMethod; // CARD, BANK_TRANSFER, CASH
    private String paymentDetails;
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private String paymentDate;
    private String transactionId;

    public Payment(int id, int studentId, int bookingId, double amount, String paymentMethod, String status) {
        this.id = id;
        this.studentId = studentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Payment(int id, int studentId, int bookingId, double amount, String paymentMethod,
                   String status, String paymentDate, String transactionId) {
        this.id = id;
        this.studentId = studentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = paymentDate;
        this.transactionId = transactionId;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentDetails() { return paymentDetails; }
    public String getStatus() { return status; }
    public String getPaymentDate() { return paymentDate; }
    public String getTransactionId() { return transactionId; }

    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }
    public void setStatus(String status) { this.status = status; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    // Payment operations
    public boolean makePayment() {
        this.status = "COMPLETED";
        this.transactionId = "TXN" + System.currentTimeMillis();
        return true;
    }

    public String getPaymentDetailsFormatted() {
        return String.format("Amount: €%.2f | Method: %s | Status: %s", amount, paymentMethod, status);
    }

    public String getFormattedDate() {
        try {
            LocalDateTime dt = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        } catch (Exception e) {
            return paymentDate;
        }
    }

    @Override
    public String toString() {
        return String.format("Payment #%d - €%.2f (%s)", id, amount, status);
    }
}











