package com.hospital.servlet;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.impl.AppointmentDAOImpl;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.dao.impl.PatientDAOImpl;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/appointment/list")
public class AppointmentListServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private PatientDAO patientDAO = new PatientDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            System.out.println("访问预约列表页面");
            List<Appointment> appointments = appointmentDAO.getAll();
            List<Doctor> doctors = doctorDAO.getAvailable();
            List<Patient> patients = patientDAO.getAll();
            
            req.setAttribute("appointments", appointments);
            req.setAttribute("doctors", doctors);
            req.setAttribute("patients", patients);
            
            req.getRequestDispatcher("/WEB-INF/jsp/appointment-list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("加载预约列表失败", e);
        }
    }
} 