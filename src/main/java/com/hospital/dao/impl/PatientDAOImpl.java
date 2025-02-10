package com.hospital.dao.impl;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {
    
    @Override
    public void create(Patient patient) {
        String sql = "INSERT INTO patients (name, gender, phone, id_card, address) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getGender());
            pstmt.setString(3, patient.getPhone());
            pstmt.setString(4, patient.getIdCard());
            pstmt.setString(5, patient.getAddress());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    patient.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建患者失败", e);
        }
    }

    @Override
    public Patient getById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询患者失败", e);
        }
        return null;
    }

    @Override
    public Patient getByIdCard(String idCard) {
        String sql = "SELECT * FROM patients WHERE id_card = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idCard);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询患者失败", e);
        }
        return null;
    }

    @Override
    public List<Patient> getAll() {
        String sql = "SELECT * FROM patients ORDER BY name";
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询所有患者失败", e);
        }
        return patients;
    }

    @Override
    public List<Patient> search(String keyword) {
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR id_card LIKE ? ORDER BY name";
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("搜索患者失败", e);
        }
        return patients;
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE patients SET name = ?, gender = ?, phone = ?, id_card = ?, address = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getGender());
            pstmt.setString(3, patient.getPhone());
            pstmt.setString(4, patient.getIdCard());
            pstmt.setString(5, patient.getAddress());
            pstmt.setInt(6, patient.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新患者信息失败", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除患者失败", e);
        }
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setName(rs.getString("name"));
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setIdCard(rs.getString("id_card"));
        patient.setAddress(rs.getString("address"));
        return patient;
    }
} 