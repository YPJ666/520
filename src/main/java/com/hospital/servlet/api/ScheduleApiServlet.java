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
import java.util.stream.Collectors;

@WebServlet("/api/schedules/*")
public class ScheduleApiServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        try {
            if (pathInfo != null && pathInfo.startsWith("/doctor")) {
                // 获取医生某天的可用时间段
                int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("date"));
                
                List<Schedule> schedules = scheduleDAO.getAvailableByDoctorAndDate(doctorId, date);
                objectMapper.writeValue(resp.getWriter(), schedules);
            } else if (pathInfo.startsWith("/available")) {
                // 获取日期范围内的可用时间段
                String startDateStr = req.getParameter("startDate");
                String endDateStr = req.getParameter("endDate");
                
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = endDateStr != null ? dateFormat.parse(endDateStr) : startDate;
                
                List<Schedule> schedules = scheduleDAO.getAvailableByDateRange(startDate, endDate);
                writeJson(resp, schedules);
            } else if (pathInfo.startsWith("/doctor/")) {
                // 获取医生的排班
                int doctorId = Integer.parseInt(pathInfo.substring("/doctor/".length()));
                String dateStr = req.getParameter("date");
                List<Schedule> schedules;
                
                if (dateStr != null) {
                    Date date = dateFormat.parse(dateStr);
                    schedules = scheduleDAO.getByDoctorAndDate(doctorId, date);
                } else {
                    schedules = scheduleDAO.getByDoctor(doctorId);
                }
                objectMapper.writeValue(resp.getWriter(), schedules);
            } else {
                // 获取指定ID的排班
                int id = Integer.parseInt(pathInfo.substring(1));
                Schedule schedule = scheduleDAO.getById(id);
                if (schedule != null) {
                    objectMapper.writeValue(resp.getWriter(), schedule);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            if ("/batch".equals(pathInfo)) {
                // 批量创建排班
                String requestBody = req.getReader().lines().collect(Collectors.joining());
                Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
                
                int doctorId = (Integer) data.get("doctorId");
                Date startDate = dateFormat.parse((String) data.get("startDate"));
                Date endDate = dateFormat.parse((String) data.get("endDate"));
                List<String> timeSlots = (List<String>) data.get("timeSlots");
                List<Integer> workDays = (List<Integer>) data.get("workDays");
                
                List<Schedule> schedules = generateSchedules(doctorId, startDate, endDate, timeSlots, workDays);
                scheduleDAO.batchCreate(schedules);
                
                resp.setContentType("application/json;charset=UTF-8");
                objectMapper.writeValue(resp.getWriter(), schedules);
            } else {
                // 创建单个排班
                String requestBody = req.getReader().lines().collect(Collectors.joining());
                Schedule schedule = objectMapper.readValue(requestBody, Schedule.class);
                scheduleDAO.create(schedule);
                
                resp.setContentType("application/json;charset=UTF-8");
                objectMapper.writeValue(resp.getWriter(), schedule);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            Schedule schedule = objectMapper.readValue(requestBody, Schedule.class);
            schedule.setId(id);
            
            scheduleDAO.update(schedule);
            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), schedule);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean success = scheduleDAO.delete(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Schedule> generateSchedules(int doctorId, Date startDate, Date endDate, 
            List<String> timeSlots, List<Integer> workDays) {
        List<Schedule> schedules = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        while (!cal.getTime().after(endDate)) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            // Calendar.MONDAY is 2, but our workDays list uses 1 for Monday
            if (workDays.contains(dayOfWeek - 1)) {
                for (String timeSlot : timeSlots) {
                    Schedule schedule = new Schedule();
                    schedule.setDoctorId(doctorId);
                    schedule.setDate(cal.getTime());
                    schedule.setTimeSlot(timeSlot);
                    schedule.setStatus(1); // 1表示可约
                    schedules.add(schedule);
                }
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return schedules;
    }

    private void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), obj);
    }
} 