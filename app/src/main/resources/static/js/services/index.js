// index.js - Role-Based Login Handling

// Import the openModal function to handle showing login popups/modals
import { openModal } from "../components/modals.js";

// Import the base API URL from the config file
import { API_BASE_URL } from "../config/config.js";

// Import patient login service
import { patientLogin } from "./patientServices.js";

// Define constants for the admin and doctor login API endpoints using the base URL
const ADMIN_API = API_BASE_URL + '/admin/login';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Use DOMContentLoaded to ensure DOM elements are available
document.addEventListener('DOMContentLoaded', () => {
  // Select the role buttons using getElementById
  const adminBtn = document.getElementById('adminLogin');
  const doctorBtn = document.getElementById('doctorLogin');
  const patientBtn = document.getElementById('patientLogin');

  // If the admin login button exists, add a click event listener
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      openModal('adminLogin');
    });
  }

  // If the doctor login button exists, add a click event listener
  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => {
      openModal('doctorLogin');
    });
  }

  // If the patient login button exists, add a click event listener
  if (patientBtn) {
    patientBtn.addEventListener('click', () => {
      openModal('patientLogin');
    });
  }
});

// Define adminLoginHandler on the global window object
// This function will be triggered when the admin submits their login credentials
window.adminLoginHandler = async function () {
  // Step 1: Get the entered username and password from the modal input fields
  const username = document.getElementById('username')?.value;
  const password = document.getElementById('password')?.value;

  // Step 2: Create an admin object with these credentials
  const admin = { username, password };

  try {
    // Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin)
    });

    // Step 4: If the response is successful, extract token and store it
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('admin');
    } else {
      // Step 5: If login fails, alert the user
      alert("Invalid credentials!");
    }
  } catch (error) {
    // Step 6: Catch and alert any unexpected network or server errors
    console.error("Admin login error:", error);
    alert("An error occurred during login. Please try again.");
  }
};

// Define doctorLoginHandler on the global window object
// This function will be triggered when a doctor submits their login credentials
window.doctorLoginHandler = async function () {
  // Step 1: Get the entered email and password from the modal input fields
  const email = document.getElementById('email')?.value;
  const password = document.getElementById('password')?.value;

  // Step 2: Create a doctor object — backend Login DTO uses 'identifier' field
  const doctor = { identifier: email, password };

  try {
    // Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor)
    });

    // Step 4: If login is successful, extract token and store it
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('doctor');
    } else {
      // Step 5: If login fails, alert the user
      alert("Invalid credentials!");
    }
  } catch (error) {
    // Step 6: Wrap in a try-catch block to handle errors gracefully
    console.error("Doctor login error:", error);
    alert("An error occurred during login. Please try again.");
  }
};

// Define loginPatient on the global window object
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;

    const data = { identifier: email, password };
    const response = await patientLogin(data);

    if (response.ok) {
      const result = await response.json();
      localStorage.setItem('userRole', 'loggedPatient');
      localStorage.setItem('token', result.token);
      selectRole('loggedPatient');
    } else {
      alert('Invalid credentials!');
    }
  } catch (error) {
    console.error("Error :: loginPatient :: ", error);
    alert("An error occurred during login. Please try again.");
  }
};
