package com.hospital.model;

public class Doctor {
    private Integer id;
    private String name;
    private Integer departmentId;
    private String departmentName;
    private Integer titleId;
    private String titleName;
    private String specialty;
    private Integer status;
    private String schedule;
    
    // 构造函数
    public Doctor() {}
    
    public Doctor(String name, String department, String title, String specialty) {
        this.name = name;
        this.departmentName = department;
        this.titleName = title;
        this.specialty = specialty;
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public Integer getTitleId() {
        return titleId;
    }
    
    public void setTitleId(Integer titleId) {
        this.titleId = titleId;
    }
    
    public String getTitleName() {
        return titleName;
    }
    
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
} 