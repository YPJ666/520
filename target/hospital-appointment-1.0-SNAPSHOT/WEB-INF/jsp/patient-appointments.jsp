<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>患者预约记录</title>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
            <style>
                body {
                    background-color: #f5f5f5;
                    padding: 20px;
                }

                .container {
                    background-color: #fff;
                    border-radius: 10px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                    padding: 20px;
                }

                .button {
                    background-color: #007aff;
                    color: #fff;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    font-size: 16px;
                    transition: background-color 0.3s;
                    margin-right: 10px;
                    margin-bottom: 20px;
                }

                .button:hover {
                    background-color: #0056b3;
                    color: #fff;
                    text-decoration: none;
                }

                .table {
                    margin-top: 20px;
                }

                .action-btn {
                    background-color: #007aff;
                    color: #fff;
                    border: none;
                    padding: 5px 10px;
                    border-radius: 3px;
                    font-size: 14px;
                    margin: 0 5px;
                    cursor: pointer;
                }

                .action-btn:hover {
                    background-color: #0056b3;
                    color: #fff;
                    text-decoration: none;
                }

                .patient-info {
                    background-color: #f8f9fa;
                    padding: 15px;
                    border-radius: 5px;
                    margin-bottom: 20px;
                }

                .patient-info h3 {
                    margin: 0;
                    color: #333;
                }

                .patient-info p {
                    margin: 5px 0 0;
                    color: #666;
                }

                .status-pending {
                    color: #ffc107;
                }

                .status-confirmed {
                    color: #28a745;
                }

                .status-cancelled {
                    color: #dc3545;
                }
            </style>
        </head>

        <body>
            <div class="container">
                <div class="patient-info">
                    <h3>${patient.name}</h3>
                    <p>性别：${patient.gender} | 联系电话：${patient.phone}</p>
                    <p>身份证号：${patient.idCard}</p>
                    <p>地址：${patient.address}</p>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>预约记录</h2>
                    <button class="button"
                        onclick="window.location.href='${pageContext.request.contextPath}/appointment/new?patientId=${patient.id}'">
                        新增预约
                    </button>
                </div>

                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>预约日期</th>
                                <th>时间段</th>
                                <th>科室</th>
                                <th>医生</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${appointments}" var="appointment">
                                <tr>
                                    <td>${appointment.appointmentDate}</td>
                                    <td>${appointment.timeSlot}</td>
                                    <td>${appointment.department}</td>
                                    <td>${appointment.doctorName}</td>
                                    <td class="status-${appointment.status.toLowerCase()}">${appointment.status}</td>
                                    <td>
                                        <button class="action-btn"
                                            onclick="editAppointment('${appointment.id}')">修改</button>
                                        <button class="action-btn"
                                            onclick="cancelAppointment('${appointment.id}')">取消</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                function editAppointment(id) {
                    window.location.href = '${pageContext.request.contextPath}/appointment/edit/' + id;
                }

                function cancelAppointment(id) {
                    if (confirm('确定要取消这个预约吗？')) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/appointments/' + id + '/cancel',
                            type: 'POST',
                            success: function () {
                                window.location.reload();
                            },
                            error: function () {
                                alert('取消预约失败，请重试');
                            }
                        });
                    }
                }
            </script>
        </body>

        </html>