package com.hospital.model;

public class Department {
    private int id;
    private String name;
    private String description;
    private String location;
    
    // 构造函数
    public Department() {}
    
    public Department(String name, String description, String location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }
    
    // Getter和Setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
} 