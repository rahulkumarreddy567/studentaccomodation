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

            boolean migrationNeeded = false;
            try (ResultSet rs = s.executeQuery("PRAGMA table_info(bookings)")) {
                boolean hasRoomId = false;
                boolean hasAccommodationId = false;
                while (rs.next()) {
                    String name = rs.getString("name");
                    if ("room_id".equalsIgnoreCase(name))
                        hasRoomId = true;
                    if ("accommodation_id".equalsIgnoreCase(name))
                        hasAccommodationId = true;
                }
                if (hasRoomId && !hasAccommodationId)
                    migrationNeeded = true;
            } catch (Exception e) {
            }

            if (migrationNeeded) {
                s.execute("BEGIN TRANSACTION");
                try {
                    s.execute("ALTER TABLE bookings RENAME TO bookings_old");
                    s.execute("CREATE TABLE bookings (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "student_id INTEGER NOT NULL, " +
                            "accommodation_id INTEGER NOT NULL, " +
                            "start_date TEXT NOT NULL, " +
                            "end_date TEXT NOT NULL, " +
                            "status TEXT DEFAULT 'PENDING', " +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");

                    s.execute(
                            "INSERT INTO bookings (id, student_id, accommodation_id, start_date, end_date, status, created_at) "
                                    +
                                    "SELECT id, student_id, room_id, start_date, end_date, 'PENDING', CURRENT_TIMESTAMP FROM bookings_old");

                    s.execute("DROP TABLE bookings_old");
                    s.execute("COMMIT");
                } catch (SQLException ex) {
                    s.execute("ROLLBACK");
                    System.err.println("BookingDAO: Migration FAILED: " + ex.getMessage());
                }
            } else {
                s.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "student_id INTEGER NOT NULL, " +
                        "accommodation_id INTEGER NOT NULL, " +
                        "start_date TEXT NOT NULL, " +
                        "end_date TEXT NOT NULL, " +
                        "status TEXT DEFAULT 'PENDING', " +
                        "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");

                boolean missingCritical = false;
                try (ResultSet rsInfo = s.executeQuery("PRAGMA table_info(bookings)")) {
                    java.util.Set<String> existingCols = new java.util.HashSet<>();
                    while (rsInfo.next())
                        existingCols.add(rsInfo.getString("name").toLowerCase());

                    if (!existingCols.contains("accommodation_id") ||
                            !existingCols.contains("status") ||
                            !existingCols.contains("created_at")) {
                        missingCritical = true;
                    }
                }

                if (missingCritical) {
                    s.execute("BEGIN TRANSACTION");
                    try {
                        s.execute("ALTER TABLE bookings RENAME TO bookings_temp_fix");
                        s.execute("CREATE TABLE bookings (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "student_id INTEGER NOT NULL, " +
                                "accommodation_id INTEGER NOT NULL, " +
                                "start_date TEXT NOT NULL, " +
                                "end_date TEXT NOT NULL, " +
                                "status TEXT DEFAULT 'PENDING', " +
                                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");

                        String select = "SELECT id, student_id, ";
                        try (ResultSet rsTemp = s.executeQuery("PRAGMA table_info(bookings_temp_fix)")) {
                            boolean hasRoomId = false;
                            boolean hasAccId = false;
                            while (rsTemp.next()) {
                                String n = rsTemp.getString("name").toLowerCase();
                                if (n.equals("room_id"))
                                    hasRoomId = true;
                                if (n.equals("accommodation_id"))
                                    hasAccId = true;
                            }
                            if (hasAccId)
                                select += "accommodation_id, ";
                            else if (hasRoomId)
                                select += "room_id, ";
                            else
                                select += "0, ";
                        }

                        select += "start_date, end_date, 'PENDING', CURRENT_TIMESTAMP FROM bookings_temp_fix";
                        s.execute(
                                "INSERT INTO bookings (id, student_id, accommodation_id, start_date, end_date, status, created_at) "
                                        + select);

                        s.execute("DROP TABLE bookings_temp_fix");
                        s.execute("COMMIT");
                    } catch (SQLException ex) {
                        s.execute("ROLLBACK");
                        System.err.println("BookingDAO: Recreation FAILED: " + ex.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("BookingDAO.init CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean create(int studentId, int accommodationId, String startDate, String endDate, String status) {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection c = DBConnection.getConnection()) {
            if (c == null) {
                System.err.println("BookingDAO.create FAILURE: Connection is NULL");
                return false;
            }

            String sql = "INSERT INTO bookings(student_id, accommodation_id, start_date, end_date, status, created_at) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, accommodationId);
                ps.setString(3, startDate);
                ps.setString(4, endDate);
                ps.setString(5, status);
                ps.setString(6, createdAt);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    return true;
                } else {
                    System.err.println("BookingDAO.create FAILURE: executeUpdate returned 0 rows");
                }
            }
        } catch (SQLException e) {
            System.err.println("BookingDAO.create SQL CRITICAL ERROR: [" + e.getErrorCode() + "] " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("BookingDAO.create UNEXPECTED ERROR: " + e.getMessage());
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

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM bookings WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
