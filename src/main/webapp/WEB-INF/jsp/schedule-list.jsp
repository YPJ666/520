<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>排班管理</title>
                <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
                <link rel="stylesheet"
                    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css">
            </head>

            <body>
                <div class="container mt-4">
                    <h2>排班管理</h2>

                    <div class="row mb-3">
                        <div class="col">
                            <button type="button" class="btn btn-primary" data-toggle="modal"
                                data-target="#addScheduleModal">
                                <i class="bi bi-plus"></i> 新增排班
                            </button>
                        </div>
                    </div>

                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>医生</th>
                                <th>科室</th>
                                <th>日期</th>
                                <th>时间段</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${schedules}" var="schedule">
                                <tr>
                                    <td>${schedule.doctorName}</td>
                                    <td>${schedule.departmentName}</td>
                                    <td>
                                        <fmt:formatDate value="${schedule.date}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td>${schedule.timeSlot}</td>
                                    <td>
                                        <span
                                            class="badge badge-${schedule.status == 1 ? 'success' : schedule.status == 2 ? 'warning' : 'danger'}">
                                            ${schedule.status == 1 ? '可约' : schedule.status == 2 ? '已约' : '停诊'}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-danger" onclick="deleteSchedule(${schedule.id})">
                                            <i class="bi bi-trash"></i> 删除
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- 新增排班模态框 -->
                <div class="modal fade" id="addScheduleModal">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">新增排班</h5>
                                <button type="button" class="close" data-dismiss="modal">&times;</button>
                            </div>
                            <div class="modal-body">
                                <form id="scheduleForm">
                                    <div class="form-group">
                                        <label>医生</label>
                                        <select class="form-control" name="doctorId" required>
                                            <c:forEach items="${doctors}" var="doctor">
                                                <option value="${doctor.id}">${doctor.name} - ${doctor.departmentName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>日期</label>
                                        <input type="date" class="form-control" name="date" required>
                                    </div>
                                    <div class="form-group">
                                        <label>时间段</label>
                                        <select class="form-control" name="timeSlotId" required>
                                            <c:forEach items="${timeSlots}" var="timeSlot">
                                                <option value="${timeSlot.id}">
                                                    ${timeSlot.startTime}-${timeSlot.endTime} (${timeSlot.period})
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" onclick="addSchedule()">保存</button>
                            </div>
                        </div>
                    </div>
                </div>

                <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    function addSchedule() {
                        const formData = new FormData(document.getElementById('scheduleForm'));
                        const data = Object.fromEntries(formData.entries());

                        $.ajax({
                            url: '${pageContext.request.contextPath}/schedule/add',
                            type: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify(data),
                            success: function () {
                                location.reload();
                            },
                            error: function () {
                                alert('添加排班失败');
                            }
                        });
                    }

                    function deleteSchedule(id) {
                        if (confirm('确认删除此排班？')) {
                            $.ajax({
                                url: '${pageContext.request.contextPath}/schedule/' + id,
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