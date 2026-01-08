package com.pandalodge.dao;

import com.pandalodge.model.Booking;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            // Create bookings table with all necessary columns
            s.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "accommodation_id INTEGER NOT NULL, " +
                    "start_date TEXT NOT NULL, " +
                    "end_date TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'PENDING', " +
                    "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");

            System.out.println("BookingDAO: bookings table initialized successfully");

            // Add created_at column if it doesn't exist (for existing databases)
            try {
                s.execute("ALTER TABLE bookings ADD COLUMN created_at TEXT DEFAULT CURRENT_TIMESTAMP");
            } catch (Exception ignored) {
                // Column already exists
            }

            // Add status column if it doesn't exist (for existing databases)
            try {
                s.execute("ALTER TABLE bookings ADD COLUMN status TEXT DEFAULT 'PENDING'");
            } catch (Exception ignored) {
                // Column already exists
            }
        } catch (Exception e) {
            System.err.println("BookingDAO.init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean create(int studentId, int accommodationId, String start, String end) {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (Connection c = DBConnection.getConnection()) {
            if (c == null) {
                System.err.println("BookingDAO.create: Database connection is null!");
                return false;
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO bookings(student_id, accommodation_id, start_date, end_date, status, created_at) VALUES(?,?,?,?,?,?)")) {
                ps.setInt(1, studentId);
                ps.setInt(2, accommodationId);
                ps.setString(3, start);
                ps.setString(4, end);
                ps.setString(5, "PENDING");
                ps.setString(6, createdAt);

                int rows = ps.executeUpdate();
                System.out.println("BookingDAO.create: Inserted " + rows + " row(s) for student=" + studentId
                        + ", accommodation=" + accommodationId);
                return rows > 0;
            }
        } catch (Exception e) {
            System.err.println("BookingDAO.create failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static List<Booking> findByStudent(int studentId) {
        List<Booking> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c
                        .prepareStatement("SELECT * FROM bookings WHERE student_id = ? ORDER BY id DESC")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Booking(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                            rs.getString("start_date"), rs.getString("end_date"), rs.getString("status"),
                            rs.getString("created_at")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM bookings ORDER BY id DESC")) {
            while (rs.next()) {
                list.add(new Booking(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                        rs.getString("start_date"), rs.getString("end_date"), rs.getString("status"),
                        rs.getString("created_at")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateStatus(int bookingId, String newStatus) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("UPDATE bookings SET status = ? WHERE id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Booking findById(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT * FROM bookings WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Booking(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                            rs.getString("start_date"), rs.getString("end_date"), rs.getString("status"),
                            rs.getString("created_at"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}











