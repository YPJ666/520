<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>患者信息管理</title>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css">
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
            </style>
        </head>

        <body>
            <div class="container">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>患者信息管理</h2>
                    <button class="btn btn-primary" data-toggle="modal" data-target="#patientModal">新增患者</button>
                </div>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>姓名</th>
                            <th>性别</th>
                            <th>联系电话</th>
                            <th>身份证号</th>
                            <th>地址</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${patients}" var="patient">
                            <tr>
                                <td>${patient.name}</td>
                                <td>${patient.gender}</td>
                                <td>${patient.phone}</td>
                                <td>${patient.idCard}</td>
                                <td>${patient.address}</td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-info"
                                            onclick="editPatient(${patient.id})">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-danger"
                                            onclick="deletePatient(${patient.id})">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-success"
                                            onclick="openAppointmentModal(${patient.id})">
                                            <i class="bi bi-calendar-plus"></i> 预约挂号
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- 患者信息模态框 -->
                <div class="modal fade" id="patientModal">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="modalTitle">新增患者</h5>
                                <button type="button" class="close" data-dismiss="modal">&times;</button>
                            </div>
                            <div class="modal-body">
                                <form id="patientForm">
                                    <input type="hidden" id="patientId" name="id">
                                    <div class="form-group">
                                        <label>姓名</label>
                                        <input type="text" class="form-control" id="patientName" name="name" required>
                                    </div>
                                    <div class="form-group">
                                        <label>性别</label>
                                        <select class="form-control" id="patientGender" name="gender" required>
                                            <option value="男">男</option>
                                            <option value="女">女</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>联系电话</label>
                                        <input type="tel" class="form-control" id="patientPhone" name="phone" required>
                                    </div>
                                    <div class="form-group">
                                        <label>身份证号</label>
                                        <input type="text" class="form-control" id="patientIdCard" name="idCard"
                                            required>
                                    </div>
                                    <div class="form-group">
                                        <label>地址</label>
                                        <textarea class="form-control" id="patientAddress" name="address"
                                            rows="3"></textarea>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" onclick="savePatient()">保存</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 修改预约模态框 -->
                <div class="modal fade" id="appointmentModal">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">预约挂号</h5>
                                <button type="button" class="close" data-dismiss="modal">&times;</button>
                            </div>
                            <div class="modal-body">
                                <form id="appointmentForm">
                                    <input type="hidden" name="patientId" id="appointmentPatientId">
                                    <div class="form-group">
                                        <label>科室</label>
                                        <select class="form-control" id="departmentSelect" required>
                                            <option value="">请选择科室</option>
                                            <c:forEach items="${departments}" var="dept">
                                                <option value="${dept.id}">${dept.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>医生</label>
                                        <select class="form-control" id="doctorSelect" disabled required>
                                            <option value="">请先选择科室</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>预约日期</label>
                                        <input type="date" class="form-control" id="appointmentDate" disabled required>
                                    </div>
                                    <div class="form-group">
                                        <label>时间段</label>
                                        <select class="form-control" name="scheduleId" id="timeSlotSelect" disabled
                                            required>
                                            <option value="">请先选择日期</option>
                                        </select>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" onclick="saveAppointment()">保存</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                function editPatient(id) {
                    $.get('${pageContext.request.contextPath}/api/patients/' + id, function (patient) {
                        $('#patientId').val(patient.id);
                        $('#patientName').val(patient.name);
                        $('#patientGender').val(patient.gender);
                        $('#patientPhone').val(patient.phone);
                        $('#patientIdCard').val(patient.idCard);
                        $('#patientAddress').val(patient.address);
                        $('#modalTitle').text('编辑患者');
                        $('#patientModal').modal('show');
                    });
                }

                function deletePatient(id) {
                    if (confirm('确定要删除这个患者吗？')) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/patients/' + id,
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

                function savePatient() {
                    const formData = new FormData(document.getElementById('patientForm'));
                    const data = {};
                    formData.forEach((value, key) => data[key] = value);

                    const id = data.id;
                    const method = id ? 'PUT' : 'POST';
                    const url = id ?
                        '${pageContext.request.contextPath}/api/patients/' + id :
                        '${pageContext.request.contextPath}/api/patients';

                    $.ajax({
                        url: url,
                        type: method,
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function () {
                            $('#patientModal').modal('hide');
                            window.location.reload();
                        },
                        error: function () {
                            alert('保存失败，请重试');
                        }
                    });
                }

                // 重置表单
                $('#patientModal').on('hidden.bs.modal', function () {
                    $('#patientForm')[0].reset();
                    $('#patientId').val('');
                    $('#modalTitle').text('新增患者');
                });

                // 打开预约模态框时的处理
                function openAppointmentModal(patientId) {
                    $('#appointmentPatientId').val(patientId);
                    $('#appointmentForm')[0].reset();
                    $('#doctorSelect').prop('disabled', true);
                    $('#appointmentDate').prop('disabled', true);
                    $('#timeSlotSelect').prop('disabled', true);
                    $('#appointmentModal').modal('show');
                }

                // 当选择科室时加载医生列表
                $('#departmentSelect').change(function () {
                    const deptId = $(this).val();
                    const doctorSelect = $('#doctorSelect');

                    if (deptId) {
                        console.log('正在获取科室可预约医生，科室ID:', deptId);

                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/departments/' + deptId + '/doctors',
                            type: 'GET',
                            success: function (doctors) {
                                console.log('获取到的医生列表:', doctors);

                                doctorSelect.empty();
                                doctorSelect.append($('<option>', {
                                    value: '',
                                    text: '请选择医生'
                                }));

                                if (Array.isArray(doctors) && doctors.length > 0) {
                                    doctors.forEach(doctor => {
                                        const doctorName = doctor.name || '未知医生';
                                        const titleName = doctor.titleName || '';
                                        const specialty = doctor.specialty || '';

                                        const optionText = doctorName +
                                            (titleName ? ' - ' + titleName : '') +
                                            (specialty ? ' (' + specialty + ')' : '');

                                        doctorSelect.append($('<option>', {
                                            value: doctor.id,
                                            text: optionText
                                        }));

                                        console.log('添加医生选项:', {
                                            id: doctor.id,
                                            name: doctorName,
                                            title: titleName,
                                            specialty: specialty,
                                            text: optionText
                                        });
                                    });
                                    doctorSelect.prop('disabled', false);
                                } else {
                                    doctorSelect.append($('<option>', {
                                        value: '',
                                        text: '该科室暂无可预约医生'
                                    }));
                                    doctorSelect.prop('disabled', true);
                                }
                            },
                            error: function (xhr, status, error) {
                                console.error('获取医生列表失败:', {
                                    status: status,
                                    error: error,
                                    response: xhr.responseText
                                });

                                doctorSelect.empty()
                                    .append($('<option>', {
                                        value: '',
                                        text: '加载失败，请重试'
                                    }))
                                    .prop('disabled', true);
                            }
                        });
                    } else {
                        doctorSelect.empty()
                            .append($('<option>', {
                                value: '',
                                text: '请先选择科室'
                            }))
                            .prop('disabled', true);
                    }
                });

                // 当选择医生时启用日期选择
                $('#doctorSelect').change(function () {
                    const doctorId = $(this).val();
                    const dateInput = $('#appointmentDate');

                    if (doctorId) {
                        dateInput.prop('disabled', false);
                        // 设置最小日期为今天
                        const today = new Date().toISOString().split('T')[0];
                        dateInput.attr('min', today);
                        // 设置最大日期为30天后
                        const maxDate = new Date();
                        maxDate.setDate(maxDate.getDate() + 30);
                        dateInput.attr('max', maxDate.toISOString().split('T')[0]);
                    } else {
                        dateInput.prop('disabled', true);
                        $('#timeSlotSelect').prop('disabled', true);
                    }
                });

                // 当选择日期时加载可用时间段
                $('#appointmentDate').change(function () {
                    const date = $(this).val();
                    const doctorId = $('#doctorSelect').val();
                    const timeSlotSelect = $('#timeSlotSelect');

                    if (date && doctorId) {
                        console.log('正在获取可用时间段:', {
                            doctorId: doctorId,
                            date: date
                        });

                        $.ajax({
                            url: '${pageContext.request.contextPath}/api/schedules/doctor',
                            type: 'GET',
                            data: {
                                doctorId: doctorId,
                                date: date
                            },
                            success: function (schedules) {
                                console.log('获取到的时间段:', schedules);

                                timeSlotSelect.empty();
                                timeSlotSelect.append($('<option>', {
                                    value: '',
                                    text: '请选择时间段'
                                }));

                                if (Array.isArray(schedules) && schedules.length > 0) {
                                    schedules.forEach(schedule => {
                                        if (schedule.status === 1) { // 只显示可预约的时间段
                                            timeSlotSelect.append($('<option>', {
                                                value: schedule.id,
                                                text: schedule.timeSlot
                                            }));
                                            console.log('添加时间段选项:', {
                                                id: schedule.id,
                                                timeSlot: schedule.timeSlot
                                            });
                                        }
                                    });
                                    timeSlotSelect.prop('disabled', false);
                                } else {
                                    timeSlotSelect.append($('<option>', {
                                        value: '',
                                        text: '当天无可用时间段'
                                    }));
                                    timeSlotSelect.prop('disabled', true);
                                }
                            },
                            error: function (xhr, status, error) {
                                console.error('获取时间段失败:', {
                                    status: status,
                                    error: error,
                                    response: xhr.responseText
                                });

                                timeSlotSelect.empty()
                                    .append($('<option>', {
                                        value: '',
                                        text: '加载失败，请重试'
                                    }))
                                    .prop('disabled', true);
                            }
                        });
                    } else {
                        timeSlotSelect.empty()
                            .append($('<option>', {
                                value: '',
                                text: '请先选择日期'
                            }))
                            .prop('disabled', true);
                    }
                });

                // 保存预约
                function saveAppointment() {
                    const formData = {
                        patientId: $('#appointmentPatientId').val(),
                        scheduleId: $('#timeSlotSelect').val()
                    };

                    if (!formData.scheduleId) {
                        alert('请选择预约时间段');
                        return;
                    }

                    console.log('正在保存预约:', formData);

                    $.ajax({
                        url: '${pageContext.request.contextPath}/api/appointments',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(formData),
                        success: function (response) {
                            console.log('预约成功:', response);
                            $('#appointmentModal').modal('hide');
                            alert('预约成功');
                            location.reload();
                        },
                        error: function (xhr, status, error) {
                            console.error('预约失败:', {
                                status: xhr.status,
                                statusText: xhr.statusText,
                                error: error,
                                response: xhr.responseText
                            });

                            let errorMessage = '预约失败';
                            try {
                                // 尝试解析错误响应
                                const response = JSON.parse(xhr.responseText);
                                errorMessage = response.message || response.error || xhr.responseText;
                            } catch (e) {
                                errorMessage = xhr.responseText || '请稍后重试';
                            }

                            alert('预约失败: ' + errorMessage);
                        }
                    });
                }
            </script>
        </body>

        </html>