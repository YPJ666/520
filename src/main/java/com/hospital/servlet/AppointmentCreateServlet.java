package com.hospital.servlet;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.dao.impl.PatientDAOImpl;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/appointment/create")
public class AppointmentCreateServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private PatientDAO patientDAO = new PatientDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            System.out.println("访问预约挂号页面");
            List<Doctor> doctors = doctorDAO.getAvailable();
            List<Patient> patients = patientDAO.getAll();
            
            req.setAttribute("doctors", doctors);
            req.setAttribute("patients", patients);
            
            req.getRequestDispatcher("/WEB-INF/jsp/appointment-create.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("加载预约页面失败", e);
        }
    }
} 