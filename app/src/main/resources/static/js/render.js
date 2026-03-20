// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    if (token) {
      window.location.href = `/adminDashboard/${token}`;
    } else {
      alert("Session expired or not logged in. Please log in again.");
      window.location.href = "/";
    }
  } else if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    if (token) {
      window.location.href = `/doctorDashboard/${token}`;
    } else {
      alert("Session expired or not logged in. Please log in again.");
      window.location.href = "/";
    }
  } else if (role === "loggedPatient") {
    window.location.href = "/pages/loggedPatientDashboard.html";
  }
}


function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
