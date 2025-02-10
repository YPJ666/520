SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS hospital;
CREATE DATABASE IF NOT EXISTS hospital DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hospital;

-- 先删除所有表（注意删除顺序，先删除有外键引用的表）
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS titles;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS time_slots;
DROP TABLE IF EXISTS appointment_status;

-- 创建基础表（没有外键依赖的表）
CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    location VARCHAR(100),
    description TEXT
);

CREATE TABLE IF NOT EXISTS titles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL COMMENT '职称级别：1-初级，2-中级，3-副高，4-正高',
    description TEXT
);

CREATE TABLE IF NOT EXISTS time_slots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    period VARCHAR(20) COMMENT '时段：上午/下午',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用'
);

CREATE TABLE IF NOT EXISTS appointment_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    phone VARCHAR(20),
    id_card VARCHAR(18) UNIQUE,
    address TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建有外键依赖的表
CREATE TABLE IF NOT EXISTS doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    department_id INT,
    title_id INT COMMENT '职称ID',
    specialty TEXT,
    status TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (title_id) REFERENCES titles(id)
);

CREATE TABLE IF NOT EXISTS schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT,
    date DATE,
    time_slot_id INT,
    status TINYINT DEFAULT 1 COMMENT '状态：0-停诊，1-可约，2-已约',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(id)
);

CREATE TABLE IF NOT EXISTS appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    schedule_id INT,
    appointment_date DATE,
    status_id INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id),
    FOREIGN KEY (status_id) REFERENCES appointment_status(id)
);

-- 插入一些测试数据
INSERT INTO departments (name, location, description) VALUES 
('内科', '门诊楼1楼', '治疗内科疾病'),
('外科', '门诊楼2楼', '进行外科手术'),
('儿科', '门诊楼1楼', '专门治疗儿童疾病'),
('妇科', '门诊楼3楼', '妇科疾病诊治'),
('骨科', '门诊楼2楼', '骨科疾病和外伤治疗');

INSERT INTO titles (name, level, description) VALUES
('主任医师', 4, '最高级别的临床医生职称'),
('副主任医师', 3, '副高级职称'),
('主治医师', 2, '中级职称'),
('住院医师', 1, '初级职称');

-- 插入一些测试医生数据
INSERT INTO doctors (name, department_id, title_id, specialty, status) VALUES 
('张三', 1, 1, '内科常见病', 1),
('李四', 1, 2, '心血管疾病', 1),
('王五', 2, 1, '外科手术', 1),
('赵六', 2, 2, '骨科手术', 1),
('钱七', 3, 1, '儿科疾病', 1);

-- 插入时间段数据
INSERT INTO time_slots (start_time, end_time, period) VALUES 
('08:00:00', '09:00:00', '上午'),
('09:00:00', '10:00:00', '上午'),
('10:00:00', '11:00:00', '上午'),
('11:00:00', '12:00:00', '上午'),
('14:00:00', '15:00:00', '下午'),
('15:00:00', '16:00:00', '下午'),
('16:00:00', '17:00:00', '下午');

-- 插入预约状态数据
INSERT INTO appointment_status (name, description) VALUES 
('待就诊', '已预约，等待就诊'),
('已完成', '已完成就诊'),
('已取消', '预约已取消'),
('爽约', '患者未按时就诊');

-- 插入一些测试预约数据
INSERT INTO patients (name, gender, phone, id_card, address) VALUES 
('测试患者', '男', '13800138000', '110101199001011234', '测试地址');

-- 插入医生排班数据
INSERT INTO schedules (doctor_id, date, time_slot_id, status) VALUES 
-- 内科医生(张三)的排班
(1, CURDATE(), 1, 1), -- 上午8-9点
(1, CURDATE(), 2, 1), -- 上午9-10点
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 1),
-- 内科医生(李四)的排班
(2, CURDATE(), 3, 1), -- 上午10-11点
(2, CURDATE(), 4, 1), -- 上午11-12点
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 3, 1),
-- 外科医生(王五)的排班
(3, CURDATE(), 5, 1), -- 下午2-3点
(3, CURDATE(), 6, 1), -- 下午3-4点
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 5, 1);

INSERT INTO appointments (patient_id, doctor_id, schedule_id, appointment_date, status_id) VALUES 
(1, 1, 1, CURDATE(), 1);

-- 添加索引
ALTER TABLE schedules ADD INDEX idx_doctor_date (doctor_id, date);
ALTER TABLE schedules ADD INDEX idx_date_status (date, status);
ALTER TABLE appointments ADD INDEX idx_patient_date (patient_id, appointment_date);

-- 添加唯一约束防止重复预约
ALTER TABLE appointments 
ADD CONSTRAINT uk_schedule_appointment UNIQUE (schedule_id);

-- 添加检查约束
ALTER TABLE schedules 
ADD CONSTRAINT chk_schedule_status 
CHECK (status IN (0, 1, 2)); -- 0-停诊，1-可约，2-已约 

-- 修改预约状态表，确保状态值的规范性
ALTER TABLE appointment_status 
ADD CONSTRAINT chk_appointment_status_name 
CHECK (name IN ('待确认', '已确认', '已取消'));

-- 添加更多的参照完整性约束
ALTER TABLE appointments
ADD CONSTRAINT fk_appointment_patient
FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_appointment_doctor
FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_appointment_schedule
FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_appointment_status
FOREIGN KEY (status_id) REFERENCES appointment_status(id) ON DELETE RESTRICT;

-- 添加医生排班的约束
ALTER TABLE schedules
ADD CONSTRAINT fk_schedule_doctor
FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_schedule_timeslot
FOREIGN KEY (time_slot_id) REFERENCES time_slots(id) ON DELETE RESTRICT; 