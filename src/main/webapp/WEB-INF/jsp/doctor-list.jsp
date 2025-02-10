<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>医生管理</title>
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

                .modal-content {
                    border-radius: 10px;
                }

                .form-control {
                    border-radius: 5px;
                    border: 1px solid #ddd;
                }

                .form-control:focus {
                    border-color: #007aff;
                    box-shadow: 0 0 0 0.2rem rgba(0, 122, 255, 0.25);
                }
            </style>
        </head>

        <body>
            <div class="container">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>医生管理</h2>
                    <button class="btn btn-primary" data-toggle="modal" data-target="#doctorModal">新增医生</button>
                </div>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>姓名</th>
                            <th>科室</th>
                            <th>职称</th>
                            <th>专长</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${doctors}" var="doctor">
                            <tr>
                                <td>${doctor.name}</td>
                                <td>${doctor.departmentName}</td>
                                <td>${doctor.titleName}</td>
                                <td>${doctor.specialty}</td>
                                <td>${doctor.status == 1 ? '在职' : '离职'}</td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary"
                                            onclick="editDoctor(${doctor.id})">
                                            <i class="bi bi-pencil"></i> 编辑
                                        </button>
                                        <a href="${pageContext.request.contextPath}/schedule"
                                            class="btn btn-sm btn-info">
                                            <i class="bi bi-calendar"></i> 排班管理
                                        </a>
                                        <button type="button" class="btn btn-sm btn-danger"
                                            onclick="deleteDoctor(${doctor.id})">
                                            <i class="bi bi-trash"></i> 删除
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- 医生信息编辑模态框 -->
            <div class="modal fade" id="doctorModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalTitle">医生信息</h5>
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                        </div>
                        <div class="modal-body">
                            <form id="doctorForm">
                                <input type="hidden" id="doctorId" name="id">

                                <div class="form-group">
                                    <label>姓名</label>
                                    <input type="text" class="form-control" id="doctorName" name="name" required>
                                </div>

                                <div class="form-group">
                                    <label>科室</label>
                                    <select class="form-control" id="departmentId" name="departmentId" required>
                                        <option value="">请选择科室</option>
                                        <c:forEach items="${departments}" var="dept">
                                            <option value="${dept.id}">${dept.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>职称</label>
                                    <select class="form-control" id="doctorTitle" name="titleId" required>
                                        <c:forEach items="${titles}" var="title">
                                            <option value="${title.id}">${title.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>状态</label>
                                    <select class="form-control" id="doctorStatus" name="status">
                                        <option value="1">在职</option>
                                        <option value="0">离职</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>专长</label>
                                    <textarea class="form-control" id="doctorSpecialty" name="specialty"
                                        rows="3"></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" onclick="saveDoctor()">保存</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                function editDoctor(id) {
                    $.get('${pageContext.request.contextPath}/api/doctors/' + id, function (doctor) {
                        $('#doctorId').val(doctor.id);
                        $('#doctorName').val(doctor.name);
                        $('#departmentId').val(doctor.departmentId);
                        $('#doctorTitle').val(doctor.titleId);
                        $('#doctorSpecialty').val(doctor.specialty);
                        $('#doctorStatus').val(doctor.status);
                        $('#modalTitle').text('编辑医生信息');
                        $('#doctorModal').modal('show');
                    });
                }

                function deleteDoctor(id) {
                    if (confirm('确定要删除这个医生吗？')) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/doctors/' + id,
                            type: 'DELETE',
                            success: function () {
                                window.location.reload();
                            },
                            error: function () {
                                alert('删除失败，请重试');
                            }
                        });
                    }
                }

                function saveDoctor() {
                    const formData = new FormData(document.getElementById('doctorForm'));
                    const data = {};
                    formData.forEach((value, key) => data[key] = value);

                    const id = data.id;
                    const method = id ? 'PUT' : 'POST';
                    const url = id ?
                        '${pageContext.request.contextPath}/api/doctors/' + id :
                        '${pageContext.request.contextPath}/api/doctors';

                    $.ajax({
                        url: url,
                        type: method,
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function () {
                            $('#doctorModal').modal('hide');
                            window.location.reload();
                        },
                        error: function () {
                            alert('保存失败，请重试');
                        }
                    });
                }

                // 重置表单
                $('#doctorModal').on('hidden.bs.modal', function () {
                    $('#doctorForm')[0].reset();
                    $('#doctorId').val('');
                    $('#modalTitle').text('新增医生');
                });
            </script>
        </body>

        </html>