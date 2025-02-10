package com.hospital.dao;

import com.hospital.model.TimeSlot;
import java.util.List;

public interface TimeSlotDAO {
    void create(TimeSlot timeSlot);
    TimeSlot getById(int id);
    List<TimeSlot> getAll();
    List<TimeSlot> getEnabled();
    boolean update(TimeSlot timeSlot);
    boolean delete(int id);
} 