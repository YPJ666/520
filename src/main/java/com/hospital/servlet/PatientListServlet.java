package com.hospital.servlet;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.impl.PatientDAOImpl;
import com.hospital.model.Patient;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.model.Doctor;
import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.impl.DepartmentDAOImpl;
import com.hospital.model.Department;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/patient/list")
public class PatientListServlet extends HttpServlet {
    private PatientDAO patientDAO = new PatientDAOImpl();
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            List<Patient> patients = patientDAO.getAll();
            List<Department> departments = departmentDAO.getAll();
            
            req.setAttribute("patients", patients);
            req.setAttribute("departments", departments);
            
            req.getRequestDispatcher("/WEB-INF/jsp/patient-list.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("加载患者列表失败", e);
        }
    }
} 