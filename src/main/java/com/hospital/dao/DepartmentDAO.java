package com.hospital.dao;

import com.hospital.model.Department;
import java.util.List;

public interface DepartmentDAO {
    Department getById(int id);
    List<Department> getAll();
    void create(Department department);
    void update(Department department);
    boolean delete(int id);
    List<Department> getByHospital(int hospitalId);
} 