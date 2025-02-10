package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.model.Schedule;
import java.util.List;
import java.util.Date;

public interface AppointmentDAO {
    void create(Appointment appointment);
    Appointment getById(int id);
    List<Appointment> getAll();
    List<Appointment> getByPatient(int patientId);
    List<Appointment> getByDoctor(int doctorId);
    List<Appointment> getByDate(Date date);
    void update(Appointment appointment);
    boolean delete(int id);
    boolean cancelAppointment(int id);
    boolean completeAppointment(int id);
    /**
     * 通过存储过程获取指定患者的预约记录
     */
    List<Appointment> getPatientAppointmentsByProc(int patientId);
    
    /**
     * 通过存储过程获取医生某天的预约情况
     */
    List<Schedule> getDoctorDayScheduleByProc(int doctorId, Date date);
} 