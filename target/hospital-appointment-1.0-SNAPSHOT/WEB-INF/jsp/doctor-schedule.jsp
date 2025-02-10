<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>医生排班管理</title>
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

                .doctor-info {
                    background-color: #f8f9fa;
                    padding: 15px;
                    border-radius: 5px;
                    margin-bottom: 20px;
                }

                .doctor-info h3 {
                    margin: 0;
                    color: #333;
                }

                .doctor-info p {
                    margin: 5px 0 0;
                    color: #666;
                }

                .schedule-grid {
                    margin-top: 20px;
                }

                .time-slot {
                    padding: 10px;
                    margin: 5px;
                    border-radius: 5px;
                    cursor: pointer;
                }

                .time-slot.available {
                    background-color: #d4edda;
                    border: 1px solid #c3e6cb;
                }

                .time-slot.unavailable {
                    background-color: #f8d7da;
                    border: 1px solid #f5c6cb;
                }

                .time-slot.booked {
                    background-color: #e2e3e5;
                    border: 1px solid #d6d8db;
                    cursor: not-allowed;
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
                <div class="doctor-info mb-4">
                    <h3>${doctor.name}</h3>
                    <p>科室：${doctor.departmentName} | 职称：${doctor.titleName}</p>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <button class="btn btn-outline-primary" onclick="prevWeek()">&lt; 上周</button>
                        <span class="mx-3" id="currentWeek"></span>
                        <button class="btn btn-outline-primary" onclick="nextWeek()">下周 &gt;</button>
                    </div>
                    <button class="btn btn-primary" onclick="showBatchScheduleModal()">批量设置</button>
                </div>

                <div class="schedule-grid">
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>时间段</th>
                                    <th>周一</th>
                                    <th>周二</th>
                                    <th>周三</th>
                                    <th>周四</th>
                                    <th>周五</th>
                                </tr>
                            </thead>
                            <tbody id="scheduleTable">
                                <!-- 排班表格将通过JavaScript动态生成 -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- 批量排班模态框 -->
            <div class="modal fade" id="batchScheduleModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">批量设置排班</h5>
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                        </div>
                        <div class="modal-body">
                            <form id="batchScheduleForm">
                                <div class="form-group">
                                    <label>起始日期</label>
                                    <input type="date" class="form-control" id="startDate" required>
                                </div>
                                <div class="form-group">
                                    <label>结束日期</label>
                                    <input type="date" class="form-control" id="endDate" required>
                                </div>
                                <div class="form-group">
                                    <label>工作日</label>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="monday" value="1">
                                        <label class="form-check-label">周一</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="tuesday" value="2">
                                        <label class="form-check-label">周二</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="wednesday" value="3">
                                        <label class="form-check-label">周三</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="thursday" value="4">
                                        <label class="form-check-label">周四</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="friday" value="5">
                                        <label class="form-check-label">周五</label>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>时间段</label>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="timeSlots"
                                            value="08:00-09:00">
                                        <label class="form-check-label">08:00-09:00</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="timeSlots"
                                            value="09:00-10:00">
                                        <label class="form-check-label">09:00-10:00</label>
                                    </div>
                                    <!-- 更多时间段... -->
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" onclick="saveBatchSchedule()">保存</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                let currentWeek = 0;

                function loadSchedule() {
                    $.get('${pageContext.request.contextPath}/api/doctors/${doctor.id}/schedule', {
                        week: currentWeek
                    }, function (data) {
                        updateScheduleGrid(data);
                        updateWeekDisplay();
                    });
                }

                function updateScheduleGrid(scheduleData) {
                    const tbody = $('#scheduleTable');
                    tbody.empty();

                    scheduleData.forEach(schedule => {
                        const row = $('<tr>');
                        row.append($('<td>').text(schedule.timeSlot));
                        row.append($('<td>').text(schedule.monday ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.tuesday ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.wednesday ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.thursday ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.friday ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.available ? '可预约' : '不可预约'));
                        row.append($('<td>').text(schedule.booked ? '已预约' : '未预约'));
                        tbody.append(row);
                    });
                }

                function updateWeekDisplay() {
                    const today = new Date();
                    today.setDate(today.getDate() + currentWeek * 7);
                    const start = getWeekStart(today);
                    const end = getWeekEnd(today);
                    $('#currentWeek').text(formatDate(start) + ' - ' + formatDate(end));
                }

                function prevWeek() {
                    currentWeek--;
                    loadSchedule();
                }

                function nextWeek() {
                    currentWeek++;
                    loadSchedule();
                }

                function saveBatchSchedule() {
                    const data = {
                        startDate: $('#startDate').val(),
                        endDate: $('#endDate').val(),
                        workDays: []
                    };

                    $('input[type="checkbox"]:checked').each(function () {
                        data.workDays.push($(this).val());
                    });

                    $.ajax({
                        url: '${pageContext.request.contextPath}/api/doctors/${doctor.id}/schedule/batch',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function () {
                            $('#batchScheduleModal').modal('hide');
                            loadSchedule();
                        },
                        error: function () {
                            alert('保存失败，请重试');
                        }
                    });
                }

                // 页面加载时初始化
                $(document).ready(function () {
                    loadSchedule();
                });

                // 修改formatDate函数实现
                function formatDate(date) {
                    const year = date.getFullYear();
                    const month = String(date.getMonth() + 1).padStart(2, '0');
                    const day = String(date.getDate()).padStart(2, '0');
                    return `${year}-${month}-${day}`;
                }

                function getWeekStart(date) {
                    const d = new Date(date);
                    const day = d.getDay() || 7; // 如果是周日(0)，改为7
                    if (day !== 1) {
                        d.setHours(-24 * (day - 1)); // 设置为本周一
                    }
                    return d;
                }

                function getWeekEnd(date) {
                    const d = new Date(date);
                    const day = d.getDay() || 7;
                    if (day !== 5) { // 设置为本周五
                        d.setHours(24 * (5 - day));
                    }
                    return d;
                }
            </script>
        </body>

        </html>