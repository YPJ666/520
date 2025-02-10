package com.hospital.dao.impl;

import com.hospital.dao.ScheduleDAO;
import com.hospital.model.Schedule;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ScheduleDAOImpl implements ScheduleDAO {
    @Override
    public void create(Schedule schedule) {
        String sql = "INSERT INTO schedules (doctor_id, date, time_slot_id, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, schedule.getDoctorId());
            pstmt.setDate(2, new java.sql.Date(schedule.getDate().getTime()));
            pstmt.setInt(3, schedule.getTimeSlotId());
            pstmt.setInt(4, schedule.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    schedule.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建排班失败", e);
        }
    }

    @Override
    public Schedule getById(int id) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSchedule(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询排班失败", e);
        }
        return null;
    }

    @Override
    public List<Schedule> getAll() {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "ORDER BY s.date DESC, ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询所有排班失败", e);
        }
        return schedules;
    }

    @Override
    public List<Schedule> getByDoctor(int doctorId) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.doctor_id = ? " +
                    "ORDER BY s.date DESC, ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询医生排班失败", e);
        }
        return schedules;
    }

    @Override
    public List<Schedule> getByDate(Date date) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.date = ? " +
                    "ORDER BY ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询日期排班失败", e);
        }
        return schedules;
    }

    @Override
    public boolean delete(int id) {
        // 先检查是否有关联的预约
        String checkSql = "SELECT COUNT(*) FROM appointments WHERE schedule_id = ?";
        String deleteSql = "DELETE FROM schedules WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            // 开启事务
            conn.setAutoCommit(false);
            
            try {
                // 检查是否有关联的预约
                try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        // 如果有关联的预约，先删除预约
                        try (PreparedStatement delAppStmt = conn.prepareStatement(
                                "DELETE FROM appointments WHERE schedule_id = ?")) {
                            delAppStmt.setInt(1, id);
                            delAppStmt.executeUpdate();
                        }
                    }
                }
                
                // 删除排班
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, id);
                    boolean result = pstmt.executeUpdate() > 0;
                    conn.commit();
                    return result;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("删除排班失败", e);
        }
    }

    @Override
    public boolean updateStatus(int id, int status) {
        String sql = "UPDATE schedules SET status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("更新排班状态失败", e);
        }
    }
    
    @Override
    public List<Schedule> getByDoctorAndDate(int doctorId, Date date) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.doctor_id = ? AND s.date = ? " +
                    "ORDER BY ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询医生指定日期排班失败", e);
        }
        return schedules;
    }

    @Override
    public List<Schedule> getAvailableByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.date BETWEEN ? AND ? AND s.status = 1 " +
                    "ORDER BY s.date, ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询日期范围内可用排班失败", e);
        }
        return schedules;
    }

    @Override
    public List<Schedule> getByDoctorAndDateRange(int doctorId, Date startDate, Date endDate) {
        String sql = "SELECT s.*, d.name as doctor_name, dept.name as department_name, " +
                    "CONCAT(ts.start_time, '-', ts.end_time, ' (', ts.period, ')') as time_slot " +
                    "FROM schedules s " +
                    "LEFT JOIN doctors d ON s.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.doctor_id = ? AND s.date BETWEEN ? AND ? " +
                    "ORDER BY s.date, ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询医生日期范围内排班失败", e);
        }
        return schedules;
    }

    @Override
    public void update(Schedule schedule) {
        String sql = "UPDATE schedules SET doctor_id = ?, date = ?, time_slot_id = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, schedule.getDoctorId());
            pstmt.setDate(2, new java.sql.Date(schedule.getDate().getTime()));
            pstmt.setInt(3, schedule.getTimeSlotId());
            pstmt.setInt(4, schedule.getStatus());
            pstmt.setInt(5, schedule.getId());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("更新排班失败", e);
        }
    }

    @Override
    public void batchCreate(List<Schedule> schedules) {
        String sql = "INSERT INTO schedules (doctor_id, date, time_slot_id, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (Schedule schedule : schedules) {
                pstmt.setInt(1, schedule.getDoctorId());
                pstmt.setDate(2, new java.sql.Date(schedule.getDate().getTime()));
                pstmt.setInt(3, schedule.getTimeSlotId());
                pstmt.setInt(4, schedule.getStatus());
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            
        } catch (SQLException e) {
            throw new RuntimeException("批量创建排班失败", e);
        }
    }
    
    @Override
    public List<Schedule> getAvailableByDoctorAndDate(int doctorId, Date date) {
        String sql = "SELECT s.*, ts.start_time, ts.end_time, ts.period " +
                    "FROM schedules s " +
                    "JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "WHERE s.doctor_id = ? " +
                    "AND s.date = ? " +
                    "AND s.status = 1 " +  // 只查询可预约的时间段
                    "ORDER BY ts.start_time";
        
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            
            System.out.println("正在查询医生ID=" + doctorId + " 在 " + date + " 的可用时间段");
            System.out.println("执行SQL: " + sql.replace("?", "'" + doctorId + "', '" + new java.sql.Date(date.getTime()) + "'"));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Schedule schedule = new Schedule();
                    schedule.setId(rs.getInt("id"));
                    schedule.setDoctorId(rs.getInt("doctor_id"));
                    schedule.setDate(rs.getDate("date"));
                    schedule.setTimeSlotId(rs.getInt("time_slot_id"));
                    schedule.setStatus(rs.getInt("status"));
                    
                    // 设置时间段信息
                    String timeSlot = String.format("%s-%s (%s)", 
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("period"));
                    schedule.setTimeSlot(timeSlot);
                    
                    schedules.add(schedule);
                    System.out.println("找到可用时间段: " + timeSlot);
                }
            }
            System.out.println("共找到 " + schedules.size() + " 个可用时间段");
        } catch (SQLException e) {
            System.err.println("查询医生可用时间段失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("查询医生可用时间段失败", e);
        }
        return schedules;
    }
    
    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setId(rs.getInt("id"));
        schedule.setDoctorId(rs.getInt("doctor_id"));
        schedule.setDate(rs.getDate("date"));
        schedule.setTimeSlotId(rs.getInt("time_slot_id"));
        schedule.setStatus(rs.getInt("status"));
        schedule.setCreateTime(rs.getTimestamp("create_time"));
        
        schedule.setDoctorName(rs.getString("doctor_name"));
        schedule.setDepartmentName(rs.getString("department_name"));
        schedule.setTimeSlot(rs.getString("time_slot"));
        
        return schedule;
    }
} 