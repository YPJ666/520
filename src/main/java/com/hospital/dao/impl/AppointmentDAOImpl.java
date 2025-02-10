package com.hospital.dao.impl;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Schedule;
import com.hospital.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class AppointmentDAOImpl implements AppointmentDAO {
    
    @Override
    public void create(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, schedule_id, appointment_date, status_id) " +
                    "SELECT ?, s.doctor_id, ?, s.date, 1 " +  // status_id=1 表示待就诊状态
                    "FROM schedules s WHERE s.id = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 开启事务
            
            try {
                System.out.println("开始创建预约: " + 
                    "patientId=" + appointment.getPatientId() + 
                    ", scheduleId=" + appointment.getScheduleId());
                
                // 1. 先检查排班是否可预约
                String checkSql = "SELECT status FROM schedules WHERE id = ? AND status = 1";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, appointment.getScheduleId());
                    System.out.println("检查排班状态: " + checkSql.replace("?", String.valueOf(appointment.getScheduleId())));
                    
                    ResultSet rs = checkStmt.executeQuery();
                    if (!rs.next()) {
                        throw new RuntimeException("该时间段已不可预约");
                    }
                    System.out.println("排班状态检查通过");
                }
                
                // 2. 插入预约记录
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, appointment.getPatientId());
                    pstmt.setInt(2, appointment.getScheduleId());
                    pstmt.setInt(3, appointment.getScheduleId());
                    
                    System.out.println("执行预约插入SQL: " + sql.replace("?", appointment.getPatientId() + ", " + 
                        appointment.getScheduleId() + ", " + appointment.getScheduleId()));
                    
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new RuntimeException("创建预约记录失败，未插入任何数据");
                    }
                    
                    // 获取生成的预约ID
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            appointment.setId(rs.getInt(1));
                            System.out.println("预约记录创建成功，ID=" + appointment.getId());
                        } else {
                            throw new RuntimeException("创建预约记录失败，未获取到生成的ID");
                        }
                    }
                }
                
                // 3. 更新排班状态为已约（status=2）
                String updateSql = "UPDATE schedules SET status = 2 WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, appointment.getScheduleId());
                    System.out.println("更新排班状态: " + updateSql.replace("?", String.valueOf(appointment.getScheduleId())));
                    
                    int updatedRows = updateStmt.executeUpdate();
                    if (updatedRows == 0) {
                        throw new RuntimeException("更新排班状态失败");
                    }
                    System.out.println("排班状态更新成功");
                }
                
                // 提交事务
                conn.commit();
                System.out.println("预约创建完成，事务已提交");
                
            } catch (Exception e) {
                // 发生异常时回滚事务
                conn.rollback();
                System.err.println("创建预约失败，执行回滚: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("创建预约失败: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败: " + e.getMessage());
        }
    }
    
    @Override
    public Appointment getById(int id) {
        String sql = "SELECT " +
                    "a.*, " +
                    "p.name as patient_name, " +
                    "d.name as doctor_name, " +
                    "dept.name as department_name, " +
                    "ts.start_time, " +
                    "ts.end_time, " +
                    "ts.period, " +
                    "ast.name as status_name " +
                    "FROM appointments a " +
                    "LEFT JOIN patients p ON a.patient_id = p.id " +
                    "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN schedules s ON a.schedule_id = s.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "LEFT JOIN appointment_status ast ON a.status_id = ast.id " +
                    "WHERE a.id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询预约失败", e);
        }
        return null;
    }
    
    @Override
    public List<Appointment> getByPatient(int patientId) {
        String sql = "SELECT " +
                    "a.*, " +
                    "p.name as patient_name, " +
                    "d.name as doctor_name, " +
                    "dept.name as department_name, " +
                    "ts.start_time, " +
                    "ts.end_time, " +
                    "ts.period, " +
                    "ast.name as status_name " +
                    "FROM appointments a " +
                    "LEFT JOIN patients p ON a.patient_id = p.id " +
                    "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN schedules s ON a.schedule_id = s.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "LEFT JOIN appointment_status ast ON a.status_id = ast.id " +
                    "WHERE a.patient_id = ? " +
                    "ORDER BY a.appointment_date DESC, ts.start_time";
        
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询患者预约记录失败", e);
        }
        return appointments;
    }

    @Override
    public List<Appointment> getByDoctor(int doctorId) {
        String sql = "SELECT " +
                    "a.*, " +
                    "p.name as patient_name, " +
                    "d.name as doctor_name, " +
                    "dept.name as department_name, " +
                    "ts.start_time, " +
                    "ts.end_time, " +
                    "ts.period, " +
                    "ast.name as status_name " +
                    "FROM appointments a " +
                    "LEFT JOIN patients p ON a.patient_id = p.id " +
                    "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN schedules s ON a.schedule_id = s.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "LEFT JOIN appointment_status ast ON a.status_id = ast.id " +
                    "WHERE a.doctor_id = ? " +
                    "ORDER BY a.appointment_date DESC, ts.start_time";
        
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询医生预约记录失败", e);
        }
        return appointments;
    }

    @Override
    public List<Appointment> getByDate(Date date) {
        String sql = "SELECT " +
                    "a.*, " +
                    "p.name as patient_name, " +
                    "d.name as doctor_name, " +
                    "dept.name as department_name, " +
                    "ts.start_time, " +
                    "ts.end_time, " +
                    "ts.period, " +
                    "ast.name as status_name " +
                    "FROM appointments a " +
                    "LEFT JOIN patients p ON a.patient_id = p.id " +
                    "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN schedules s ON a.schedule_id = s.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "LEFT JOIN appointment_status ast ON a.status_id = ast.id " +
                    "WHERE a.appointment_date = ? " +
                    "ORDER BY ts.start_time";
        
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询指定日期预约记录失败", e);
        }
        return appointments;
    }

    @Override
    public void update(Appointment appointment) {
        String sql = "UPDATE appointments SET status_id = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getStatusId());
            pstmt.setInt(2, appointment.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新预约状态失败", e);
        }
    }

    @Override
    public boolean cancelAppointment(int id) {
        String sql = "UPDATE appointments SET status_id = 3 WHERE id = ?"; // 3表示已取消
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("取消预约失败", e);
        }
    }

    @Override
    public boolean completeAppointment(int id) {
        String sql = "UPDATE appointments SET status_id = 2 WHERE id = ?"; // 2表示已完成
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("完成预约失败", e);
        }
    }

    @Override
    public List<Appointment> getAll() {
        String sql = "SELECT " +
                    "a.*, " +
                    "p.name as patient_name, " +
                    "d.name as doctor_name, " +
                    "dept.name as department_name, " +
                    "ts.start_time, " +
                    "ts.end_time, " +
                    "ts.period, " +
                    "ast.name as status_name " +
                    "FROM appointments a " +
                    "LEFT JOIN patients p ON a.patient_id = p.id " +
                    "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                    "LEFT JOIN departments dept ON d.department_id = dept.id " +
                    "LEFT JOIN schedules s ON a.schedule_id = s.id " +
                    "LEFT JOIN time_slots ts ON s.time_slot_id = ts.id " +
                    "LEFT JOIN appointment_status ast ON a.status_id = ast.id " +
                    "ORDER BY a.appointment_date DESC, ts.start_time";
        
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询所有预约记录失败", e);
        }
        return appointments;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除预约失败", e);
        }
    }
    
    @Override
    public List<Appointment> getPatientAppointmentsByProc(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "{CALL GetPatientAppointments(?)}";
        
        try (Connection conn = DBUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取患者预约记录失败", e);
        }
        return appointments;
    }
    
    @Override
    public List<Schedule> getDoctorDayScheduleByProc(int doctorId, Date date) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "{CALL GetDoctorDaySchedule(?, ?)}";
        
        try (Connection conn = DBUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, doctorId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(rs.getInt("id"));
                schedule.setDoctorId(rs.getInt("doctor_id"));
                schedule.setDate(rs.getDate("date"));
                schedule.setTimeSlotId(rs.getInt("time_slot_id"));
                schedule.setStatus(rs.getInt("status"));
                schedule.setStatusName(rs.getString("status_name"));
                
                // 设置时间段信息
                String timeSlot = String.format("%s-%s (%s)", 
                    rs.getString("start_time"),
                    rs.getString("end_time"),
                    rs.getString("period"));
                schedule.setTimeSlot(timeSlot);
                
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取医生排班失败", e);
        }
        return schedules;
    }
    
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setScheduleId(rs.getInt("schedule_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setStatusId(rs.getInt("status_id"));
        appointment.setCreateTime(rs.getTimestamp("create_time"));
        
        // 设置关联信息
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        appointment.setDepartmentName(rs.getString("department_name"));
        
        // 组装时间段信息
        String timeSlot = String.format("%s-%s (%s)", 
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getString("period"));
        appointment.setTimeSlot(timeSlot);
        
        appointment.setStatusName(rs.getString("status_name"));
        
        return appointment;
    }

    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setId(rs.getInt("id"));
        schedule.setDoctorId(rs.getInt("doctor_id"));
        schedule.setDate(rs.getDate("date"));
        schedule.setTimeSlotId(rs.getInt("time_slot_id"));
        schedule.setStatus(rs.getInt("status"));
        schedule.setStatusName(rs.getString("status"));
        
        // 设置时间段信息
        String timeSlot = String.format("%s-%s (%s)", 
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getString("period"));
        schedule.setTimeSlot(timeSlot);
        
        return schedule;
    }
} 