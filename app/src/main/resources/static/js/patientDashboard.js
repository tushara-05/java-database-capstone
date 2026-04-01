// patientDashboard.js
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

// Consolidated single DOMContentLoaded listener
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }
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

document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpeciality")?.addEventListener("change", filterDoctorsOnChange);

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
      alert("An error occurred while filtering doctors.");
    });
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name")?.value;
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;
    const phone = document.getElementById("phone")?.value;
    const address = document.getElementById("address")?.value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
      window.location.reload();
    } else {
      alert(message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("An error occurred while signing up.");
  }
};

window.loginPatient = async function () {
  try {
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;

    // Backend Login DTO uses 'identifier' field (not 'email')
    const data = { identifier: email, password };

    const response = await patientLogin(data);

    if (response.ok) {
      const result = await response.json();
      // Set userRole directly instead of relying on global selectRole from render.js
      localStorage.setItem('userRole', 'loggedPatient');
      localStorage.setItem('token', result.token);
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('Invalid credentials!');
    }
  } catch (error) {
    console.error("Error :: loginPatient :: ", error);
    alert("Failed to login. Please try again.");
  }
};
