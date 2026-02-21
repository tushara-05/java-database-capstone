// index.js
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// Define API endpoints
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

// Ensure DOM elements exist before attaching listeners
window.onload = function () {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  if (adminBtn) {
    adminBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

/**
 * Handles Admin login
 */
window.adminLoginHandler = async function () {
  try {
    const username = document.getElementById("adminUsername").value.trim();
    const password = document.getElementById("adminPassword").value.trim();

    const admin = { username, password };

    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert("Invalid credentials!");
    }
  } catch (err) {
    console.error(err);
    alert("Error occurred during login. Please try again.");
  }
};

/**
 * Handles Doctor login
 */
window.doctorLoginHandler = async function () {
  try {
    const email = document.getElementById("doctorEmail").value.trim();
    const password = document.getElementById("doctorPassword").value.trim();

    const doctor = { email, password };

    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert("Invalid credentials!");
    }
  } catch (err) {
    console.error(err);
    alert("Error occurred during login. Please try again.");
  }
};

/**
 * Saves the selected role in localStorage and redirects accordingly
 * @param {string} role - Role to set ("admin" or "doctor")
 */
function selectRole(role) {
  localStorage.setItem("userRole", role);

  // Redirect based on role
  if (role === "admin") {
    window.location.href = "/pages/adminDashboard.html";
  } else if (role === "doctor") {
    window.location.href = "/pages/doctorDashboard.html";
  }
}
