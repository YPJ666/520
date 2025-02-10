DELIMITER //

-- 新增预约时更新排班状态
CREATE TRIGGER after_appointment_insert
AFTER INSERT ON appointments
FOR EACH ROW
BEGIN
    UPDATE schedules 
    SET status = 2 -- 设置为已约
    WHERE id = NEW.schedule_id;
END //

-- 取消预约时更新排班状态
CREATE TRIGGER after_appointment_update
AFTER UPDATE ON appointments
FOR EACH ROW
BEGIN
    IF NEW.status_id = 3 THEN -- 如果状态改为已取消
        UPDATE schedules 
        SET status = 1 -- 恢复为可约状态
        WHERE id = NEW.schedule_id;
    END IF;
END //

-- 删除预约时更新排班状态
CREATE TRIGGER after_appointment_delete
AFTER DELETE ON appointments
FOR EACH ROW
BEGIN
    UPDATE schedules 
    SET status = 1 -- 恢复为可约状态
    WHERE id = OLD.schedule_id;
END //

DELIMITER ; 