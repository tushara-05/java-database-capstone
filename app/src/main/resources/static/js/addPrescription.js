import { savePrescription, getPrescription } from "./services/prescriptionServices.js";

// Make selectRole available globally (defined in util.js)
// window.selectRole = selectRole; - Now handled in util.js

// setRole is now defined in util.js - removed duplicate

document.addEventListener('DOMContentLoaded', async () => {
  const savePrescriptionBtn = document.getElementById("savePrescription");
  const patientNameInput = document.getElementById("patientName");
  const medicinesInput = document.getElementById("medicines");
  const dosageInput = document.getElementById("dosage");
  const notesInput = document.getElementById("notes");
  const heading = document.getElementById("heading");

  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const mode = urlParams.get("mode");
  const token = localStorage.getItem("token");
  const patientName = urlParams.get("patientName");

  if (heading) {
    if (mode === "view") {
      heading.innerHTML = `View <span>Prescription</span>`;
    } else {
      heading.innerHTML = `Add <span>Prescription</span>`;
    }
  }

  // Pre-fill patient name
  if (patientNameInput && patientName) {
    patientNameInput.value = patientName;
  }

  // Fetch and pre-fill existing prescription if it exists
  if (appointmentId && token) {
    try {
      const response = await getPrescription(appointmentId, token);
      console.log("getPrescription :: ", response);

      // Check if prescription exists - API returns single object, not array
      if (response.prescription) {
        const existingPrescription = response.prescription;
        if (patientNameInput) patientNameInput.value = existingPrescription.patientName || "You";
        if (medicinesInput) medicinesInput.value = existingPrescription.medication || "";
        if (dosageInput) dosageInput.value = existingPrescription.dosage || "";
        if (notesInput) notesInput.value = existingPrescription.doctorNotes || "";
      }

    } catch (error) {
      if (error.message && error.message.includes("not found")) {
        console.log("No existing prescription found for this appointment. Ready to create a new one.");
      } else {
        console.warn("Failed to load existing prescription:", error);
      }
    }
  }

  if (mode === 'view') {
    // Make fields read-only
    if (patientNameInput) patientNameInput.disabled = true;
    if (medicinesInput) medicinesInput.disabled = true;
    if (dosageInput) dosageInput.disabled = true;
    if (notesInput) notesInput.disabled = true;
    if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
  }

  // Cancel button logic
  const cancelBtn = document.getElementById("cancelBtn");
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      const role = localStorage.getItem("userRole");
      if (role === "loggedPatient") {
        window.location.href = "/pages/patientAppointments.html";
      } else {
        window.selectRole("doctor");
      }
    });
  }

  // Save prescription on button click
  if (savePrescriptionBtn) {
    savePrescriptionBtn.addEventListener('click', async (e) => {
      e.preventDefault();

      const prescription = {
        patientName: patientNameInput ? patientNameInput.value : "",
        medication: medicinesInput ? medicinesInput.value : "",
        dosage: dosageInput ? dosageInput.value : "",
        doctorNotes: notesInput ? notesInput.value : "",
        appointmentId
      };

      const { success, message } = await savePrescription(prescription, token);

      if (success) {
        alert("✅ Prescription saved successfully.");
        const role = localStorage.getItem("userRole");
        if (role === "loggedPatient") {
          window.location.href = "/pages/patientAppointments.html";
        } else {
          window.selectRole('doctor');
        }
      } else {
        alert("❌ Failed to save prescription. " + message);
      }
    });
  }
});

