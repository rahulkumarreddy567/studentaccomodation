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
                    "owner_phone TEXT, " +
                    "size TEXT)");

            // Schema upgrade: check if owner_name column exists
            try {
                s.executeQuery("SELECT owner_name FROM accommodations LIMIT 1");
            } catch (SQLException e) {
                System.out.println("Migrating database to include owner fields...");
                s.execute("ALTER TABLE accommodations ADD COLUMN owner_name TEXT");
                s.execute("ALTER TABLE accommodations ADD COLUMN owner_email TEXT");
                s.execute("ALTER TABLE accommodations ADD COLUMN owner_phone TEXT");
            }

            // Check if size column exists
            try {
                s.executeQuery("SELECT size FROM accommodations LIMIT 1");
            } catch (SQLException e) {
                s.execute("ALTER TABLE accommodations ADD COLUMN size TEXT");
            }

            // Check if address column exists (another possible legacy state)
            try {
                s.executeQuery("SELECT address FROM accommodations LIMIT 1");
            } catch (SQLException e) {
                s.execute("ALTER TABLE accommodations ADD COLUMN address TEXT");
                s.execute("ALTER TABLE accommodations ADD COLUMN image_url TEXT");
                s.execute("ALTER TABLE accommodations ADD COLUMN furnished INTEGER DEFAULT 0");
                s.execute("ALTER TABLE accommodations ADD COLUMN description TEXT");
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
        // Paris - Quartier Latin
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Studio', 950, 'Quartier Latin, Paris', 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1000', 1, "
                        + "'Premium studio in the heart of the historic 5th arrondissement. Steps away from the Sorbonne. Fully renovated in 2025.', 'AVAILABLE', 48.8490, 2.3470, 'Jean Dupont', 'jean.dupont@paris.fr', '+33 1 45 67 89 01', '22m²')");

        // Lyon - Part-Dieu
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Room', 550, 'Part-Dieu, Lyon', 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=1000', 1, "
                        + "'Large room in a friendly international flatshare. Close to Part-Dieu station. Includes weekly cleaning.', 'AVAILABLE', 45.7606, 4.8592, 'Sophie Morel', 'sophie.lyon@gmail.com', '+33 4 78 12 34 56', '18m²')");

        // Bordeaux - Chartrons
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Apartment', 1100, 'Chartrons, Bordeaux', 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1000', 1, "
                        + "'Typical Bordeaux stone apartment. 2 bedrooms, perfect for sharing. 2 minutes from the tramway.', 'AVAILABLE', 44.8540, -0.5750, 'Marc Durand', 'marc.bordeaux@yahoo.com', '+33 5 56 11 22 33', '55m²')");

        // Marseille - Prado
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Studio', 700, 'Prado, Marseille', 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=1000', 1, "
                        + "'Sunny studio with sea view from the balcony. Located near university campus and beaches.', 'AVAILABLE', 43.2721, 5.3854, 'Claire Petit', 'claire.petit@marseille.com', '+33 4 91 00 11 22', '20m²')");

        // Toulouse - Capitole
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Studio', 650, 'Capitole, Toulouse', 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=1000', 1, "
                        + "'Charming studio in the heart of the Pink City. Fast internet and bicycle storage. Close to Toulouse 1 University.', 'AVAILABLE', 43.6047, 1.4442, 'Antoine Bernard', 'a.bernard@toulouse.fr', '+33 5 61 33 44 55', '19m²')");

        // Nice - Vieux Nice
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Room', 600, 'Vieux Nice, Nice', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=1000', 1, "
                        + "'Room with Mediterranean charm. 5 min walk to the beach. Perfect for students at the Université Côte d''Azur.', 'AVAILABLE', 43.6961, 7.2718, 'Elodie Blanc', 'elodie.nice@orange.fr', '+33 4 93 55 66 77', '16m²')");

        // Nantes - Centre
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Apartment', 900, 'Centre, Nantes', 'https://images.unsplash.com/photo-1484154218962-a197022b5858?w=1000', 1, "
                        + "'Modern 1-bedroom apartment near the Machines de l''île. Quiet area, very well connected to the university by tram.', 'AVAILABLE', 47.2184, -1.5536, 'Julien Leroy', 'j.leroy@nantes.fr', '+33 2 40 77 88 99', '45m²')");

        // Strasbourg - Krutenau
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Studio', 750, 'Krutenau, Strasbourg', 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=1000', 1, "
                        + "'Student-friendly studio in Strasbourg''s most dynamic district. Close to the campus and European Parliament.', 'AVAILABLE', 48.5839, 7.7455, 'Marie Fischer', 'm.fischer@strasbourg.eu', '+33 3 88 11 22 33', '21m²')");

        // Lille - Vieux Lille
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Room', 500, 'Vieux Lille, Lille', 'https://images.unsplash.com/photo-1493238555826-5304c1af0039?w=1000', 1, "
                        + "'Cozy attic room in a Flanders-style house. Very central, friendly atmosphere. All bills included.', 'AVAILABLE', 50.6292, 3.0573, 'Pierre Dubois', 'pierre.dubois@lille.fr', '+33 3 20 44 55 66', '14m²')");

        // Montpellier - Écusson
        s.execute(
                "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, latitude, longitude, owner_name, owner_email, owner_phone, size) VALUES "
                        + "('Studio', 680, 'Écusson, Montpellier', 'https://images.unsplash.com/photo-1505691938895-1758d7eaa511?w=1000', 1, "
                        + "'Bright studio in the medieval center. Terrace with city views. Highly popular for student life.', 'AVAILABLE', 43.6109, 3.8767, 'Lucie Girard', 'lucie.mont@gmail.com', '+33 4 67 99 00 11', '18m²')");
    }

    public static Accommodation create(String type, double price, String address, String imageUrl, boolean furnished,
            String description, String status, String ownerName, String ownerEmail, String ownerPhone, String size)
            throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO accommodations(type, price, address, image_url, furnished, description, status, owner_name, owner_email, owner_phone, size) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setDouble(2, price);
            ps.setString(3, address);
            ps.setString(4, imageUrl);
            ps.setInt(5, furnished ? 1 : 0);
            ps.setString(6, description);
            ps.setString(7, status);
            ps.setString(8, ownerName);
            ps.setString(9, ownerEmail);
            ps.setString(10, ownerPhone);
            ps.setString(11, size);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return new Accommodation(rs.getInt(1), type, price, address, imageUrl, furnished, description,
                            status, 0.0, 0.0, 0, ownerName, ownerEmail, ownerPhone, size);
            }
        }
        return null;
    }

    public static Accommodation create(String type, double price, String address, String imageUrl, boolean furnished,
            String description, String status) throws SQLException {
        return create(type, price, address, imageUrl, furnished, description, status, "Admin", "admin@panda.com", "",
                "15m²");
    }

    public static boolean update(Accommodation a) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE accommodations SET type = ?, price = ?, address = ?, image_url = ?, furnished = ?, description = ?, status = ?, owner_name = ?, owner_email = ?, owner_phone = ?, size = ? WHERE id = ?")) {
            ps.setString(1, a.getType());
            ps.setDouble(2, a.getPrice());
            ps.setString(3, a.getAddress());
            ps.setString(4, a.getImageUrl());
            ps.setInt(5, a.isFurnished() ? 1 : 0);
            ps.setString(6, a.getDescription());
            ps.setString(7, a.getStatus());
            ps.setString(8, a.getOwnerName());
            ps.setString(9, a.getOwnerEmail());
            ps.setString(10, a.getOwnerPhone());
            ps.setString(11, a.getSize());
            ps.setInt(12, a.getId());
            return ps.executeUpdate() > 0;
        }
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
                rs.getString("owner_phone"),
                rs.getString("size"));
    }

    public static boolean updateStatus(int id, String status) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("UPDATE accommodations SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Accommodation> findFeatured(int limit) {
        List<Accommodation> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM accommodations LIMIT " + limit)) {
            while (rs.next()) {
                list.add(parseAccommodation(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
