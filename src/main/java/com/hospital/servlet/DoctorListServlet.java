package com.hospital.servlet;

import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.TitleDAO;
import com.hospital.dao.impl.DepartmentDAOImpl;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.dao.impl.TitleDAOImpl;
import com.hospital.model.Doctor;
import com.hospital.model.Department;
import com.hospital.model.Title;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/doctor/list")
public class DoctorListServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private TitleDAO titleDAO = new TitleDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            List<Doctor> doctors = doctorDAO.getAll();
            List<Department> departments = departmentDAO.getAll();
            List<Title> titles = titleDAO.getAll();
            
            req.setAttribute("doctors", doctors);
            req.setAttribute("departments", departments);
            req.setAttribute("titles", titles);
            
            System.out.println("医生数量: " + doctors.size());
            System.out.println("科室数量: " + departments.size());
            System.out.println("职称数量: " + titles.size());
            
            req.getRequestDispatcher("/WEB-INF/jsp/doctor-list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("查询医生列表失败", e);
        }
    }
} 