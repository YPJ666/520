package com.hospital.servlet.api;

import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.ScheduleDAO;
import com.hospital.dao.impl.DepartmentDAOImpl;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.dao.impl.ScheduleDAOImpl;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Schedule;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Calendar;
import java.util.stream.Collectors;

@WebServlet("/api/departments/*")
public class DepartmentApiServlet extends HttpServlet {
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有科室
                List<Department> departments = departmentDAO.getAll();
                objectMapper.writeValue(resp.getWriter(), departments);
            } else if (pathInfo.endsWith("/doctors")) {
                // 获取科室下的医生列表
                int deptId = Integer.parseInt(pathInfo.split("/")[1]);
                System.out.println("正在查询科室ID: " + deptId + " 的医生");
                
                // 确保在数据库中有排班数据
                addTestSchedules(deptId);
                
                List<Doctor> doctors = doctorDAO.getByDepartment(deptId);
                System.out.println("找到 " + doctors.size() + " 名医生");
                
                objectMapper.writeValue(resp.getWriter(), doctors);
            } else {
                // 获取指定ID的科室
                int id = Integer.parseInt(pathInfo.substring(1));
                Department department = departmentDAO.getById(id);
                if (department != null) {
                    objectMapper.writeValue(resp.getWriter(), department);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            System.err.println("处理请求失败: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 添加测试排班数据的辅助方法
    private void addTestSchedules(int departmentId) {
        try {
            // 获取该科室的医生
            List<Doctor> doctors = doctorDAO.getAll().stream()
                .filter(d -> d.getDepartmentId() == departmentId)
                .collect(Collectors.toList());
            
            // 为每个医生添加未来7天的排班
            Calendar cal = Calendar.getInstance();
            
            for (Doctor doctor : doctors) {
                for (int i = 0; i < 7; i++) {
                    Schedule schedule = new Schedule();
                    schedule.setDoctorId(doctor.getId());
                    schedule.setDate(cal.getTime());
                    schedule.setTimeSlotId(1); // 使用第一个时间段
                    schedule.setStatus(1); // 1表示可约
                    
                    try {
                        scheduleDAO.create(schedule);
                    } catch (Exception e) {
                        // 忽略重复排班的错误
                        System.out.println("排班可能已存在: " + e.getMessage());
                    }
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            System.err.println("添加测试排班失败: " + e.getMessage());
        }
    }
} 