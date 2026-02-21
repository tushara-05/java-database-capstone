// doctorCard.js

import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js";

/**
 * Creates a dynamic doctor card element.
 * @param {Object} doctor - Doctor object with { name, specialization, email, availability }.
 * @returns {HTMLElement} - DOM element representing the doctor card.
 */
export function createDoctorCard(doctor) {
    // Main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Fetch current user role
    const role = localStorage.getItem("userRole");

    // Doctor info section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialization}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    availability.textContent = `Available: ${doctor.availability.join(", ")}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Action buttons container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // ADMIN: Delete button
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.addEventListener("click", async () => {
            if (!confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) return;

            const token = localStorage.getItem("token");
            try {
                const result = await deleteDoctor(doctor.id, token);
                if (result.success) {
                    alert("Doctor deleted successfully.");
                    card.remove();
                } else {
                    alert("Failed to delete doctor.");
                }
            } catch (err) {
                console.error(err);
                alert("Error deleting doctor.");
            }
        });
        actionsDiv.appendChild(removeBtn);
    }

    // PATIENT (not logged in)
    else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Please log in first to book an appointment.");
        });
        actionsDiv.appendChild(bookNow);
    }

    // LOGGED-IN PATIENT
    else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/pages/patientDashboard.html";
                return;
            }
            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(e, doctor, patientData);
            } catch (err) {
                console.error(err);
                alert("Unable to fetch patient data.");
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // Assemble card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
