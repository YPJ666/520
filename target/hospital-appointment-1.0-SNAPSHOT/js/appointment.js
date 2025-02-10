// 获取可用时间段
function getAvailableTimeSlots() {
    const doctorId = $('#doctorId').val();
    const appointmentDate = $('#appointmentDate').val();

    if (doctorId && appointmentDate) {
        $.get(`${contextPath}/api/doctors/${doctorId}/schedules?date=${appointmentDate}`)
            .done(function (schedules) {
                const $timeSlot = $('#timeSlot');
                $timeSlot.empty().append('<option value="">请选择时间段</option>');

                schedules.forEach(function (schedule) {
                    if (schedule.status === 1) { // 1表示可约
                        $timeSlot.append(`<option value="${schedule.id}">${schedule.timeSlot}</option>`);
                    }
                });
            })
            .fail(function () {
                alert('获取可用时间段失败');
            });
    }
}

// 保存预约
function saveAppointment() {
    const appointment = {
        id: $('#appointmentId').val(),
        patientId: $('#patientId').val(),
        doctorId: $('#doctorId').val(),
        scheduleId: $('#timeSlot').val(),
        appointmentDate: $('#appointmentDate').val(),
        statusId: 1 // 1表示待就诊
    };

    const url = appointment.id ?
        `${contextPath}/api/appointments/${appointment.id}` :
        `${contextPath}/api/appointments`;

    $.ajax({
        url: url,
        type: appointment.id ? 'PUT' : 'POST',
        contentType: 'application/json',
        data: JSON.stringify(appointment)
    })
        .done(function () {
            $('#appointmentModal').modal('hide');
            location.reload();
        })
        .fail(function () {
            alert('保存预约失败');
        });
}

// 编辑预约
function editAppointment(id) {
    $.get(`${contextPath}/api/appointments/${id}`)
        .done(function (appointment) {
            $('#appointmentId').val(appointment.id);
            $('#patientId').val(appointment.patientId);
            $('#doctorId').val(appointment.doctorId);
            $('#appointmentDate').val(appointment.appointmentDate);
            getAvailableTimeSlots();
            $('#timeSlot').val(appointment.scheduleId);
            $('#appointmentModal').modal('show');
        })
        .fail(function () {
            alert('获取预约信息失败');
        });
}

// 完成预约
function completeAppointment(id) {
    if (confirm('确认完成此预约？')) {
        $.ajax({
            url: `${contextPath}/api/appointments/${id}/complete`,
            type: 'PUT'
        })
            .done(function () {
                location.reload();
            })
            .fail(function () {
                alert('完成预约失败');
            });
    }
}

// 取消预约
function cancelAppointment(id) {
    if (confirm('确认取消此预约？')) {
        $.ajax({
            url: `${contextPath}/api/appointments/${id}/cancel`,
            type: 'PUT'
        })
            .done(function () {
                location.reload();
            })
            .fail(function () {
                alert('取消预约失败');
            });
    }
}

// 删除预约
function deleteAppointment(id) {
    if (confirm('确认删除此预约？')) {
        $.ajax({
            url: `${contextPath}/api/appointments/${id}`,
            type: 'DELETE'
        })
            .done(function () {
                location.reload();
            })
            .fail(function () {
                alert('删除预约失败');
            });
    }
}

// 事件绑定
$(function () {
    $('#doctorId, #appointmentDate').change(getAvailableTimeSlots);

    $('#appointmentModal').on('hidden.bs.modal', function () {
        $('#appointmentForm')[0].reset();
        $('#appointmentId').val('');
    });
}); 