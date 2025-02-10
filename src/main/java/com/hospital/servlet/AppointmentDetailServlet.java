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

@WebServlet("/appointment/detail/*")
public class AppointmentDetailServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            int appointmentId = Integer.parseInt(pathInfo.substring(1));
            Appointment appointment = appointmentDAO.getById(appointmentId);
            
            if (appointment != null) {
                req.setAttribute("appointment", appointment);
                req.getRequestDispatcher("/WEB-INF/jsp/appointment-detail.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "预约不存在");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
} 