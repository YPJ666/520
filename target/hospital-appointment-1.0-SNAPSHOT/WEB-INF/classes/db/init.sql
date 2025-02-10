-- 创建科室表
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    location VARCHAR(100)
);

-- 创建医生表
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    department_id INT,
    title VARCHAR(50),
    specialty VARCHAR(100),
    schedule TEXT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 创建患者表
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    phone VARCHAR(20),
    id_card VARCHAR(18) UNIQUE,
    address TEXT
);

-- 创建预约表
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    time_slot VARCHAR(20),
    status VARCHAR(20) CHECK (status IN ('待确认', '已确认', '已取消')),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
); 