// updateAppointment.js
import { updateAppointment } from "./services/appointmentRecordService.js";
import { getDoctors } from "./services/doctorServices.js";

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Missing session data, redirecting to appointments page.");
    window.location.href = "/pages/patientAppointments.html";
    return;
  }

  // Get appointmentId and patientId from the URL query parameters
  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const patientId = urlParams.get("patientId");
  const doctorId = urlParams.get("doctorId");
  const patientName = urlParams.get("patientName");
  const doctorName = urlParams.get("doctorName");
  const appointmentDate = urlParams.get("appointmentDate");
  const appointmentTime = urlParams.get("appointmentTime");

  if (!patientId) {
    alert("Missing session data, redirecting to appointments page.");
    window.location.href = "/pages/patientAppointments.html";
    return;
  }

  // get doctor to display only the available time of doctor
  getDoctors()
    .then(doctors => {
      // Find the doctor by the ID from the URL
      const doctor = doctors.find(d => d.id == doctorId);
      if (!doctor) {
        alert("Doctor not found.");
        return;
      }

      // Fill the form with the appointment data passed in the URL
      const patientNameEl = document.getElementById("patientName");
      const doctorNameEl = document.getElementById("doctorName");
      const appointmentDateEl = document.getElementById("appointmentDate");
      const appointmentTimeEl = document.getElementById("appointmentTime");

      if (patientNameEl) patientNameEl.value = patientName || "You";
      if (doctorNameEl) doctorNameEl.value = doctorName;
      if (appointmentDateEl) {
        appointmentDateEl.value = appointmentDate;
        appointmentDateEl.min = new Date().toISOString().split('T')[0];
      }

      // Get availability times from doctor (handle both property names)
      const availableTimes = doctor.availableTimes || doctor.availability || [];

      availableTimes.forEach(time => {
        const option = document.createElement("option");
        option.value = time;
        option.textContent = time;
        appointmentTimeEl.appendChild(option);
      });

      // Set the pre-selected time AFTER options are populated so the value sticks
      if (appointmentTimeEl) appointmentTimeEl.value = appointmentTime;

      // Handle form submission for updating the appointment
      const form = document.getElementById("updateAppointmentForm");
      if (form) {
        form.addEventListener("submit", async (e) => {
          e.preventDefault();

          const date = document.getElementById("appointmentDate")?.value;
          const time = document.getElementById("appointmentTime")?.value;

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

          const startTime = time.includes('-') ? time.split('-')[0] : time;

          const updatedAppointment = {
            id: appointmentId,
            doctor: { id: doctor.id },
            patient: { id: patientId },
            appointmentTime: `${date}T${startTime}:00`,
            status: 0
          };

          const updateResponse = await updateAppointment(updatedAppointment, token);

          if (updateResponse.success) {
            alert("Appointment updated successfully!");
            window.location.href = "/pages/patientAppointments.html";
          } else {
            alert("❌ Failed to update appointment: " + updateResponse.message);
          }
        });
      }
    })
    .catch(error => {
      console.error("Error fetching doctors:", error);
      alert("❌ Failed to load doctor data.");
    });
}

