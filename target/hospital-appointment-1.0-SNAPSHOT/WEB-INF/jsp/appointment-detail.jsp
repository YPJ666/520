<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>预约详情</title>
                <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
            </head>

            <body>
                <div class="container mt-4">
                    <div class="row mb-4">
                        <div class="col">
                            <h2>预约详情</h2>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header">
                            <h4>基本信息</h4>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p><strong>预约日期：</strong>
                                        <fmt:formatDate value="${appointment.appointmentDate}" pattern="yyyy-MM-dd" />
                                    </p>
                                    <p><strong>时间段：</strong>${appointment.timeSlot}</p>
                                    <p><strong>状态：</strong>
                                        <span class="badge badge-${appointment.status eq '待确认' ? 'warning' : 
                                              appointment.status eq '已确认' ? 'success' : 'danger'}">
                                            ${appointment.status}
                                        </span>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>创建时间：</strong>
                                        <fmt:formatDate value="${appointment.createTime}"
                                            pattern="yyyy-MM-dd HH:mm:ss" />
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h4>患者信息</h4>
                                </div>
                                <div class="card-body">
                                    <p><strong>姓名：</strong>${appointment.patient.name}</p>
                                    <p><strong>性别：</strong>${appointment.patient.gender}</p>
                                    <p><strong>联系电话：</strong>${appointment.patient.phone}</p>
                                    <p><strong>身份证号：</strong>${appointment.patient.idCard}</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h4>医生信息</h4>
                                </div>
                                <div class="card-body">
                                    <p><strong>姓名：</strong>${appointment.doctor.name}</p>
                                    <p><strong>科室：</strong>${appointment.doctor.department}</p>
                                    <p><strong>职称：</strong>${appointment.doctor.title}</p>
                                    <p><strong>专长：</strong>${appointment.doctor.specialty}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-4">
                        <div class="col">
                            <div class="btn-group">
                                <c:if test="${appointment.status eq '待确认'}">
                                    <button type="button" class="btn btn-success"
                                        onclick="updateStatus('已确认')">确认预约</button>
                                    <button type="button" class="btn btn-danger"
                                        onclick="updateStatus('已取消')">取消预约</button>
                                </c:if>
                                <a href="javascript:history.back()" class="btn btn-secondary">返回</a>
                            </div>
                        </div>
                    </div>
                </div>

                <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    function updateStatus(status) {
                        if (confirm('确定要将预约状态更新为' + status + '吗？')) {
                            const form = document.createElement('form');
                            form.method = 'POST';
                            form.action = '${pageContext.request.contextPath}/appointment/';

                            const actionInput = document.createElement('input');
                            actionInput.type = 'hidden';
                            actionInput.name = 'action';
                            actionInput.value = 'updateStatus';
                            form.appendChild(actionInput);

                            const idInput = document.createElement('input');
                            idInput.type = 'hidden';
                            idInput.name = 'appointmentId';
                            idInput.value = '${appointment.id}';
                            form.appendChild(idInput);

                            const statusInput = document.createElement('input');
                            statusInput.type = 'hidden';
                            statusInput.name = 'status';
                            statusInput.value = status;
                            form.appendChild(statusInput);

                            document.body.appendChild(form);
                            form.submit();
                        }
                    }
                </script>
            </body>

            </html>