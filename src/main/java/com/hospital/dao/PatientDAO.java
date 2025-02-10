package com.hospital.dao;

import com.hospital.model.Patient;
import java.util.List;

public interface PatientDAO {
    void create(Patient patient);
    Patient getById(int id);
    Patient getByIdCard(String idCard);
    List<Patient> getAll();
    List<Patient> search(String keyword);
    void update(Patient patient);
    boolean delete(int id);
} 