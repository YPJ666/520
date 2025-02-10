package com.hospital.servlet.api;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.ScheduleDAO;
import com.hospital.dao.impl.AppointmentDAOImpl;
import com.hospital.dao.impl.ScheduleDAOImpl;
import com.hospital.model.Appointment;
import com.hospital.model.Schedule;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/api/appointments/*")
public class AppointmentApiServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                objectMapper.writeValue(resp.getWriter(), appointmentDAO.getAll());
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Appointment appointment = appointmentDAO.getById(id);
                if (appointment != null) {
                    objectMapper.writeValue(resp.getWriter(), appointment);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            Appointment appointment = objectMapper.readValue(req.getReader(), Appointment.class);
            
            // 检查时间段是否已被预约
            Schedule schedule = scheduleDAO.getById(appointment.getScheduleId());
            if (schedule == null || schedule.getStatus() != 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "该时间段不可预约");
                return;
            }
            
            // 创建预约并更新排班状态
            appointmentDAO.create(appointment);
            schedule.setStatus(2); // 设置为已约
            scheduleDAO.updateStatus(schedule.getId(), 2);
            
            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), appointment);
            
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (pathInfo.startsWith("/complete/")) {
                // 完成就诊
                int id = Integer.parseInt(pathInfo.substring("/complete/".length()));
                Appointment appointment = appointmentDAO.getById(id);
                if (appointment == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // 更新预约状态为已完成
                appointment.setStatusId(2); // 2-已完成
                appointmentDAO.update(appointment);

                resp.setContentType("application/json;charset=UTF-8");
                objectMapper.writeValue(resp.getWriter(), appointment);
            } else if (pathInfo.startsWith("/cancel/")) {
                // 取消预约
                int id = Integer.parseInt(pathInfo.substring("/cancel/".length()));
                Appointment appointment = appointmentDAO.getById(id);
                if (appointment == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // 更新预约状态为已取消
                appointment.setStatusId(3); // 3-已取消
                appointmentDAO.update(appointment);

                // 恢复排班状态为可约
                scheduleDAO.updateStatus(appointment.getScheduleId(), 1);

                resp.setContentType("application/json;charset=UTF-8");
                objectMapper.writeValue(resp.getWriter(), appointment);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            boolean success = appointmentDAO.delete(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
} 