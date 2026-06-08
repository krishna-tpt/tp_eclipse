package com.expensemanager.dao;

import com.expensemanager.model.SubCategory;
import com.expensemanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubCategoryDAO {

    private final DBConnection db = DBConnection.getInstance();

    public List<SubCategory> findAll() throws SQLException {
        String sql = "SELECT sub_categories_id, name, category_id FROM sub_categories ORDER BY category_id, name";
        Connection conn = db.getConnection();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<SubCategory> list = new ArrayList<>();
            while (rs.next())
                list.add(new SubCategory(rs.getInt("sub_categories_id"),
                        rs.getString("name"), rs.getInt("category_id")));
            return list;
        } finally {
            db.releaseConnection(conn);
        }
    }

    public void insert(String name, int categoryId) throws SQLException {
        String sql = "INSERT INTO sub_categories (name, category_id) VALUES (?, ?)";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.executeUpdate();
        } finally {
            db.releaseConnection(conn);
        }
    }

    public void delete(int id) throws SQLException {
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE transactions SET sub_categories_id = NULL WHERE sub_categories_id = ?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM sub_categories WHERE sub_categories_id = ?")) {
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