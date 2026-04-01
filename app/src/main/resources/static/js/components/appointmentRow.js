// appointmentRow.js
export function getAppointments(appointment) {
  const tr = document.createElement("tr");
  const isCompleted = appointment.status == 1;
  const iconTitle = isCompleted ? "View Prescription" : "Add Prescription";
  tr.innerHTML = `
      <td class="patient-id">${appointment.patientName}</td>
      <td>${appointment.doctorName}</td>
      <td>${appointment.date}</td>
      <td>${appointment.time}</td>
      <td><img src="../assets/images/edit/edit.png" alt="action" title="${iconTitle}" class="prescription-btn" data-id="${appointment.id}"></td>
    `;
  // Attach event listeners
  const prescriptionBtn = tr.querySelector(".prescription-btn");
  if (prescriptionBtn) {
    prescriptionBtn.addEventListener("click", () => {
      const modeParam = isCompleted ? "&mode=view" : "";
      window.location.href = `/pages/addPrescription.html?appointmentId=${appointment.id}&patientName=${appointment.patientName}${modeParam}`;
    });
  }
  return tr;
}
