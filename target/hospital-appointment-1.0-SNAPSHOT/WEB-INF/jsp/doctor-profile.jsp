<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>医生个人信息</title>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
        </head>

        <body>
            <div class="container mt-4">
                <div class="row mb-4">
                    <div class="col">
                        <h2>医生个人信息</h2>
                    </div>
                </div>

                <div class="card">
                    <div class="card-body">
                        <form id="doctorForm" method="post" action="${pageContext.request.contextPath}/doctor/profile">
                            <input type="hidden" name="id" value="${doctor.id}">

                            <div class="form-group row">
                                <label class="col-sm-2 col-form-label">姓名</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" name="name" value="${doctor.name}" required>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label class="col-sm-2 col-form-label">所属科室</label>
                                <div class="col-sm-10">
                                    <select class="form-control" name="departmentId" required>
                                        <c:forEach items="${departments}" var="dept">
                                            <option value="${dept.id}" ${dept.id eq doctor.departmentId ? 'selected'
                                                : '' }>
                                                ${dept.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label class="col-sm-2 col-form-label">职称</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" name="title" value="${doctor.title}"
                                        required>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label class="col-sm-2 col-form-label">专长</label>
                                <div class="col-sm-10">
                                    <textarea class="form-control" name="specialty"
                                        rows="3">${doctor.specialty}</textarea>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label class="col-sm-2 col-form-label">出诊时间</label>
                                <div class="col-sm-10">
                                    <div class="schedule-grid">
                                        <c:forEach items="${timeSlots}" var="slot">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="scheduleSlots"
                                                    value="${slot}" ${doctor.schedule.contains(slot) ? 'checked' : '' }>
                                                <label class="form-check-label">${slot}</label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <div class="col-sm-10 offset-sm-2">
                                    <button type="submit" class="btn btn-primary">保存修改</button>
                                    <a href="${pageContext.request.contextPath}/doctor/schedule"
                                        class="btn btn-secondary">返回日程</a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                $(document).ready(function () {
                    $('#doctorForm').on('submit', function (e) {
                        e.preventDefault();

                        const formData = new FormData(this);
                        const scheduleSlots = [];
                        $('input[name="scheduleSlots"]:checked').each(function () {
                            scheduleSlots.push($(this).val());
                        });

                        const data = {
                            id: formData.get('id'),
                            name: formData.get('name'),
                            departmentId: formData.get('departmentId'),
                            title: formData.get('title'),
                            specialty: formData.get('specialty'),
                            schedule: scheduleSlots.join(',')
                        };

                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/manage/doctors/' + data.id,
                            type: 'PUT',
                            contentType: 'application/json',
                            data: JSON.stringify(data),
                            success: function () {
                                alert('保存成功');
                                window.location.reload();
                            },
                            error: function () {
                                alert('保存失败，请重试');
                            }
                        });
                    });
                });
            </script>
        </body>

        </html>