package com.hospital.model;

public class Patient {
    private int id;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private String address;
    
    // 构造函数
    public Patient() {}
    
    public Patient(String name, String gender, String phone, String idCard, String address) {
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.idCard = idCard;
        this.address = address;
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
} 