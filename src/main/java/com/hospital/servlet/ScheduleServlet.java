package com.hospital.servlet;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.ScheduleDAO;
import com.hospital.dao.TimeSlotDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.dao.impl.ScheduleDAOImpl;
import com.hospital.dao.impl.TimeSlotDAOImpl;
import com.hospital.model.Schedule;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet("/schedule/*")
public class ScheduleServlet extends HttpServlet {
    private ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private TimeSlotDAO timeSlotDAO = new TimeSlotDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || "/".equals(pathInfo)) {
            // 显示排班列表页面
            req.setAttribute("schedules", scheduleDAO.getAll());
            req.setAttribute("doctors", doctorDAO.getAvailable());
            req.setAttribute("timeSlots", timeSlotDAO.getAll());
            req.getRequestDispatcher("/WEB-INF/jsp/schedule-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/add".equals(pathInfo)) {
            // 添加新排班
            Schedule schedule = objectMapper.readValue(req.getReader(), Schedule.class);
            schedule.setStatus(1); // 1表示可约
            scheduleDAO.create(schedule);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] parts = pathInfo.split("/");
        
        if (parts.length == 2) {
            int id = Integer.parseInt(parts[1]);
            if (scheduleDAO.delete(id)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
} 