package com.hospital.servlet;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.impl.AppointmentDAOImpl;
import com.hospital.model.Appointment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/appointments/*")
public class AppointmentServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有预约
                List<Appointment> appointments = appointmentDAO.getAll();
                req.setAttribute("appointments", appointments);
                req.getRequestDispatcher("/WEB-INF/jsp/appointment-list.jsp").forward(req, resp);
            } else {
                // 获取指定患者的预约
                int patientId = Integer.parseInt(pathInfo.substring(1));
                List<Appointment> appointments = appointmentDAO.getByPatient(patientId);
                req.setAttribute("appointments", appointments);
                req.getRequestDispatcher("/WEB-INF/jsp/patient-appointments.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("查询预约记录失败", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // 创建新预约
            Appointment appointment = new Appointment();
            appointment.setPatientId(Integer.parseInt(req.getParameter("patientId")));
            appointment.setDoctorId(Integer.parseInt(req.getParameter("doctorId")));
            appointment.setScheduleId(Integer.parseInt(req.getParameter("scheduleId")));
            appointment.setAppointmentDate(dateFormat.parse(req.getParameter("appointmentDate")));
            appointment.setStatusId(1); // 1表示待就诊
            
            appointmentDAO.create(appointment);
            resp.sendRedirect(req.getContextPath() + "/appointments");
            
        } catch (Exception e) {
            throw new ServletException("创建预约失败", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            
            if ("complete".equals(action)) {
                appointmentDAO.completeAppointment(id);
            } else if ("cancel".equals(action)) {
                appointmentDAO.cancelAppointment(id);
            } else {
                Appointment appointment = new Appointment();
                appointment.setId(id);
                appointment.setStatusId(Integer.parseInt(req.getParameter("statusId")));
                appointmentDAO.update(appointment);
            }
            
            resp.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            throw new ServletException("更新预约状态失败", e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean success = appointmentDAO.delete(id);
            
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException("删除预约失败", e);
        }
    }
} 