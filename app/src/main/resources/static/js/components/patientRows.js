// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId, status) {
  const tr = document.createElement("tr");
  const isCompleted = status == 1;
  const iconSrc = isCompleted ? "/assets/images/addPrescriptionIcon/addPrescription.png" : "/assets/images/addPrescriptionIcon/addPrescription.png"; // Placeholder for icon names
  const iconTitle = isCompleted ? "View Prescription" : "Add Prescription";
  
  // Use absolute path from static resources root to ensure it works from any page
  tr.innerHTML = `
      <td class="patient-id">${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><img src="${iconSrc}" alt="${iconTitle}" title="${iconTitle}" class="prescription-btn" data-id="${patient.id}"></td>
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
      const modeParam = isCompleted ? "&mode=view" : "";
      window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}${modeParam}`;
    });
  }
  return tr;
}
