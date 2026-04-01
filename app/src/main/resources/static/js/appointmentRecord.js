// appointmentRecord.js
import { getAppointments } from "./components/appointmentRow.js";
import { getAllAppointments } from "./services/appointmentRecordService.js";

const tableBody = document.getElementById("patientTableBody");
const filterSelect = document.getElementById("appointmentFilter");

async function loadAppointments(filter = "upcoming") {
  if (!tableBody) return;

  const token = localStorage.getItem('token');
  const today = new Date().toISOString().split('T')[0];

  try {
    const appointments = await getAllAppointments(today, '', token);

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No appointments found.</td></tr>`;
      return;
    }

    const todayStart = new Date().setHours(0, 0, 0, 0);
    let filteredAppointments = appointments;

    if (filter === "upcoming") {
      filteredAppointments = appointments.filter(app => new Date(app.appointmentDate) >= todayStart);
    } else if (filter === "past") {
      filteredAppointments = appointments.filter(app => new Date(app.appointmentDate) < todayStart);
    }

    if (filteredAppointments.length === 0) {
      tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No ${filter} appointments found.</td></tr>`;
      return;
    }

    tableBody.innerHTML = "";
    filteredAppointments.forEach(appointment => {
      const row = getAppointments(appointment);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>Error loading appointments.</td></tr>`;
  }
}

// Handle filter change
if (filterSelect) {
  filterSelect.addEventListener("change", (e) => {
    const selectedFilter = e.target.value;
    loadAppointments(selectedFilter);
  });
}

// Load upcoming appointments by default
loadAppointments("upcoming");

