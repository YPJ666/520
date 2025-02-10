package com.hospital.dao.impl;

import com.hospital.dao.TitleDAO;
import com.hospital.model.Title;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TitleDAOImpl implements TitleDAO {
    
    @Override
    public void create(Title title) {
        String sql = "INSERT INTO titles (name, level, description) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, title.getName());
            pstmt.setInt(2, title.getLevel());
            pstmt.setString(3, title.getDescription());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    title.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建职称失败", e);
        }
    }

    @Override
    public Title getById(int id) {
        String sql = "SELECT * FROM titles WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTitle(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询职称失败", e);
        }
        return null;
    }

    @Override
    public List<Title> getAll() {
        String sql = "SELECT * FROM titles ORDER BY level DESC";
        List<Title> titles = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                titles.add(mapResultSetToTitle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询所有职称失败", e);
        }
        return titles;
    }

    @Override
    public void update(Title title) {
        String sql = "UPDATE titles SET name = ?, level = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title.getName());
            pstmt.setInt(2, title.getLevel());
            pstmt.setString(3, title.getDescription());
            pstmt.setInt(4, title.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新职称失败", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM titles WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("删除职称失败", e);
        }
    }

    private Title mapResultSetToTitle(ResultSet rs) throws SQLException {
        Title title = new Title();
        title.setId(rs.getInt("id"));
        title.setName(rs.getString("name"));
        title.setLevel(rs.getInt("level"));
        title.setDescription(rs.getString("description"));
        return title;
    }
} 