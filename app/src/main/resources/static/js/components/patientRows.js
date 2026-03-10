// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");
  // Use absolute path from static resources root to ensure it works from any page
  tr.innerHTML = `
      <td class="patient-id">${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><img src="/assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></td>
    `;

  // Attach event listeners
  const patientIdCell = tr.querySelector(".patient-id");
  if (patientIdCell) {
    patientIdCell.addEventListener("click", () => {
      window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
    });
  }

  const prescriptionBtn = tr.querySelector(".prescription-btn");
  if (prescriptionBtn) {
    prescriptionBtn.addEventListener("click", () => {
      window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}`;
    });
  }

  return tr;
}

