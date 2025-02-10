<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>预约信息管理</title>
                <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
                <link rel="stylesheet"
                    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css">
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

                    .search-box {
                        margin-bottom: 20px;
                        display: flex;
                        gap: 10px;
                    }

                    .search-input {
                        flex: 1;
                        padding: 10px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                        font-size: 16px;
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
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2>预约信息管理</h2>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">返回首页</a>
                    </div>

                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>患者姓名</th>
                                <th>医生姓名</th>
                                <th>科室</th>
                                <th>预约日期</th>
                                <th>时间段</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${appointments}" var="appointment">
                                <tr>
                                    <td>${appointment.patientName}</td>
                                    <td>${appointment.doctorName}</td>
                                    <td>${appointment.departmentName}</td>
                                    <td>
                                        <fmt:formatDate value="${appointment.appointmentDate}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td>${appointment.timeSlot}</td>
                                    <td>
                                        <span class="badge badge-${appointment.statusId == 1 ? 'warning' : 
                                                        appointment.statusId == 2 ? 'success' : 
                                                        appointment.statusId == 3 ? 'danger' : 'secondary'}">
                                            ${appointment.statusName}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="btn-group">
                                            <c:if test="${appointment.statusId == 1}">
                                                <button type="button" class="btn btn-sm btn-success"
                                                    onclick="completeAppointment(${appointment.id})">
                                                    <i class="bi bi-check-circle"></i> 完成
                                                </button>
                                                <button type="button" class="btn btn-sm btn-warning"
                                                    onclick="cancelAppointment(${appointment.id})">
                                                    <i class="bi bi-x-circle"></i> 取消
                                                </button>
                                            </c:if>
                                            <button type="button" class="btn btn-sm btn-danger"
                                                onclick="deleteAppointment(${appointment.id})">
                                                <i class="bi bi-trash"></i> 删除
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    function completeAppointment(id) {
                        if (confirm('确认完成此预约？')) {
                            $.ajax({
                                url: '${pageContext.request.contextPath}/api/appointments/complete/' + id,
                                type: 'PUT',
                                success: function () {
                                    location.reload();
                                },
                                error: function (xhr) {
                                    alert('操作失败: ' + (xhr.responseText || '请稍后重试'));
                                }
                            });
                        }
                    }

                    function cancelAppointment(id) {
                        if (confirm('确认取消此预约？')) {
                            $.ajax({
                                url: '${pageContext.request.contextPath}/appointments/' + id,
                                type: 'PUT',
                                data: { action: 'cancel' },
                                success: function () {
                                    location.reload();
                                },
                                error: function () {
                                    alert('操作失败');
                                }
                            });
                        }
                    }

                    function deleteAppointment(id) {
                        if (confirm('确认删除此预约？')) {
                            $.ajax({
                                url: '${pageContext.request.contextPath}/appointments/' + id,
                                type: 'DELETE',
                                success: function () {
                                    location.reload();
                                },
                                error: function () {
                                    alert('删除失败');
                                }
                            });
                        }
                    }
                </script>
            </body>

            </html>