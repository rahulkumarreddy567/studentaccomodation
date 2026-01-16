package com.pandalodge.dao;

import com.pandalodge.model.Renter;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RenterDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS renters (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "phone TEXT, " +
                    "password TEXT)");

            ResultSet rs = s.executeQuery("SELECT count(*) FROM renters");
            if (rs.next() && rs.getInt(1) == 0) {
                seedData(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedData(Statement s) throws SQLException {
        s.execute("INSERT INTO renters(name, email, phone, password) VALUES " +
                "('Marie Dubois', 'marie.dubois@gmail.com', '+33 6 12 34 56 78', '" + BCrypt.hashpw("password123", BCrypt.gensalt()) + "')");
        s.execute("INSERT INTO renters(name, email, phone, password) VALUES " +
                "('Jean-Pierre Martin', 'jp.martin@orange.fr', '+33 6 98 76 54 32', '" + BCrypt.hashpw("password123", BCrypt.gensalt()) + "')");
        s.execute("INSERT INTO renters(name, email, phone, password) VALUES " +
                "('Sophie Laurent', 'sophie.laurent@free.fr', '+33 7 11 22 33 44', '" + BCrypt.hashpw("password123", BCrypt.gensalt()) + "')");
        s.execute("INSERT INTO renters(name, email, phone, password) VALUES " +
                "('Pierre Lefebvre', 'p.lefebvre@gmail.com', '+33 6 55 66 77 88', '" + BCrypt.hashpw("password123", BCrypt.gensalt()) + "')");
        s.execute("INSERT INTO renters(name, email, phone, password) VALUES " +
                "('Isabelle Moreau', 'isabelle.m@yahoo.fr', '+33 7 22 33 44 55', '" + BCrypt.hashpw("password123", BCrypt.gensalt()) + "')");
    }

    public static Renter create(String name, String email, String phone, String password) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO renters(name, email, phone, password) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password != null ? BCrypt.hashpw(password, BCrypt.gensalt()) : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Renter(rs.getInt(1), name, email, phone);
                }
            }
        }
        return null;
    }

    public static Renter verify(String email, String password) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM renters WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (hash != null && BCrypt.checkpw(password, hash)) {
                        return new Renter(rs.getInt("id"), rs.getString("name"),
                                         rs.getString("email"), rs.getString("phone"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Renter findById(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM renters WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Renter(rs.getInt("id"), rs.getString("name"),
                                     rs.getString("email"), rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Renter findByEmail(String email) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM renters WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Renter(rs.getInt("id"), rs.getString("name"),
                                     rs.getString("email"), rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Renter> findAll() {
        List<Renter> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM renters ORDER BY name")) {
            while (rs.next()) {
                list.add(new Renter(rs.getInt("id"), rs.getString("name"),
                                   rs.getString("email"), rs.getString("phone")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(int id, String name, String email, String phone) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE renters SET name = ?, email = ?, phone = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM renters WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

