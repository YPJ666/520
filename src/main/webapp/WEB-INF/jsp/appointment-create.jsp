<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>预约挂号</title>
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
                    margin-top: 20px;
                }
            </style>
        </head>

        <body>
            <div class="container">
                <h2 class="mb-4">预约挂号</h2>
                <form id="appointmentForm" method="post" action="${pageContext.request.contextPath}/appointments">
                    <div class="form-group">
                        <label for="patientId">患者</label>
                        <select class="form-control" id="patientId" name="patientId" required>
                            <option value="">请选择患者</option>
                            <c:forEach items="${patients}" var="patient">
                                <option value="${patient.id}">${patient.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="doctorId">医生</label>
                        <select class="form-control" id="doctorId" name="doctorId" required>
                            <option value="">请选择医生</option>
                            <c:forEach items="${doctors}" var="doctor">
                                <option value="${doctor.id}">${doctor.name} - ${doctor.departmentName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="appointmentDate">预约日期</label>
                        <input type="date" class="form-control" id="appointmentDate" name="appointmentDate" required>
                    </div>

                    <div class="form-group">
                        <label for="scheduleId">时间段</label>
                        <select class="form-control" id="scheduleId" name="scheduleId" required>
                            <option value="">请先选择医生和日期</option>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">提交预约</button>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">返回首页</a>
                </form>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
            <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
            <script>
                $(function () {
                    // 当医生或日期改变时，获取可用时间段
                    $('#doctorId, #appointmentDate').change(function () {
                        const doctorId = $('#doctorId').val();
                        const appointmentDate = $('#appointmentDate').val();

                        if (doctorId && appointmentDate) {
                            $.get('${pageContext.request.contextPath}/api/doctors/' + doctorId + '/schedules', {
                                date: appointmentDate
                            })
                                .done(function (schedules) {
                                    const $scheduleId = $('#scheduleId');
                                    $scheduleId.empty().append('<option value="">请选择时间段</option>');

                                    schedules.forEach(function (schedule) {
                                        if (schedule.status === 1) { // 1表示可约
                                            $scheduleId.append(`<option value="${schedule.id}">${schedule.timeSlot}</option>`);
                                        }
                                    });
                                })
                                .fail(function () {
                                    alert('获取可用时间段失败');
                                });
                        }
                    });
                });
            </script>
        </body>

        </html>