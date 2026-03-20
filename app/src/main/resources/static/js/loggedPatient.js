// loggedPatient.js 
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { bookAppointment } from './services/appointmentRecordService.js';

// Flag to prevent concurrent booking requests
let isBookingInProgress = false;

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  attachFilterListeners();
});

function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      if (!contentDiv) return;
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}

export function showBookingOverlay(e, doctor, patient) {
  // Prevent multiple overlays
  const existingOverlay = document.querySelector(".modalApp");
  if (existingOverlay) {
    existingOverlay.remove();
  }

  const button = e.target;

  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);

  setTimeout(() => ripple.classList.add("active"), 50);

  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");

  document.body.style.overflow = "hidden"; // Prevent background scrolling

  const availableTimes = doctor.availableTimes || doctor.availability || [];

  modalApp.innerHTML = `
    <span class="close-modal-btn" style="position: absolute; right: 20px; top: 15px; font-size: 28px; font-weight: bold; cursor: pointer; color: #aaa;">&times;</span>
    <h2>Book Appointment</h2>
    <input class="input-field" type="text" id="patientName" name="patientName" value="${patient.name}" disabled />
    <input class="input-field" type="text" id="doctorName" name="doctorName" value="${doctor.name}" disabled />
    <input class="input-field" type="text" id="doctorSpeciality" name="doctorSpeciality" value="${doctor.speciality || doctor.specialty || doctor.specialization || ''}" disabled/>
    <input class="input-field" type="email" id="doctorEmail" name="doctorEmail" value="${doctor.email}" disabled/>
    <input class="input-field" type="date" id="appointment-date" name="appointmentDate" min="${new Date().toISOString().split('T')[0]}" />
    <select class="input-field" id="appointment-time" name="appointmentTime">
      <option value="">Select time</option>
      ${availableTimes.map(t => `<option value="${t}">${t}</option>`).join('')}
    </select>
    <button class="confirm-booking" id="confirmBookingBtn">Confirm Booking</button>
  `;

  document.body.appendChild(modalApp);

  setTimeout(() => modalApp.classList.add("active"), 600);

  // Close modal handler
  const closeBtn = modalApp.querySelector(".close-modal-btn");
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      document.body.style.overflow = ""; // Restore scrolling
      modalApp.classList.remove("active");
      setTimeout(() => {
        ripple.remove();
        modalApp.remove();
      }, 500); // Wait for transition to finish
    });
  }

  // Use single event listener with flag to prevent race conditions
  const confirmBtn = modalApp.querySelector("#confirmBookingBtn");
  confirmBtn?.addEventListener("click", async () => {
    // Prevent concurrent booking requests
    if (isBookingInProgress) {
      return;
    }

    const dateInput = modalApp.querySelector("#appointment-date");
    const timeSelect = modalApp.querySelector("#appointment-time");

    const date = dateInput?.value;
    const time = timeSelect?.value;
    const token = localStorage.getItem("token");

    if (!date || !time) {
      alert("Please select both date and time.");
      return;
    }

    const selectedDate = new Date(date);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      alert("Appointment date cannot be in the past.");
      return;
    }

    // Set flag to prevent concurrent requests
    isBookingInProgress = true;
    confirmBtn.disabled = true;
    confirmBtn.textContent = "Booking...";

    try {
      // Handle both "09:00" format (new) and "09:00-10:00" format (legacy)
      const startTime = time.includes('-') ? time.split('-')[0] : time;
      const appointment = {
        doctor: { id: doctor.id },
        patient: { id: patient.id },
        appointmentTime: `${date}T${startTime}:00`,
        status: 0
      };

      const { success, message } = await bookAppointment(appointment, token);

      if (success) {
        alert("Appointment Booked successfully");
        document.body.style.overflow = ""; // Restore scrolling
        ripple.remove();
        modalApp.remove();
      } else {
        alert("❌ Failed to book an appointment :: " + message);
      }
    } finally {
      // Reset flag after request completes
      isBookingInProgress = false;
      confirmBtn.disabled = false;
      confirmBtn.textContent = "Confirm Booking";
    }
  });
}

// Attach filter listeners with null check to prevent errors
function attachFilterListeners() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpeciality = document.getElementById("filterSpeciality");

  searchBar?.addEventListener("input", filterDoctorsOnChange);
  filterTime?.addEventListener("change", filterDoctorsOnChange);
  filterSpeciality?.addEventListener("change", filterDoctorsOnChange);
}

function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpeciality = document.getElementById("filterSpeciality");

  if (!searchBar || !filterTime || !filterSpeciality) return;

  const searchBarValue = searchBar.value.trim();
  const filterTimeValue = filterTime.value;
  const filterSpecialityValue = filterSpeciality.value;

  const name = searchBarValue.length > 0 ? searchBarValue : null;
  const time = filterTimeValue.length > 0 ? filterTimeValue : null;
  const speciality = filterSpecialityValue.length > 0 ? filterSpecialityValue : null;

  filterDoctors(name, time, speciality)
    .then(response => {
      const doctors = response.doctors;
      const contentDiv = document.getElementById("content");
      if (!contentDiv) return;
      contentDiv.innerHTML = "";

      if (doctors && doctors.length > 0) {
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

