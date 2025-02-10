package com.hospital.model;

public class Title {
    private Integer id;
    private String name;
    private Integer level;
    private String description;
    
    public Title() {}
    
    public Title(String name, Integer level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }
    
    // Getters and Setters
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
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 