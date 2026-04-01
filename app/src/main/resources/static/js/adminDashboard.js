// adminDashboard.js - Managing Doctors
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
// Initialize once DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  // Attach event listener to Add Doctor button
  document.getElementById('addDocBtn')?.addEventListener('click', () => {
    openModal('addDoctor');
  });
  renderContent();
  loadDoctorCards();
});
// Render content based on user role
function renderContent() {
  const role = localStorage.getItem('userRole');
  if (!role) {
    window.location.href = "/";
    return;
  }
}
/**
 * Load all doctors and display them as cards
 */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}
/**
 * Attach event listeners to search bar and filter dropdowns
 */
document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpeciality")?.addEventListener("change", filterDoctorsOnChange);
/**
 * Filter doctors based on name, time, and speciality
 */
async function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpeciality = document.getElementById("filterSpeciality");
  const name = searchBar?.value?.trim() || null;
  const time = filterTime?.value || null;
  const speciality = filterSpeciality?.value || null;
  try {
    const result = await filterDoctors(name, time, speciality);
    const doctors = result.doctors || [];
    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      renderDoctorCards([]);
      // Show "No doctors found" message
      const contentDiv = document.getElementById("content");
      if (contentDiv) {
        contentDiv.innerHTML = "<p class='no-results'>No doctors found with the given filters.</p>";
      }
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Error filtering doctors. Please try again.");
  }
}
/**
 * Render doctor cards to the content div
 * @param {Array} doctors - Array of doctor objects
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;
  contentDiv.innerHTML = "";
  if (doctors.length === 0) {
    contentDiv.innerHTML = "<p class='no-results'>No doctors found.</p>";
    return;
  }
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}
/**
 * Handle adding a new doctor from the modal form
 */
window.adminAddDoctor = async function () {
  const name = document.getElementById('doctorName')?.value;
  const speciality = document.getElementById('speciality')?.value;
  const email = document.getElementById('doctorEmail')?.value;
  const password = document.getElementById('doctorPassword')?.value;
  const phone = document.getElementById('doctorPhone')?.value;
  // Get selected availability times
  const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
  const availability = Array.from(availabilityCheckboxes).map(cb => cb.value);
  // Validate required fields
  if (!name || !speciality || !email || !password || !phone) {
    alert("Please fill in all required fields");
    return;
  }
  // Validate password length
  if (password.length < 6) {
    alert("Password must be at least 6 characters long.");
    return;
  }
  // Validate phone format (exactly 10 digits)
  const phoneRegex = /^\d{10}$/;
  if (!phoneRegex.test(phone)) {
    alert("Phone number must be exactly 10 digits.");
    return;
  }
  // Get token from localStorage
  const token = localStorage.getItem('token');
  if (!token) {
    alert("Authentication required. Please log in again.");
    return;
  }
  // Create doctor object
  const doctor = {
    name,
    speciality,
    email,
    password,
    phone,
    availableTimes: availability
  };
  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert(result.message || "Doctor added successfully!");
      // Close modal
      const modal = document.getElementById('modal');
      if (modal) {
        modal.style.display = 'none';
        document.body.classList.remove('modal-open');
      }
      // Reload doctor list
      loadDoctorCards();
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("Error adding doctor. Please try again.");
  }
};
