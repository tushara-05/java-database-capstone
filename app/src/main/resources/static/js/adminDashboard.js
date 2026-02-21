// adminDashboard.js

import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// --------------------- Event Binding ---------------------

// Open Add Doctor Modal
document.getElementById('addDocBtn')?.addEventListener('click', () => {
  openModal('addDoctor');
});

// --------------------- Load Doctor Cards ---------------------

window.addEventListener('DOMContentLoaded', loadDoctorCards);

/**
 * Fetches all doctors and renders their cards in the dashboard
 */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctor cards:", error);
  }
}

/**
 * Helper function to render a list of doctors as cards
 * @param {Array} doctors - Array of doctor objects
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
    return;
  }

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// --------------------- Filter/Search Doctors ---------------------

// Attach listeners for search bar and filters
document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);

/**
 * Filter doctors based on current input and dropdown selections
 */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim() || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    const doctors = await filterDoctors(name, time, specialty);

    if (doctors && doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Failed to filter doctors.");
  }
}

// --------------------- Add Doctor Functionality ---------------------

/**
 * Collect form data and save a new doctor
 */
export async function adminAddDoctor() {
  const name = document.getElementById("docName").value.trim();
  const email = document.getElementById("docEmail").value.trim();
  const phone = document.getElementById("docPhone").value.trim();
  const password = document.getElementById("docPassword").value.trim();
  const specialty = document.getElementById("docSpecialty").value.trim();

  // Collect available times from checkboxes
  const timeCheckboxes = document.querySelectorAll(".docTimeCheckbox");
  const availability = Array.from(timeCheckboxes)
    .filter(cb => cb.checked)
    .map(cb => cb.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Admin not authenticated. Please login.");
    return;
  }

  const doctor = { name, email, phone, password, specialty, availability };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      // Close modal and reload doctor list
      document.querySelector(".modal-close")?.click();
      loadDoctorCards();
    } else {
      alert("Failed to add doctor: " + result.message);
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("Something went wrong while adding the doctor.");
  }
}
