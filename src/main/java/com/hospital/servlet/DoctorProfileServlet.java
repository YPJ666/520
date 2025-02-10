package com.hospital.servlet;

import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.impl.DepartmentDAOImpl;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.model.Doctor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/doctor/profile/*")
public class DoctorProfileServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private static final List<String> TIME_SLOTS = Arrays.asList(
        "08:00-08:30", "08:30-09:00", "09:00-09:30", "09:30-10:00",
        "10:00-10:30", "10:30-11:00", "11:00-11:30", "11:30-12:00",
        "14:00-14:30", "14:30-15:00", "15:00-15:30", "15:30-16:00",
        "16:00-16:30", "16:30-17:00"
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            int doctorId = Integer.parseInt(pathInfo.substring(1));
            Doctor doctor = doctorDAO.getById(doctorId);
            
            if (doctor != null) {
                req.setAttribute("doctor", doctor);
                req.setAttribute("departments", departmentDAO.getAll());
                req.setAttribute("timeSlots", TIME_SLOTS);
                req.getRequestDispatcher("/WEB-INF/jsp/doctor-profile.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "医生不存在");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
} 