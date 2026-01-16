package com.pandalodge.dao;

import com.pandalodge.model.FAQ;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FAQDAO {
    public static void init() {
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS faqs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "question TEXT NOT NULL, " +
                    "answer TEXT NOT NULL, " +
                    "category TEXT DEFAULT 'GENERAL', " +
                    "order_index INTEGER DEFAULT 0)");

            ResultSet rs = s.executeQuery("SELECT count(*) FROM faqs");
            if (rs.next() && rs.getInt(1) == 0) {
                seedData(s);
            }

            s.execute("DELETE FROM faqs WHERE question LIKE '%payment%' OR question LIKE '%charged%'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedData(Statement s) throws SQLException {
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('How do I book an accommodation?', 'Browse our listings, select your preferred accommodation, choose your dates, and click \"Book Now\". You will need to be logged in to complete the booking.', 'BOOKING', 1)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Can I cancel my booking?', 'Yes, you can cancel your booking up to 48 hours before check-in for a full refund. Cancellations within 48 hours may incur a fee.', 'BOOKING', 2)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('How far in advance can I book?', 'You can book up to 12 months in advance, depending on availability.', 'BOOKING', 3)");

        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Are the properties verified?', 'Yes, all properties are personally verified by our team before listing. We ensure safety and quality standards are met.', 'SECURITY', 1)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Is my personal data safe?', 'Absolutely. We use industry-standard encryption and never share your data with third parties without your consent.', 'SECURITY', 2)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('What if I have issues with my landlord?', 'Contact our support team immediately. We mediate disputes and ensure your rights as a tenant are protected.', 'SECURITY', 3)");

        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Are the accommodations furnished?', 'Most accommodations are fully furnished. Check the listing details for specific furnishing information.', 'ACCOMMODATION', 1)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Is WiFi included?', 'WiFi is included in most listings. Check the amenities section for each property.', 'ACCOMMODATION', 2)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Can I visit before booking?', 'Virtual tours are available for most properties. For in-person visits, contact the property owner through our platform.', 'ACCOMMODATION', 3)");

        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('How do I contact support?', 'You can reach our support team at support@pandastays.com or through the Help section in your account.', 'GENERAL', 1)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('Is Panda Stays only for students?', 'Yes, we specialize in student accommodations near universities and educational institutions in France.', 'GENERAL', 2)");
        s.execute("INSERT INTO faqs(question, answer, category, order_index) VALUES " +
                "('What documents do I need?', 'You will need a valid student ID, proof of enrollment, and a valid ID/passport.', 'GENERAL', 3)");
    }

    public static FAQ create(String question, String answer, String category) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO faqs(question, answer, category) VALUES(?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, question);
            ps.setString(2, answer);
            ps.setString(3, category);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new FAQ(rs.getInt(1), question, answer, category);
                }
            }
        }
        return null;
    }

    public static FAQ findById(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT * FROM faqs WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FAQ(rs.getInt("id"), rs.getString("question"), rs.getString("answer"),
                            rs.getString("category"), rs.getInt("order_index"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FAQ> findByCategory(String category) {
        List<FAQ> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM faqs WHERE category = ? ORDER BY order_index")) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FAQ(rs.getInt("id"), rs.getString("question"), rs.getString("answer"),
                            rs.getString("category"), rs.getInt("order_index")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<FAQ> findAll() {
        List<FAQ> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM faqs ORDER BY category, order_index")) {
            while (rs.next()) {
                list.add(new FAQ(rs.getInt("id"), rs.getString("question"), rs.getString("answer"),
                        rs.getString("category"), rs.getInt("order_index")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(int id, String question, String answer, String category) throws SQLException {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE faqs SET question = ?, answer = ?, category = ? WHERE id = ?")) {
            ps.setString(1, question);
            ps.setString(2, answer);
            ps.setString(3, category);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM faqs WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
