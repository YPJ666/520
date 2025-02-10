package com.hospital.servlet.api;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.impl.DoctorDAOImpl;
import com.hospital.model.Doctor;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/api/manage/doctors/*")
public class DoctorManageApiServlet extends HttpServlet {
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有医生列表
            objectMapper.writeValue(resp.getWriter(), doctorDAO.getAll());
        } else {
            try {
                // 获取指定ID的医生
                int id = Integer.parseInt(pathInfo.substring(1));
                Doctor doctor = doctorDAO.getById(id);
                if (doctor != null) {
                    objectMapper.writeValue(resp.getWriter(), doctor);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String requestBody = req.getReader().lines().collect(Collectors.joining());
        Doctor doctor = objectMapper.readValue(requestBody, Doctor.class);
        
        doctorDAO.create(doctor);
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), doctor);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            Doctor doctor = objectMapper.readValue(requestBody, Doctor.class);
            doctor.setId(id);
            
            doctorDAO.update(doctor);
            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), doctor);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean success = doctorDAO.delete(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
} 