package com.pandalodge.dao;

import com.pandalodge.model.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");
            try (ResultSet rs = s.executeQuery("PRAGMA table_info(students)")) {
                boolean hasPassword = false;
                while (rs.next()) {
                    String col = rs.getString("name");
                    if ("password".equalsIgnoreCase(col)) {
                        hasPassword = true;
                        break;
                    }
                }
                if (!hasPassword) {
                    try {
                        s.execute("ALTER TABLE students ADD COLUMN password TEXT");
                    } catch (SQLException ignore) {
                        ignore.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensurePasswordColumn(Connection c) {
        try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("PRAGMA table_info(students)")) {
            boolean hasPassword = false;
            while (rs.next()) {
                String col = rs.getString("name");
                if ("password".equalsIgnoreCase(col)) {
                    hasPassword = true;
                    break;
                }
            }
            if (!hasPassword) {
                try {
                    s.execute("ALTER TABLE students ADD COLUMN password TEXT");
                } catch (SQLException ignore) {
                }
            }
        } catch (SQLException e) {
        }
    }

    public static Student create(String name, String email) throws SQLException {
        return create(name, email, null);
    }

    public static Student create(String name, String email, String password) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            ensurePasswordColumn(c);
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO students(name,email,password) VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                if (password != null)
                    ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
                else
                    ps.setString(3, null);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        return new Student(rs.getInt(1), name, email);
                }
            } catch (SQLException ex) {
                String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
                if (msg.contains("no column") || msg.contains("has no column")
                        || msg.contains("table students has no column")) {
                    try (PreparedStatement ps2 = c.prepareStatement("INSERT INTO students(name,email) VALUES(?,?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        ps2.setString(1, name);
                        ps2.setString(2, email);
                        ps2.executeUpdate();
                        try (ResultSet rs = ps2.getGeneratedKeys()) {
                            if (rs.next())
                                return new Student(rs.getInt(1), name, email);
                        }
                    }
                }
                throw ex;
            }
        }
        return null;
    }

    public static boolean update(int id, String name, String email) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("UPDATE students SET name = ?, email = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Student verify(String email, String password) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT * FROM students WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (hash == null)
                        return null;
                    if (BCrypt.checkpw(password, hash)) {
                        return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Student findById(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT id, name, email FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Student findByEmail(String email) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT id, name, email FROM students WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT id, name, email FROM students")) {
            while (rs.next())
                list.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

