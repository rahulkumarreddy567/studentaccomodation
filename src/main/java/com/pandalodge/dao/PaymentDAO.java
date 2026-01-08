package com.pandalodge.dao;

import com.pandalodge.model.Payment;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS payments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "booking_id INTEGER NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "payment_method TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'PENDING', " +
                    "payment_date TEXT, " +
                    "transaction_id TEXT, " +
                    "FOREIGN KEY(student_id) REFERENCES students(id), " +
                    "FOREIGN KEY(booking_id) REFERENCES bookings(id))");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Payment create(int studentId, int bookingId, double amount, String paymentMethod) throws SQLException {
        String paymentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String transactionId = "TXN" + System.currentTimeMillis();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO payments(student_id, booking_id, amount, payment_method, status, payment_date, transaction_id) " +
                     "VALUES(?,?,?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookingId);
            ps.setDouble(3, amount);
            ps.setString(4, paymentMethod);
            ps.setString(5, "COMPLETED");
            ps.setString(6, paymentDate);
            ps.setString(7, transactionId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Payment(rs.getInt(1), studentId, bookingId, amount, paymentMethod,
                                      "COMPLETED", paymentDate, transactionId);
                }
            }
        }
        return null;
    }

    public static Payment findById(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM payments WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Payment(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("booking_id"),
                            rs.getDouble("amount"), rs.getString("payment_method"), rs.getString("status"),
                            rs.getString("payment_date"), rs.getString("transaction_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Payment findByBooking(int bookingId) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM payments WHERE booking_id = ?")) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Payment(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("booking_id"),
                            rs.getDouble("amount"), rs.getString("payment_method"), rs.getString("status"),
                            rs.getString("payment_date"), rs.getString("transaction_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Payment> findByStudent(int studentId) {
        List<Payment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM payments WHERE student_id = ? ORDER BY payment_date DESC")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Payment(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("booking_id"),
                            rs.getDouble("amount"), rs.getString("payment_method"), rs.getString("status"),
                            rs.getString("payment_date"), rs.getString("transaction_id")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM payments ORDER BY payment_date DESC")) {
            while (rs.next()) {
                list.add(new Payment(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("booking_id"),
                        rs.getDouble("amount"), rs.getString("payment_method"), rs.getString("status"),
                        rs.getString("payment_date"), rs.getString("transaction_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateStatus(int id, String newStatus) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE payments SET status = ? WHERE id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static double getTotalRevenue() {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT SUM(amount) as total FROM payments WHERE status = 'COMPLETED'")) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}












