package com.hospital.dao.impl;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl implements DoctorDAO {
    
    @Override
    public void create(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, department_id, title_id, specialty, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setInt(2, doctor.getDepartmentId());
            pstmt.setInt(3, doctor.getTitleId());
            pstmt.setString(4, doctor.getSpecialty());
            pstmt.setInt(5, doctor.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    doctor.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建医生失败", e);
        }
    }

    @Override
    public Doctor getById(int id) {
        String sql = "SELECT d.*, dept.name as department_name, t.name as title_name " +
                    "FROM doctors d " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN titles t ON d.title_id = t.id " +
                    "WHERE d.id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询医生失败", e);
        }
        return null;
    }

    @Override
    public List<Doctor> getAll() {
        String sql = "SELECT d.*, dept.name as department_name, t.name as title_name " +
                    "FROM doctors d " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN titles t ON d.title_id = t.id";
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询所有医生失败", e);
        }
        return doctors;
    }

    @Override
    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, department_id = ?, title_id = ?, specialty = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setInt(2, doctor.getDepartmentId());
            pstmt.setInt(3, doctor.getTitleId());
            pstmt.setString(4, doctor.getSpecialty());
            pstmt.setInt(5, doctor.getStatus());
            pstmt.setInt(6, doctor.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新医生信息失败", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("删除医生失败", e);
        }
    }

    @Override
    public List<Doctor> getByDepartment(int departmentId) {
        String sql = "SELECT DISTINCT " +
                    "d.id, d.name, d.department_id, d.title_id, d.specialty, d.status, " +
                    "dept.name as department_name, " +
                    "t.name as title_name " +
                    "FROM doctors d " +
                    "JOIN departments dept ON d.department_id = dept.id " +
                    "JOIN titles t ON d.title_id = t.id " +
                    "JOIN schedules s ON d.id = s.doctor_id " +
                    "WHERE d.department_id = ? " +
                    "AND d.status = 1 " +  // 医生在职
                    "AND s.status = 1 " +   // 排班状态为可约
                    "AND s.date >= CURDATE() " + // 只看今天及以后的排班
                    "ORDER BY d.name";
        
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, departmentId);
            System.out.println("正在查询科室ID为 " + departmentId + " 的可预约医生");
            System.out.println("执行SQL: " + sql.replace("?", String.valueOf(departmentId)));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setDepartmentId(rs.getInt("department_id"));
                    doctor.setDepartmentName(rs.getString("department_name"));
                    doctor.setTitleId(rs.getInt("title_id"));
                    doctor.setTitleName(rs.getString("title_name"));
                    doctor.setSpecialty(rs.getString("specialty"));
                    doctor.setStatus(rs.getInt("status"));
                    doctors.add(doctor);
                    
                    System.out.println("找到医生: " + doctor.getName() + 
                        " (ID=" + doctor.getId() + 
                        ", 科室=" + doctor.getDepartmentName() + 
                        ", 职称=" + doctor.getTitleName() + 
                        ", 专长=" + doctor.getSpecialty() + ")");
                }
            }
            System.out.println("共找到 " + doctors.size() + " 名可预约医生");
        } catch (SQLException e) {
            System.err.println("查询科室医生失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("查询科室医生失败", e);
        }
        return doctors;
    }

    @Override
    public List<Doctor> getAvailable() {
        String sql = "SELECT d.*, dept.name as department_name, t.name as title_name " +
                    "FROM doctors d " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN titles t ON d.title_id = t.id " +
                    "WHERE d.status = 1";
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询在职医生失败", e);
        }
        return doctors;
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setName(rs.getString("name"));
        doctor.setDepartmentId(rs.getInt("department_id"));
        doctor.setDepartmentName(rs.getString("department_name"));
        doctor.setTitleId(rs.getInt("title_id"));
        doctor.setTitleName(rs.getString("title_name"));
        doctor.setSpecialty(rs.getString("specialty"));
        doctor.setStatus(rs.getInt("status"));
        return doctor;
    }
} 