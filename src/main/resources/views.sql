CREATE OR REPLACE VIEW doctor_available_slots AS
SELECT 
    d.id AS doctor_id,
    d.name AS doctor_name,
    dept.name AS department_name,
    s.date,
    ts.start_time,
    ts.end_time,
    ts.period,
    s.id AS schedule_id,
    CASE 
        WHEN s.status = 1 THEN '可预约'
        WHEN s.status = 2 THEN '已约满'
        ELSE '停诊'
    END AS status
FROM doctors d
JOIN departments dept ON d.department_id = dept.id
JOIN schedules s ON d.id = s.doctor_id
JOIN time_slots ts ON s.time_slot_id = ts.id
WHERE s.status = 1 -- 只显示可预约的时间段
AND s.date >= CURDATE() -- 只显示当前日期及以后的时间段
ORDER BY d.name, s.date, ts.start_time; 