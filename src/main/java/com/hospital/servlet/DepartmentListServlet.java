package com.hospital.servlet;

import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.impl.DepartmentDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/department/list")
public class DepartmentListServlet extends HttpServlet {
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setAttribute("departments", departmentDAO.getAll());
        req.getRequestDispatcher("/WEB-INF/jsp/department-list.jsp").forward(req, resp);
    }
} 