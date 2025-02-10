package com.hospital.dao.impl;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {
    
    @Override
    public Department getById(int id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询科室失败", e);
        }
        return null;
    }

    @Override
    public List<Department> getAll() {
        String sql = "SELECT * FROM departments ORDER BY name";
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询所有科室失败", e);
        }
        return departments;
    }

    @Override
    public void create(Department department) {
        String sql = "INSERT INTO departments (name, description, location) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getDescription());
            pstmt.setString(3, department.getLocation());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    department.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建科室失败", e);
        }
    }

    @Override
    public void update(Department department) {
        String sql = "UPDATE departments SET name = ?, description = ?, location = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getDescription());
            pstmt.setString(3, department.getLocation());
            pstmt.setInt(4, department.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新科室失败", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除科室失败", e);
        }
    }

    @Override
    public List<Department> getByHospital(int hospitalId) {
        String sql = "SELECT * FROM departments WHERE hospital_id = ? ORDER BY name";
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hospitalId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    departments.add(mapResultSetToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询医院科室失败", e);
        }
        return departments;
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("id"));
        department.setName(rs.getString("name"));
        department.setDescription(rs.getString("description"));
        department.setLocation(rs.getString("location"));
        return department;
    }
} 