function showAppointmentHistory(patientId) {
    // 移除字符串模板字面量的包裹，因为已经在HTML中处理
    fetch(`/api/patients/${patientId}/appointments`)
        .then(response => response.json())
        .then(appointments => {
            const tbody = document.getElementById('appointmentHistoryTable');
            tbody.innerHTML = ''; // 清空现有内容

            appointments.forEach(appointment => {
                const row = `
                    <tr>
                        <td>${appointment.date}</td>
                        <td>${appointment.doctorName}</td>
                        <td>${appointment.department}</td>
                        <td>${appointment.status}</td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        })
        .catch(error => {
            console.error('获取预约记录失败:', error);
            alert('获取预约记录失败，请稍后重试');
        });
} 