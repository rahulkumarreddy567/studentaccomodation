package com.pandalodge.dao;

import com.pandalodge.model.Review;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "accommodation_id INTEGER NOT NULL, " +
                    "review_data TEXT NOT NULL, " +
                    "review_date TEXT NOT NULL, " +
                    "rating INTEGER DEFAULT 5, " +
                    "FOREIGN KEY(student_id) REFERENCES students(id), " +
                    "FOREIGN KEY(accommodation_id) REFERENCES accommodations(id))");

            // Seed sample reviews if empty
            ResultSet rs = s.executeQuery("SELECT count(*) FROM reviews");
            if (rs.next() && rs.getInt(1) == 0) {
                seedData(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedData(Statement s) throws SQLException {
        // Sample reviews for accommodations
        s.execute("INSERT INTO reviews(student_id, accommodation_id, review_data, review_date, rating) VALUES " +
                "(1, 1, 'Great location near the university! The room was clean and cozy. Highly recommend for students.', '2025-10-15', 5)");
        s.execute("INSERT INTO reviews(student_id, accommodation_id, review_data, review_date, rating) VALUES " +
                "(1, 2, 'Nice area with good transport links. The landlord was very helpful.', '2025-09-20', 4)");
        s.execute("INSERT INTO reviews(student_id, accommodation_id, review_data, review_date, rating) VALUES " +
                "(1, 9, 'Beautiful studio in a perfect location. A bit pricey but worth it!', '2025-11-05', 5)");
    }

    public static Review create(int studentId, int accommodationId, String reviewData, int rating) throws SQLException {
        String reviewDate = LocalDate.now().toString();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO reviews(student_id, accommodation_id, review_data, review_date, rating) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, accommodationId);
            ps.setString(3, reviewData);
            ps.setString(4, reviewDate);
            ps.setInt(5, rating);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Review(rs.getInt(1), studentId, accommodationId, reviewData, reviewDate, rating);
                }
            }
        }
        return null;
    }

    public static List<Review> findByAccommodation(int accommodationId) {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT r.*, s.name as student_name FROM reviews r " +
                                "LEFT JOIN students s ON r.student_id = s.id " +
                                "WHERE r.accommodation_id = ? ORDER BY r.review_date DESC")) {
            ps.setInt(1, accommodationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                            rs.getString("review_data"), rs.getString("review_date"), rs.getInt("rating"),
                            rs.getString("student_name"));
                    list.add(review);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Review> findByStudent(int studentId) {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM reviews WHERE student_id = ? ORDER BY review_date DESC")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Review(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                            rs.getString("review_data"), rs.getString("review_date"), rs.getInt("rating")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Review> findAll() {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(
                        "SELECT r.*, s.name as student_name FROM reviews r " +
                                "LEFT JOIN students s ON r.student_id = s.id ORDER BY r.review_date DESC")) {
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("id"), rs.getInt("student_id"), rs.getInt("accommodation_id"),
                        rs.getString("review_data"), rs.getString("review_date"), rs.getInt("rating"),
                        rs.getString("student_name"));
                list.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static double getAverageRating(int accommodationId) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT AVG(rating) as avg_rating FROM reviews WHERE accommodation_id = ?")) {
            ps.setInt(1, accommodationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(int id, String reviewData, int rating) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE reviews SET review_data = ?, rating = ? WHERE id = ?")) {
            ps.setString(1, reviewData);
            ps.setInt(2, rating);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }
}
