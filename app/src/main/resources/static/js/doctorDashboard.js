/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment
*/
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

/*
  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)
*/
let patientTableBody;
let selectedDate = new Date().toISOString().split('T')[0];
const token = localStorage.getItem('token');
let patientName = "null";

/*
  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter
*/
document.getElementById("searchBar")?.addEventListener("input", (e) => {
  const searchValue = e.target.value.trim();
  if (searchValue) {
    patientName = searchValue;
  } else {
    patientName = "null";
  }
  loadAppointments();
});

/*
  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today
*/
document.getElementById("todayButton")?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split('T')[0];
  const datePicker = document.getElementById("datePicker");
  if (datePicker) {
    datePicker.value = selectedDate;
  }
  loadAppointments();
});

/*
  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date
*/
document.getElementById("datePicker")?.addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});

/*
  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."
*/
async function loadAppointments() {
  if (!patientTableBody) return;

  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    patientTableBody.innerHTML = "";

    const appointments = data.appointments || data;

    if (!appointments || appointments.length === 0) {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td colspan="5" style="text-align: center;">No Appointments found for today.</td>`;
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

      const row = createPatientRow(patient, appointmentId, doctorId);
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

/*
  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
document.addEventListener('DOMContentLoaded', () => {
  patientTableBody = document.getElementById("patientTableBody");
  renderContent();

  const datePicker = document.getElementById("datePicker");
  if (datePicker) {
    datePicker.value = selectedDate;
  }

  loadAppointments();
});

