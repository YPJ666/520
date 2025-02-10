<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>科室管理</title>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
        </head>

        <body>
            <div class="container mt-4">
                <div class="row mb-4">
                    <div class="col">
                        <h2>科室管理</h2>
                    </div>
                </div>

                <div class="row mb-4">
                    <div class="col">
                        <button type="button" class="btn btn-primary" data-toggle="modal"
                            data-target="#departmentModal">新增科室</button>
                    </div>
                </div>

                <div class="row">
                    <c:forEach items="${departments}" var="dept">
                        <div class="col-md-4 mb-4">
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">${dept.name}</h5>
                                </div>
                                <div class="card-body">
                                    <p class="card-text">
                                        <strong>位置：</strong>${dept.location}<br>
                                        <strong>描述：</strong>${dept.description}
                                    </p>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-info"
                                            onclick="editDepartment('${dept.id}')">编辑</button>
                                        <button type="button" class="btn btn-sm btn-success"
                                            onclick="window.location.href='${pageContext.request.contextPath}/doctor/list?department=${dept.id}'">
                                            医生列表
                                        </button>
                                        <button type="button" class="btn btn-sm btn-danger"
                                            onclick="deleteDepartment('${dept.id}')">删除</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- 科室信息编辑模态框 -->
            <div class="modal fade" id="departmentModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalTitle">科室信息</h5>
                            <button type="button" class="close" data-dismiss="modal">
                                <span>&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <form id="departmentForm">
                                <input type="hidden" id="departmentId" name="id">

                                <div class="form-group">
                                    <label>科室名称</label>
                                    <input type="text" class="form-control" id="departmentName" name="name" required>
                                </div>

                                <div class="form-group">
                                    <label>位置</label>
                                    <input type="text" class="form-control" id="departmentLocation" name="location"
                                        required>
                                </div>

                                <div class="form-group">
                                    <label>描述</label>
                                    <textarea class="form-control" id="departmentDescription" name="description"
                                        rows="3"></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" onclick="saveDepartment()">保存</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                function editDepartment(id) {
                    $.get('${pageContext.request.contextPath}/api/departments/' + id, function (dept) {
                        $('#departmentId').val(dept.id);
                        $('#departmentName').val(dept.name);
                        $('#departmentLocation').val(dept.location);
                        $('#departmentDescription').val(dept.description);
                        $('#modalTitle').text('编辑科室信息');
                        $('#departmentModal').modal('show');
                    });
                }

                function deleteDepartment(id) {
                    if (confirm('确定要删除这个科室吗？删除科室将同时删除该科室下的所有医生信息。')) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/departments/' + id,
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

                function saveDepartment() {
                    const formData = new FormData(document.getElementById('departmentForm'));
                    const data = {};
                    formData.forEach((value, key) => data[key] = value);

                    const id = data.id;
                    const method = id ? 'PUT' : 'POST';
                    const url = id ?
                        '${pageContext.request.contextPath}/api/departments/' + id :
                        '${pageContext.request.contextPath}/api/departments';

                    $.ajax({
                        url: url,
                        type: method,
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function () {
                            $('#departmentModal').modal('hide');
                            window.location.reload();
                        },
                        error: function () {
                            alert('保存失败，请重试');
                        }
                    });
                }

                // 重置表单
                $('#departmentModal').on('hidden.bs.modal', function () {
                    $('#departmentForm')[0].reset();
                    $('#departmentId').val('');
                    $('#modalTitle').text('新增科室');
                });
            </script>
        </body>

        </html>