USE hospital;
DELIMITER //

DROP PROCEDURE IF EXISTS GetPatientAppointments//
CREATE PROCEDURE GetPatientAppointments(IN p_patient_id INT)
BEGIN
    SELECT 
        a.*,
        p.name AS patient_name,
        d.name AS doctor_name,
        dept.name AS department_name,
        s.date AS appointment_date,
        ts.start_time,
        ts.end_time,
        ts.period,
        ast.name AS status_name
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    JOIN doctors d ON a.doctor_id = d.id
    JOIN departments dept ON d.department_id = dept.id
    JOIN schedules s ON a.schedule_id = s.id
    JOIN time_slots ts ON s.time_slot_id = ts.id
    JOIN appointment_status ast ON a.status_id = ast.id
    WHERE a.patient_id = p_patient_id
    ORDER BY s.date DESC, ts.start_time;
END//

DROP PROCEDURE IF EXISTS GetDoctorDaySchedule//
CREATE PROCEDURE GetDoctorDaySchedule(
    IN p_doctor_id INT,
    IN p_schedule_date DATE
)
BEGIN
    SELECT 
        s.*,
        ts.start_time,
        ts.end_time,
        ts.period,
        COALESCE(a.patient_name, '空闲') AS patient_name,
        CASE 
            WHEN s.status = 0 THEN '停诊'
            WHEN s.status = 1 THEN '可预约'
            WHEN s.status = 2 THEN '已约'
            ELSE '未知'
        END AS status_name
    FROM schedules s
    JOIN time_slots ts ON s.time_slot_id = ts.id
    LEFT JOIN (
        SELECT 
            a.schedule_id,
            p.name AS patient_name,
            ast.name AS status_name
        FROM appointments a
        JOIN patients p ON a.patient_id = p.id
        JOIN appointment_status ast ON a.status_id = ast.id
    ) a ON s.id = a.schedule_id
    WHERE s.doctor_id = p_doctor_id
    AND s.date = p_schedule_date
    ORDER BY ts.start_time;
END//

DELIMITER ; 