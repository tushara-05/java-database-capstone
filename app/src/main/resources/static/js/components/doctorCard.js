// doctorCard.js

import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
// NOTE: showBookingOverlay is loaded lazily via dynamic import when needed

export function createDoctorCard(doctor) {
  // Create the main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get user's role from localStorage
  const role = localStorage.getItem("userRole");

  // Create doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  // Doctor name
  const name = document.createElement("h3");
  name.textContent = doctor.name || "Unknown Doctor";

  // Doctor speciality - backend uses "speciality", handle both for compatibility
  const specialization = document.createElement("p");
  specialization.textContent = doctor.speciality || doctor.specialty || doctor.specialization || "General Medicine";

  // Doctor email
  const email = document.createElement("p");
  email.textContent = doctor.email || "N/A";

  // Doctor availability
  const availability = document.createElement("p");
  const availTimes = doctor.availability || doctor.availableTimes || [];
  availability.textContent = availTimes.length > 0 ? availTimes.join(", ") : "Not Available";

  // Append info elements
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Create button container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Add role-specific buttons
  if (role === "admin") {
    // Admin: Delete button
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async () => {
      // Confirm deletion
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      // Get token from localStorage
      const token = localStorage.getItem('token');
      if (!token) {
        alert("Authentication required. Please log in again.");
        return;
      }

      // Call API to delete
      const result = await deleteDoctor(doctor.id, token);

      if (result.success) {
        alert(result.message || "Doctor deleted successfully!");
        // Remove the card from DOM
        card.remove();
      } else {
        alert(result.message || "Failed to delete doctor.");
      }
    });
    actionsDiv.appendChild(removeBtn);

  } else if (role === "patient") {
    // Patient (not logged in): Book Now button with alert
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Patient needs to login first.");
    });
    actionsDiv.appendChild(bookNow);

  } else if (role === "loggedPatient") {
    // Logged-in Patient: Real booking functionality
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Please log in to book an appointment.");
        window.location.href = "/pages/patientDashboard.html";
        return;
      }

      // Fetch patient data
      const patientData = await getPatientData(token);
      if (!patientData) {
        alert("Failed to fetch patient data. Please log in again.");
        return;
      }

      // Lazily load loggedPatient.js only when a logged-in patient clicks Book Now
      const { showBookingOverlay } = await import("../loggedPatient.js");
      showBookingOverlay(e, doctor, patientData);
    });
    actionsDiv.appendChild(bookNow);
  }

  // Final assembly
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}

