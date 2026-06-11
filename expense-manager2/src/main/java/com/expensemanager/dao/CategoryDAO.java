package com.expensemanager.dao;

import com.expensemanager.model.Category;
import com.expensemanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private final DBConnection db = DBConnection.getInstance();

    public List<Category> findByType(String type) throws SQLException {
        String sql = "SELECT id, name, type FROM categories WHERE type=?::txn_type ORDER BY name";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            List<Category> list = new ArrayList<>();
            while (rs.next())
                list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
            return list;
        } finally {
            db.releaseConnection(conn);
        }
    }

    public void insert(String name, String type) throws SQLException {
        String sql = "INSERT INTO categories (name, type) VALUES (?, ?::txn_type) ON CONFLICT DO NOTHING";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.executeUpdate();
        } finally {
            db.releaseConnection(conn);
        }
    }

    public void delete(int id) throws SQLException {
        // Set category_id to null in transactions first, then delete
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE transactions SET category_id = NULL WHERE category_id = ?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM categories WHERE id = ?")) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            db.releaseConnection(conn);
        }
    }
}