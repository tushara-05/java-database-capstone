import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";
let patientTableBody;
let selectedDate = new Date().toISOString().split('T')[0];
let token = localStorage.getItem('token');
let patientName = null;
document.addEventListener('DOMContentLoaded', () => {
  patientTableBody = document.getElementById("patientTableBody");
  renderContent();
  
  const datePicker = document.getElementById("datePicker");
  if (datePicker) {
    datePicker.value = selectedDate;
  }
  
  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", (e) => {
      const searchValue = e.target.value.trim();
      if (searchValue) {
        patientName = searchValue;
      } else {
        patientName = "null";
      }
      loadAppointments();
    });
  }
  const todayButton = document.getElementById("todayButton");
  if (todayButton) {
    todayButton.addEventListener("click", () => {
      selectedDate = new Date().toISOString().split('T')[0];
      if (datePicker) {
        datePicker.value = selectedDate;
      }
      loadAppointments();
    });
  }
  if (datePicker) {
    datePicker.addEventListener("change", (e) => {
      selectedDate = e.target.value;
      loadAppointments();
    });
  }
  loadAppointments();
});
async function loadAppointments() {
  if (!patientTableBody) return;
  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    patientTableBody.innerHTML = "";
    const appointments = data.appointments || data;
    if (!appointments || appointments.length === 0) {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td colspan="5" style="text-align: center;">No Appointments found for today</td>`;
      patientTableBody.appendChild(tr);
      return;
    }
    appointments.forEach(appointment => {
      const patient = {
        id: appointment.patient?.id || appointment.patientId || "N/A",
        name: appointment.patient?.name || appointment.patientName || "Unknown",
        phone: appointment.patient?.phone || appointment.patientPhone || "N/A",
        email: appointment.patient?.email || appointment.patientEmail || "N/A"
      };
      const appointmentId = appointment.id;
      const doctorId = appointment.doctor?.id || appointment.doctorId;
      const row = createPatientRow(patient, appointmentId, doctorId, appointment.status);
      patientTableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    if (patientTableBody) {
      patientTableBody.innerHTML = "";
      const tr = document.createElement("tr");
      tr.innerHTML = `<td colspan="5" style="text-align: center;">Error loading appointments. Try again later.</td>`;
      patientTableBody.appendChild(tr);
    }
  }
}
function renderContent() {
  const role = localStorage.getItem('userRole');
  if (!role) {
    window.location.href = "/";
    return;
  }
}
