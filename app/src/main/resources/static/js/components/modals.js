// modals.js
export function openModal(type) {
  let modalContent = '';
  if (type === 'addDoctor') {
    modalContent = `
         <h2>Add Doctor</h2>
         <form onsubmit="event.preventDefault();">
         <label for="doctorName" class="sr-only">Doctor Name</label>
         <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field" autocomplete="name">
         
         <label for="speciality" class="sr-only">Speciality</label>
         <select id="speciality" class="input-field select-dropdown">
             <option value="">Speciality</option>
                        <option value="cardiologist">Cardiologist</option>
                        <option value="dermatologist">Dermatologist</option>
                        <option value="neurologist">Neurologist</option>
                        <option value="pediatrician">Pediatrician</option>
                        <option value="orthopedic">Orthopedic</option>
                        <option value="gynecologist">Gynecologist</option>
                        <option value="psychiatrist">Psychiatrist</option>
                        <option value="dentist">Dentist</option>
                        <option value="ophthalmologist">Ophthalmologist</option>
                        <option value="ent">ENT Specialist</option>
                        <option value="urologist">Urologist</option>
                        <option value="oncologist">Oncologist</option>
                        <option value="gastroenterologist">Gastroenterologist</option>
                        <option value="general">General Physician</option>

        </select>
        
        <label for="doctorEmail" class="sr-only">Email</label>
        <input type="email" id="doctorEmail" placeholder="Email" class="input-field" autocomplete="email">
        
        <label for="doctorPassword" class="sr-only">Password</label>
        <input type="password" id="doctorPassword" placeholder="Password" class="input-field" autocomplete="new-password">
        
        <label for="doctorPhone" class="sr-only">Mobile No.</label>
        <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field" autocomplete="tel">
        
        <div class="availability-container">
        <span class="availabilityLabel">Select Availability:</span>
          <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00"> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00"> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00"> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00"> 12:00 PM - 1:00 PM</label>
              <label><input type="checkbox" name="availability" value="13:00"> 1:00 PM - 2:00 PM</label>
              <label><input type="checkbox" name="availability" value="14:00"> 2:00 PM - 3:00 PM</label>
              <label><input type="checkbox" name="availability" value="15:00"> 3:00 PM - 4:00 PM</label>
              <label><input type="checkbox" name="availability" value="16:00"> 4:00 PM - 5:00 PM</label>
          </div>
        </div>
        <button type="button" class="dashboard-btn" id="saveDoctorBtn">Save</button>
        </form>
      `;
  } else if (type === 'patientLogin') {
    modalContent = `
        <h2>Patient Login</h2>
        <form onsubmit="event.preventDefault();">
        <input type="text" id="email" placeholder="Email" class="input-field" autocomplete="email">
        <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="current-password">
        <button type="button" class="dashboard-btn" id="loginBtn">Login</button>
        </form>
      `;
  }
  else if (type === "patientSignup") {
    modalContent = `
      <h2>Patient Signup</h2>
      <form onsubmit="event.preventDefault();">
      <input type="text" id="name" placeholder="Name" class="input-field" autocomplete="name">
      <input type="email" id="email" placeholder="Email" class="input-field" autocomplete="email">
      <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="new-password">
      <input type="text" id="phone" placeholder="Phone" class="input-field" autocomplete="tel">
      <input type="text" id="address" placeholder="Address" class="input-field" autocomplete="street-address">
      <button type="button" class="dashboard-btn" id="signupBtn">Signup</button>
      </form>
    `;

  } else if (type === 'adminLogin') {
    modalContent = `
        <h2>Admin Login</h2>
        <form onsubmit="event.preventDefault();">
        <input type="text" id="username" name="username" placeholder="Username" class="input-field" autocomplete="username">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field" autocomplete="current-password">
        <button type="button" class="dashboard-btn" id="adminLoginBtn" >Login</button>
        </form>
      `;
  } else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <form onsubmit="event.preventDefault();">
        <input type="text" id="email" placeholder="Email" class="input-field" autocomplete="email">
        <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="current-password">
        <button type="button" class="dashboard-btn" id="doctorLoginBtn" >Login</button>
        </form>
      `;
  }

  document.getElementById('modal-body').innerHTML = modalContent;
  document.getElementById('modal').style.display = 'block';

  document.getElementById('closeModal').onclick = () => {
    document.getElementById('modal').style.display = 'none';
  };

  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", () => window.signupPatient?.());
  }

  if (type === "patientLogin") {
    document.getElementById("loginBtn").addEventListener("click", () => window.loginPatient?.());
  }

  if (type === 'addDoctor') {
    document.getElementById('saveDoctorBtn').addEventListener('click', () => window.adminAddDoctor?.());
  }

  if (type === 'adminLogin') {
    document.getElementById('adminLoginBtn').addEventListener('click', () => window.adminLoginHandler?.());
  }

  if (type === 'doctorLogin') {
    document.getElementById('doctorLoginBtn').addEventListener('click', () => window.doctorLoginHandler?.());
  }
}

// Make openModal available globally for inline HTML onClick handlers used in header.js
window.openModal = openModal;
