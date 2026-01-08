package com.pandalodge.dao;

import com.pandalodge.model.Accommodation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS accommodations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, " +
                    "price REAL, " +
                    "address TEXT, " +
                    "image_url TEXT, " +
                    "furnished INTEGER DEFAULT 0, " +
                    "description TEXT, " +
                    "status TEXT DEFAULT 'AVAILABLE', " +
                    "latitude REAL DEFAULT 0, " +
                    "longitude REAL DEFAULT 0, " +
                    "owner_id INTEGER DEFAULT 0, " +
                    "owner_name TEXT, " +
                    "owner_email TEXT, " +
                    "owner_phone TEXT)");

            // Attempt to migrate if table named 'accommodations' still exists (legacy cleanup)
            try {
                s.execute("INSERT INTO accommodations SELECT * FROM accommodations");
                s.execute("DROP TABLE accommodations");
            } catch (Exception e) {
            }

            ResultSet rs = s.executeQuery("SELECT count(*) FROM accommodations");
            if (rs.next() && rs.getInt(1) == 0) {
                seedData(s);
            }
        } catch (Exception e) {
            System.err.println("AccommodationDAO.init failed: " + e.getMessage());
        }
    }

    private static void seedData(Statement s) throws SQLException {
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_id, owner_name, owner_email, owner_phone) VALUES "
                        + "('Room', 450, 'Quartier Latin, 5ème, Paris', 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800', 1, "
                        + "'Cozy private room in historic Quartier Latin. Walking distance to Sorbonne University.', 'AVAILABLE', 48.8490, 2.3470, 1, 'Marie Dubois', 'marie.dubois@gmail.com', '+33 6 12 34 56 78')");
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude) VALUES "
                        + "('Studio', 850, 'Saint-Germain-des-Prés, 6ème, Paris', 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800', 1, "
                        + "'Elegant studio in prestigious Saint-Germain. Private kitchenette and bathroom.', 'AVAILABLE', 48.8539, 2.3338)");
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude) VALUES "
                        + "('Apartment', 1400, 'Saint-Germain-des-Prés, 6ème, Paris', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800', 1, "
                        + "'Luxurious 2-bedroom apartment. Perfect for sharing!', 'AVAILABLE', 48.8530, 2.3330)");
    }

    public static Accommodation create(String type, double price, String address, String imageUrl, boolean furnished,
            String description, String status) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status) VALUES(?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setDouble(2, price);
            ps.setString(3, address);
            ps.setString(4, imageUrl);
            ps.setInt(5, furnished ? 1 : 0);
            ps.setString(6, description);
            ps.setString(7, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return new Accommodation(rs.getInt(1), type, price, address, imageUrl, furnished, description,
                            status, 0.0, 0.0);
            }
        }
        return null;
    }

    public static boolean update(int id, String type, double price) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE accommodations SET type = ?, price = ? WHERE id = ?")) {
            ps.setString(1, type);
            ps.setDouble(2, price);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM accommodations WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Accommodation> findAll() {
        List<Accommodation> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM accommodations")) {
            while (rs.next()) {
                list.add(parseAccommodation(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Accommodation> findMatches(String type, String location) {
        List<Accommodation> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM accommodations WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.equals("All") && !type.isBlank()) {
            sql.append(" AND type LIKE ?");
            params.add("%" + type + "%");
        }
        if (location != null && !location.isBlank()) {
            sql.append(" AND address LIKE ?");
            params.add("%" + location + "%");
        }

        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parseAccommodation(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Accommodation findById(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT * FROM accommodations WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return parseAccommodation(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Accommodation parseAccommodation(ResultSet rs) throws SQLException {
        return new Accommodation(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getDouble("price"),
                rs.getString("address"),
                rs.getString("image_url"),
                rs.getInt("furnished") == 1,
                rs.getString("description"),
                rs.getString("status"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getInt("owner_id"),
                rs.getString("owner_name"),
                rs.getString("owner_email"),
                rs.getString("owner_phone"));
    }
}






