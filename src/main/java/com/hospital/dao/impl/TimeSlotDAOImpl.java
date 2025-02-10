package com.hospital.dao.impl;

import com.hospital.dao.TimeSlotDAO;
import com.hospital.model.TimeSlot;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAOImpl implements TimeSlotDAO {
    @Override
    public void create(TimeSlot timeSlot) {
        String sql = "INSERT INTO time_slots (start_time, end_time, period, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTime(1, timeSlot.getStartTime());
            pstmt.setTime(2, timeSlot.getEndTime());
            pstmt.setString(3, timeSlot.getPeriod());
            pstmt.setInt(4, timeSlot.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    timeSlot.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建时间段失败", e);
        }
    }

    @Override
    public TimeSlot getById(int id) {
        String sql = "SELECT * FROM time_slots WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTimeSlot(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询时间段失败", e);
        }
        return null;
    }

    @Override
    public List<TimeSlot> getAll() {
        String sql = "SELECT * FROM time_slots ORDER BY start_time";
        
        List<TimeSlot> timeSlots = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                timeSlots.add(mapResultSetToTimeSlot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询所有时间段失败", e);
        }
        return timeSlots;
    }

    @Override
    public List<TimeSlot> getEnabled() {
        String sql = "SELECT * FROM time_slots WHERE status = 1 ORDER BY start_time";
        
        List<TimeSlot> timeSlots = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                timeSlots.add(mapResultSetToTimeSlot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询启用的时间段失败", e);
        }
        return timeSlots;
    }

    @Override
    public boolean update(TimeSlot timeSlot) {
        String sql = "UPDATE time_slots SET start_time = ?, end_time = ?, period = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTime(1, timeSlot.getStartTime());
            pstmt.setTime(2, timeSlot.getEndTime());
            pstmt.setString(3, timeSlot.getPeriod());
            pstmt.setInt(4, timeSlot.getStatus());
            pstmt.setInt(5, timeSlot.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("更新时间段失败", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM time_slots WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("删除时间段失败", e);
        }
    }
    
    private TimeSlot mapResultSetToTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(rs.getInt("id"));
        timeSlot.setStartTime(rs.getTime("start_time"));
        timeSlot.setEndTime(rs.getTime("end_time"));
        timeSlot.setPeriod(rs.getString("period"));
        timeSlot.setStatus(rs.getInt("status"));
        return timeSlot;
    }
} 