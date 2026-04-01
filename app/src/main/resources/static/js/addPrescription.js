import { savePrescription, getPrescription } from "./services/prescriptionServices.js";
document.addEventListener('DOMContentLoaded', async () => {
  const savePrescriptionBtn = document.getElementById("savePrescription");
  const patientNameInput = document.getElementById("patientName");
  const medicinesInput = document.getElementById("medicines");
  const dosageInput = document.getElementById("dosage");
  const notesInput = document.getElementById("notes");
  const heading = document.getElementById("heading");
  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  let mode = urlParams.get("mode");
  const token = localStorage.getItem("token");
  const patientName = urlParams.get("patientName");
  const role = localStorage.getItem("userRole");
  
  // Patients should ALWAYS be in view mode
  if (role === "loggedPatient") {
    mode = "view";
  }

  // Determine initial mode for UI state before async fetch
  const initialMode = mode || (role === "doctor" ? "add" : "view");

  // Set initial UI state synchronously to avoid flash
  if (heading) {
    if (initialMode === "view") {
      heading.innerHTML = `View <span>Prescription</span>`;
      if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
    } else {
      heading.innerHTML = `Add <span>Prescription</span>`;
      if (savePrescriptionBtn) savePrescriptionBtn.style.display = "block";
    }
  }

  // Pre-fill patient name initially
  if (patientNameInput && patientName) {
    patientNameInput.value = patientName;
  }

  // Fetch and pre-fill existing prescription if it exists
  if (appointmentId && token) {
    try {
      const response = await getPrescription(appointmentId, token);
      console.log("getPrescription :: ", response);
      // Check if the prescription exists in the response and access it from the object
      if (response && response.prescription) {
        mode = "view"; // Force to view mode because a prescription already exists!
        const existingPrescription = response.prescription; // Access prescription object
        patientNameInput.value = existingPrescription.patientName || patientName || "";
        medicinesInput.value = existingPrescription.medication || "";
        dosageInput.value = existingPrescription.dosage || "";
        notesInput.value = existingPrescription.doctorNotes || "";
        
        // Update UI to view mode state (if it wasn't already in view mode)
        if (heading) heading.innerHTML = `View <span>Prescription</span>`;
        if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
        
        patientNameInput.disabled = true;
        medicinesInput.disabled = true;
        dosageInput.disabled = true;
        notesInput.disabled = true;
      } else if (mode === "view") {
        // We expected a prescription but didn't find one in the response data
        console.warn("Mode was 'view' but no prescription record found.");
        if (heading) heading.innerHTML = `Add <span>Prescription</span>`;
        mode = "add"; // Revert to add mode if viewing fails to find data
      }
    } catch (error) {
      console.warn("No existing prescription found or failed to load:", error);
      // If patient was trying to view but it's not found, show error
      if (mode === "view" && role === "loggedPatient") {
        alert("The prescription record for this appointment could not be found.");
        window.location.href = "/pages/patientAppointments.html";
        return;
      }
      
      // If doctor is visiting without mode, and no record found, it's 'Add' mode
      if (!mode || mode !== "view") {
        mode = "add";
        if (heading) heading.innerHTML = `Add <span>Prescription</span>`;
      }
    }
  }

  // Final check for 'view' mode to ensure consistent state
  if (mode === 'view' || initialMode === 'view') {
    patientNameInput.disabled = true;
    medicinesInput.disabled = true;
    dosageInput.disabled = true;
    notesInput.disabled = true;
    if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
  } else {
    // Ensure inputs are enabled if we are in add mode
    medicinesInput.disabled = false;
    dosageInput.disabled = false;
    notesInput.disabled = false;
    if (savePrescriptionBtn) savePrescriptionBtn.style.display = "block";
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
        patientName: patientNameInput.value,
        medication: medicinesInput.value,
        dosage: dosageInput.value,
        doctorNotes: notesInput.value,
        appointmentId: Number(appointmentId) // Ensure it is sent as a number
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
