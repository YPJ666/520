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
import java.util.List;

@WebServlet("/patient/appointments")
public class PatientAppointmentsServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // 从请求参数中获取患者ID
            String patientIdStr = req.getParameter("patientId");
            if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
                throw new ServletException("患者ID不能为空");
            }
            
            int patientId = Integer.parseInt(patientIdStr);
            
            // 使用存储过程获取预约记录
            List<Appointment> appointments = appointmentDAO.getPatientAppointmentsByProc(patientId);
            
            req.setAttribute("appointments", appointments);
            req.getRequestDispatcher("/WEB-INF/jsp/patient-appointments.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            throw new ServletException("无效的患者ID", e);
        } catch (Exception e) {
            throw new ServletException("查询患者预约记录失败", e);
        }
    }
} 