package com.hospital.servlet.api;

import com.hospital.dao.ScheduleDAO;
import com.hospital.dao.impl.ScheduleDAOImpl;
import com.hospital.model.Schedule;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/api/doctors/*/schedule")
public class DoctorScheduleApiServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        int doctorId = Integer.parseInt(pathInfo.substring(1));
        
        // 获取指定周的日期范围
        int weekOffset = 0;
        String weekParam = req.getParameter("week");
        if (weekParam != null) {
            weekOffset = Integer.parseInt(weekParam);
        }
        
        Date[] weekDates = getWeekDates(weekOffset);
        List<Schedule> schedules = scheduleDAO.getByDoctorAndDateRange(doctorId, weekDates[0], weekDates[1]);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = convertToWeekSchedule(schedules);
        
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), result);
    }

    private Date[] getWeekDates(int weekOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, weekOffset);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_WEEK, 4); // 到周五
        Date endDate = cal.getTime();
        
        return new Date[]{startDate, endDate};
    }

    private List<Map<String, Object>> convertToWeekSchedule(List<Schedule> schedules) {
        Map<String, List<Schedule>> timeSlotMap = new TreeMap<>();
        
        // 按时间段分组
        for (Schedule schedule : schedules) {
            String timeSlot = schedule.getTimeSlot();
            timeSlotMap.computeIfAbsent(timeSlot, k -> new ArrayList<>()).add(schedule);
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Schedule>> entry : timeSlotMap.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("timeSlot", entry.getKey());
            
            // 设置每天的排班状态
            for (Schedule schedule : entry.getValue()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(schedule.getDate());
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2; // 转换为0-6
                if (dayOfWeek >= 0 && dayOfWeek < 5) { // 只处理周一到周五
                    row.put("day" + dayOfWeek, schedule.getStatus() == 1);
                }
            }
            
            result.add(row);
        }
        
        return result;
    }
} 