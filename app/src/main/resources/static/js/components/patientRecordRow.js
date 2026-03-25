// patientRecordRow.js
export function createPatientRecordRow(patient) {
  const tr = document.createElement("tr");
  tr.innerHTML = `
      <td class="patient-id">${patient.appointmentDate}</td>
      <td>${patient.id}</td>
      <td>${patient.patientId}</td>
      <td>${patient.status == 1 ? `<img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="View Prescription" title="View Prescription" class="prescription-btn" data-id="${patient.id}">` : "Scheduled"}</td>
    `;
  // Attach event listeners
  const prescriptionBtn = tr.querySelector(".prescription-btn");
  if (prescriptionBtn) {
    prescriptionBtn.addEventListener("click", () => {
      window.location.href = `/pages/addPrescription.html?mode=view&appointmentId=${patient.id}&patientName=${patient.patientName || "You"}`;
    });
  }
  return tr;
}
