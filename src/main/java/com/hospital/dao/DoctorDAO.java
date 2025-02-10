package com.hospital.dao;

import com.hospital.model.Doctor;
import java.util.List;

public interface DoctorDAO {
    void create(Doctor doctor);
    Doctor getById(int id);
    List<Doctor> getAll();
    void update(Doctor doctor);
    boolean delete(int id);
    List<Doctor> getByDepartment(int departmentId);
    List<Doctor> getAvailable(); // 获取在职医生
} 