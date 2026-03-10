// patientAppointment.js
import { getPatientAppointments, getPatientData, filterAppointments } from "./services/patientServices.js";

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");

let allAppointments = [];
let filteredAppointments = [];
let patientId = null;

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  try {
    if (!token) throw new Error("No token found");

    const patient = await getPatientData(token);
    if (!patient) throw new Error("Failed to fetch patient details");

    patientId = Number(patient.id);

    // getPatientAppointments returns the appointments array directly
    const appointments = await getPatientAppointments(patientId, token, "patient") || [];
    allAppointments = Array.isArray(appointments) ? appointments : [];

    renderAppointments(allAppointments);
  } catch (error) {
    console.error("Error loading appointments:", error);
    alert("❌ Failed to load your appointments.");
  }
}

function renderAppointments(appointments) {
  if (!tableBody) return;

  tableBody.innerHTML = "";

  const actionTh = document.querySelector("#patientTable thead tr th:last-child");
  if (actionTh) {
    actionTh.style.display = "table-cell"; // Always show "Actions" column
  }

  if (!appointments.length) {
    tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;">No Appointments Found</td></tr>`;
    return;
  }

  appointments.forEach(appointment => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${appointment.patientName || "You"}</td>
      <td>${appointment.doctorName}</td>
      <td>${appointment.appointmentDate}</td>
      <td>${appointment.appointmentTimeOnly}</td>
      <td>${appointment.status == 0 ? `<img src="../assets/images/edit/edit.png" alt="Edit" class="action-btn edit-btn" data-id="${appointment.id}">` :
        (appointment.status == 1 || appointment.status == 2) ? `<img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="View Prescription" class="action-btn view-btn" data-id="${appointment.id}">` :
          "Cancelled"
      }</td>
    `;

    if (appointment.status == 0) {
      const editBtn = tr.querySelector(".edit-btn");
      if (editBtn) {
        editBtn.addEventListener("click", () => redirectToUpdatePage(appointment));
      }
    } else if (appointment.status == 1 || appointment.status == 2) {
      const viewBtn = tr.querySelector(".view-btn");
      if (viewBtn) {
        viewBtn.addEventListener("click", () => {
          window.location.href = `/pages/addPrescription.html?appointmentId=${appointment.id}&patientName=${appointment.patientName || "You"}&mode=view`;
        });
      }
    }

    tableBody.appendChild(tr);
  });
}

function redirectToUpdatePage(appointment) {
  // Prepare the query parameters
  const queryString = new URLSearchParams({
    appointmentId: appointment.id,
    patientId: appointment.patientId,
    patientName: appointment.patientName || "You",
    doctorName: appointment.doctorName,
    doctorId: appointment.doctorId,
    appointmentDate: appointment.appointmentDate,
    appointmentTime: appointment.appointmentTimeOnly,
  }).toString();

  // Redirect to the update page with the query string
  setTimeout(() => {
    window.location.href = `/pages/updateAppointment.html?${queryString}`;
  }, 100);
}


// Search and Filter Listeners
document.getElementById("searchBar")?.addEventListener("input", handleFilterChange);
document.getElementById("appointmentFilter")?.addEventListener("change", handleFilterChange);

async function handleFilterChange() {
  const searchBar = document.getElementById("searchBar");
  const filterSelect = document.getElementById("appointmentFilter");

  if (!searchBar || !filterSelect) return;

  const searchBarValue = searchBar.value.trim();
  const filterValue = filterSelect.value;

  // Handle null/undefined - convert to empty string for URL
  const name = (searchBarValue === null || searchBarValue === undefined || searchBarValue === '') ? '' : searchBarValue;
  const condition = (filterValue === "allAppointments" || filterValue === null || filterValue === undefined || filterValue === '') ? '' : filterValue;

  try {
    const response = await filterAppointments(condition, name, token);
    const appointments = response?.appointments || [];
    filteredAppointments = appointments.filter(app => app.patientId === patientId);

    renderAppointments(filteredAppointments);
  } catch (error) {
    console.error("Failed to filter appointments:", error);
    alert("❌ An error occurred while filtering appointments.");
  }
}

