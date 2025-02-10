package com.hospital.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital?" +
            "useUnicode=true" +
            "&characterEncoding=utf8" +
            "&serverTimezone=Asia/Shanghai" +
            "&useSSL=false" +
            "&allowPublicKeyRetrieval=true" +
            "&characterSetResults=UTF-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL驱动加载失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 设置连接的字符集
            conn.createStatement().execute("SET NAMES utf8mb4");
            System.out.println("数据库连接成功");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("数据库连接失败: " + e.getMessage());
            throw new SQLException("数据库连接失败: " + e.getMessage(), e);
        }
    }

    private static void initDatabase() {
        try {
            // 1. 初始化数据库表和基础数据
            initTablesAndData();
            
            // 2. 创建存储过程
            initProcedures();
            
            System.out.println("数据库初始化完成");
        } catch (Exception e) {
            System.err.println("数据库初始化失败");
            e.printStackTrace();
        }
    }

    private static void initTablesAndData() throws Exception {
        // 读取并执行init.sql
        InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("init.sql");
        if (is == null) {
            throw new RuntimeException("找不到init.sql文件");
        }
        executeScript(is);
    }

    private static void initProcedures() throws Exception {
        // 读取并执行procedure.sql
        InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("procedure.sql");
        if (is == null) {
            throw new RuntimeException("找不到procedure.sql文件");
        }
        
        String sql = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        
        try (Connection conn = getConnection()) {
            // 直接执行完整的存储过程创建语句
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP PROCEDURE IF EXISTS GetPatientAppointments");
                stmt.execute(sql);
                System.out.println("存储过程创建成功");
            }
        }
    }

    private static void executeScript(InputStream is) throws Exception {
        String sqlContent = new BufferedReader(new InputStreamReader(is, "UTF-8"))
                .lines().collect(Collectors.joining("\n"));

        String[] sqlStatements = sqlContent.split(";");

        try (Connection conn = getConnection()) {
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        System.out.println("执行SQL: " + sql);
                        stmt.execute(sql);
                        System.out.println("执行SQL成功");
                    } catch (SQLException e) {
                        System.err.println("执行SQL失败: " + sql);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
} 