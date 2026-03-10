// header.js

// openModal is imported and made globally available in modals.js

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // Check if current page is the root page (handles both "/" and "index.html")
  const pathname = window.location.pathname;
  const isRoot = pathname === "/" || pathname.endsWith("index.html") || pathname === "";

  if (isRoot) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Handle invalid session
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = `<header class="header">
    <div class="logo-section">
      <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
      <span class="logo-title">Hospital CMS</span>
    </div>
    <nav>`;

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button id="homeBtn" class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  headerContent += `
    </nav>
  </header>`;

  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
  const patientLoginBtn = document.getElementById('patientLogin');
  const patientSignupBtn = document.getElementById('patientSignup');

  if (patientLoginBtn) {
    patientLoginBtn.addEventListener('click', () => openModal('patientLogin'));
  }

  if (patientSignupBtn) {
    patientSignupBtn.addEventListener('click', () => openModal('patientSignup'));
  }
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('userRole');
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem('token');
  localStorage.setItem('userRole', 'patient');
  window.location.href = "/pages/patientDashboard.html";
}

// Make functions globally accessible
window.logout = logout;
window.logoutPatient = logoutPatient;

// Render header on load
renderHeader();
