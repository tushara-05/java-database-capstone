// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// --------------------- Global Variables ---------------------

const patientTableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // Today's date in YYYY-MM-DD
const token = localStorage.getItem("token"); // Doctor auth token
let patientName = null; // Used for search filtering

// --------------------- Search Bar Functionality ---------------------

document.getElementById("searchBar")?.addEventListener("input", (event) => {
  const input = event.target.value.trim();
  patientName = input !== "" ? input : "null";
  loadAppointments();
});

// --------------------- Filter Controls ---------------------

// "Today's Appointments" button
document.getElementById("todayButton")?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

// Date picker control
document.getElementById("datePicker")?.addEventListener("change", (event) => {
  selectedDate = event.target.value;
  loadAppointments();
});

// --------------------- Load Appointments ---------------------

/**
 * Fetches appointments for the selected date and optional patient filter
 * and renders them into the table body
 */
async function loadAppointments() {
  try {
    // Fetch appointments from backend
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear existing table rows
    patientTableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      const noDataRow = document.createElement("tr");
      noDataRow.innerHTML = `<td colspan="5" class="text-center">No Appointments found for selected date.</td>`;
      patientTableBody.appendChild(noDataRow);
      return;
    }

    // Render each appointment as a table row
    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail,
        time: appointment.time,
        status: appointment.status,
      };

      const row = createPatientRow(patient);
      patientTableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    patientTableBody.innerHTML = `<tr>
      <td colspan="5" class="text-center">Error loading appointments. Try again later.</td>
    </tr>`;
  }
}

// --------------------- Initial Render on Page Load ---------------------

window.addEventListener("DOMContentLoaded", () => {
  // Optional: renderContent() if your UI layout setup is required
  if (typeof renderContent === "function") renderContent();
  
  // Load today's appointments by default
  loadAppointments();
});
