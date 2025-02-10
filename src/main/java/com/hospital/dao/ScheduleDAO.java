package com.hospital.dao;

import com.hospital.model.Schedule;
import java.util.List;
import java.util.Date;

public interface ScheduleDAO {
    void create(Schedule schedule);
    Schedule getById(int id);
    List<Schedule> getAll();
    List<Schedule> getByDoctor(int doctorId);
    List<Schedule> getByDate(Date date);
    List<Schedule> getByDoctorAndDate(int doctorId, Date date);
    List<Schedule> getAvailableByDateRange(Date startDate, Date endDate);
    List<Schedule> getByDoctorAndDateRange(int doctorId, Date startDate, Date endDate);
    void update(Schedule schedule);
    boolean delete(int id);
    boolean updateStatus(int id, int status);
    void batchCreate(List<Schedule> schedules);
    
    /**
     * 获取医生某天的可用时间段
     * @param doctorId 医生ID
     * @param date 日期
     * @return 可用的时间段列表
     */
    List<Schedule> getAvailableByDoctorAndDate(int doctorId, Date date);
} 