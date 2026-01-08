package com.pandalodge.dao;

import com.pandalodge.model.Photo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS photos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "accommodation_id INTEGER NOT NULL, " +
                    "image_url TEXT NOT NULL, " +
                    "caption TEXT, " +
                    "is_primary INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(accommodation_id) REFERENCES accommodations(accommodation_id))");

            // Seed sample photos if empty
            ResultSet rs = s.executeQuery("SELECT count(*) FROM photos");
            if (rs.next() && rs.getInt(1) == 0) {
                seedData(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedData(Statement s) throws SQLException {
        // Additional photos for some accommodations
        s.execute("INSERT INTO photos(accommodation_id, image_url, caption, is_primary) VALUES " +
                "(1, 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800', 'Main room view', 1)");
        s.execute("INSERT INTO photos(accommodation_id, image_url, caption, is_primary) VALUES " +
                "(1, 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800', 'Shared kitchen', 0)");
        s.execute("INSERT INTO photos(accommodation_id, image_url, caption, is_primary) VALUES " +
                "(9, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800', 'Studio overview', 1)");
        s.execute("INSERT INTO photos(accommodation_id, image_url, caption, is_primary) VALUES " +
                "(9, 'https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800', 'Kitchen area', 0)");
    }

    public static Photo create(int accommodationId, String imageUrl, String caption, boolean isPrimary) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO photos(accommodation_id, image_url, caption, is_primary) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, accommodationId);
            ps.setString(2, imageUrl);
            ps.setString(3, caption);
            ps.setInt(4, isPrimary ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Photo(rs.getInt(1), accommodationId, imageUrl, caption, isPrimary);
                }
            }
        }
        return null;
    }

    public static List<Photo> findByAccommodation(int accommodationId) {
        List<Photo> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM photos WHERE accommodation_id = ? ORDER BY is_primary DESC")) {
            ps.setInt(1, accommodationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Photo(rs.getInt("id"), rs.getInt("accommodation_id"),
                            rs.getString("image_url"), rs.getString("caption"),
                            rs.getInt("is_primary") == 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Photo getPrimaryPhoto(int accommodationId) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM photos WHERE accommodation_id = ? AND is_primary = 1")) {
            ps.setInt(1, accommodationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Photo(rs.getInt("id"), rs.getInt("accommodation_id"),
                            rs.getString("image_url"), rs.getString("caption"), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM photos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setPrimary(int id, int accommodationId) {
        try (Connection c = DBConnection.getConnection()) {
            // First, unset all primary flags for this accommodation
            try (PreparedStatement ps1 = c.prepareStatement(
                    "UPDATE photos SET is_primary = 0 WHERE accommodation_id = ?")) {
                ps1.setInt(1, accommodationId);
                ps1.executeUpdate();
            }
            // Then set the new primary
            try (PreparedStatement ps2 = c.prepareStatement(
                    "UPDATE photos SET is_primary = 1 WHERE id = ?")) {
                ps2.setInt(1, id);
                return ps2.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}












