package com.pandalodge.dao;

import com.pandalodge.model.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL)");

            // Create default admin if table is empty
            ResultSet rs = s.executeQuery("SELECT count(*) FROM admins");
            if (rs.next() && rs.getInt(1) == 0) {
                // Default admin: admin@pandastays.com / admin123
                String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                s.execute("INSERT INTO admins(name, email, password) VALUES " +
                        "('System Administrator', 'admin@pandastays.com', '" + hashedPassword + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Admin verify(String email, String password) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM admins WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (hash != null && BCrypt.checkpw(password, hash)) {
                        return new Admin(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Admin create(String name, String email, String password) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO admins(name, email, password) VALUES(?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Admin(rs.getInt(1), name, email);
                }
            }
        }
        return null;
    }

    public static Admin findById(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM admins WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Admin> findAll() {
        List<Admin> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM admins ORDER BY name")) {
            while (rs.next()) {
                list.add(new Admin(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updatePassword(int id, String newPassword) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE admins SET password = ? WHERE id = ?")) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}












