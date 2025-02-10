package com.hospital.servlet;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.model.Doctor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/doctor/schedule/*")
public class DoctorScheduleServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        System.out.println("访问排班管理，pathInfo: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少医生ID");
                return;
            }

            int doctorId = Integer.parseInt(pathInfo.substring(1));
            System.out.println("查询医生ID: " + doctorId);
            
            Doctor doctor = doctorDAO.getById(doctorId);
            System.out.println("查询到的医生: " + (doctor != null ? doctor.getName() : "null"));
            
            if (doctor != null) {
                req.setAttribute("doctor", doctor);
                req.getRequestDispatcher("/WEB-INF/jsp/doctor-schedule.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "医生不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("排班管理异常: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
        }
    }
} 