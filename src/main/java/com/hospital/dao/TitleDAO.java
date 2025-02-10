package com.hospital.dao;

import com.hospital.model.Title;
import java.util.List;

public interface TitleDAO {
    void create(Title title);
    Title getById(int id);
    List<Title> getAll();
    void update(Title title);
    boolean delete(int id);
} 