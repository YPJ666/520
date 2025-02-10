package com.hospital.servlet.api;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.model.Doctor;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/api/doctors/*/available-time")
public class DoctorAvailableTimeServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] parts = pathInfo.split("/");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            int doctorId = Integer.parseInt(parts[1]);
            Doctor doctor = doctorDAO.getById(doctorId);
            
            if (doctor == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Date startDate = dateFormat.parse(req.getParameter("startDate"));
            Date endDate = dateFormat.parse(req.getParameter("endDate"));
            
            List<Map<String, Object>> availableTimes = getAvailableTime(doctor, startDate, endDate);
            objectMapper.writeValue(resp.getWriter(), availableTimes);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Map<String, Object>> getAvailableTime(Doctor doctor, Date startDate, Date endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        String[] defaultTimeSlots = {
            "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };

        while (!cal.getTime().after(endDate)) {
            Date currentDate = cal.getTime();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            
            // 只查询工作日（周一到周五）
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                if (isAvailable(doctor, currentDate)) {
                    Map<String, Object> daySchedule = new HashMap<>();
                    daySchedule.put("date", currentDate);
                    daySchedule.put("timeSlots", defaultTimeSlots);
                    result.add(daySchedule);
                }
            }
            
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }

    private boolean isAvailable(Doctor doctor, Date date) {
        String schedule = doctor.getSchedule();
        if (schedule == null || schedule.isEmpty()) {
            return false;
        }
        
        String dateStr = dateFormat.format(date);
        String[] schedules = schedule.split(",");
        
        for (String s : schedules) {
            if (s.startsWith(dateStr + "|")) {
                return true;
            }
        }
        return false;
    }
} 