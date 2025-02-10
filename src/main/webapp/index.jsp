<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>在线预约系统</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <style>
            body {
                background-color: #f5f5f5;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }

            .container {
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                padding: 20px;
                width: 300px;
                text-align: center;
            }

            .button {
                display: block;
                margin: 10px 0;
                padding: 10px 20px;
                background-color: #007aff;
                color: #fff;
                text-decoration: none;
                border-radius: 5px;
                font-size: 16px;
                transition: background-color 0.3s;
            }

            .button:hover {
                background-color: #0056b3;
                color: #fff;
                text-decoration: none;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <h1>在线预约系统</h1>
            <a href="${pageContext.request.contextPath}/doctor/list" class="button">医生信息管理</a>
            <a href="${pageContext.request.contextPath}/department/list" class="button">科室信息管理</a>
            <a href="${pageContext.request.contextPath}/patient/list" class="button">患者信息管理</a>
            <a href="${pageContext.request.contextPath}/appointment/list" class="button">预约信息管理</a>
        </div>
    </body>

    </html>