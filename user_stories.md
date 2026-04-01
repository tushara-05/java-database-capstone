# User Story Template
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._  
**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]  
**Priority:** [High/Medium/Low]  
**Story Points:** [Estimated Effort in Points]  
**Notes:**  
- [Additional information or edge cases]

---

### Admin User Stories

**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._  
**Acceptance Criteria:**
1. A login page that accepts a username and password.
2. Successful login leads to the Admin Dashboard.
3. An error message is displayed for incorrect credentials.  
**Priority:** High  
**Story Points:** 3  
**Notes:** Access is restricted to authorized administrative staff.

**Title:**
_As an admin, I want to log out of the portal, so that I can protect system access._  
**Acceptance Criteria:**  
1. A clear "Log Out" option is visible on all admin pages.
2. The user session is ended immediately upon logging out.
3. The user is redirected back to the login page.  
**Priority:** Medium  
**Story Points:** 1  
**Notes:** Ensures the account is not left active on shared devices.

**Title:**
_As an admin, I want to add doctors to the portal, so that I can expand the medical network._  
**Acceptance Criteria:**
1. A form to enter the doctor's name, speciality, and contact details.
2. The system confirms the details are saved correctly in the database.
3. The new doctor appears in the active doctor list immediately.  
**Priority:** High  
**Story Points:** 5  
**Notes:** Requires validation of professional credentials.

**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that I can keep the information accurate._  
**Acceptance Criteria:**
1. A delete option available for each doctor's profile in the list.
2. A confirmation prompt appears before the profile is removed.
3. The records are removed from the database and the public view.  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** To be used when a doctor is no longer part of the network.

**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to get a daily appointment report by doctor, so that I can review daily operations in the clinic efficiently._  
**Acceptance Criteria:**
1. Execution of `GetDailyAppointmentReportByDoctor` with a specific report date.
2. The procedure returns doctor names, appointment times, statuses, and patient details (name and phone).
3. Data is grouped by doctor and ordered by appointment time for clarity.  
**Priority:** Medium  
**Story Points:** 5  
**Notes:** Useful for operational staff to prepare for the day's schedule.

**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to identify the doctor who saw the most patients in a specific month, so that I can understand clinical workloads._  
**Acceptance Criteria:**
1. Execution of `GetDoctorWithMostPatientsByMonth` with a month and year as inputs.
2. The system returns the doctor ID and the total count of patients seen.
3. The procedure correctly limits the output to the single doctor with the highest patient count.  
**Priority:** Medium  
**Story Points:** 5  
**Notes:** Helps management recognize high-performing staff or identify high-demand areas.

**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to identify the doctor with the most patients in a given year, so that I can generate annual performance summaries._  
**Acceptance Criteria:**
1. Execution of `GetDoctorWithMostPatientsByYear` by providing a year.
2. The procedure identifies the top-performing doctor based on total unique patient visits for that year.
3. The output includes the doctor ID and the total patients seen.  
**Priority:** Medium  
**Story Points:** 8  
**Notes:** Essential for high-level annual reporting and performance reviews.

---

### Patient User Stories

**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._   
**Acceptance Criteria:**
1. A public page displaying all registered doctors and their specialities.
2. Information includes names, medical fields, and general availability.
3. No account or registration is required to view this list.  
**Priority:** High  
**Story Points:** 3  
**Notes:** Helps new users find the right care before signing up.

**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._   
**Acceptance Criteria:**
1. A registration form with email validation and password security.
2. The system creates a unique patient account upon submission.
3. The user can log in immediately after the account is created.  
**Priority:** High  
**Story Points:** 5  
**Notes:** The first step for patients to interact with the medical portal.

**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._  
**Acceptance Criteria:**
1. A secure login section for registered patients.
2. Successful login takes the user to their private dashboard.
3. Unauthorized access to the dashboard is prevented.  
**Priority:** High  
**Story Points:** 3  
**Notes:** Protects personal health and scheduling information.

**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account._  
**Acceptance Criteria:**
1. A visible "Log Out" button on the patient dashboard.
2. The session is closed and the user is taken back to the main website.
3. Access to personal data is restricted without a new login.  
**Priority:** Medium  
**Story Points:** 1  
**Notes:** Critical for maintaining patient privacy.

**Title:**
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._  
**Acceptance Criteria:**
1. Selection of a specific doctor and an available time slot.
2. Each appointment is set for a standard one-hour duration.
3. The system prevents double-booking for the same doctor and time.  
**Priority:** High  
**Story Points:** 8  
**Notes:** The primary feature for scheduling patient consultations.

**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._  
**Acceptance Criteria:**
1. A list of scheduled appointments shown on the user dashboard.
2. Details include the doctor's name, date, and time.
3. The list is updated in real-time as changes occur.  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** Ensures patients stay informed about their scheduled visits.

---

### Doctor User Stories

**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._  
**Acceptance Criteria:**
1. A secure login page specifically for medical staff.
2. Successful login leads to the doctor's professional dashboard.
3. Access is restricted to verified medical accounts only.  
**Priority:** High  
**Story Points:** 3  
**Notes:** Main entrance for doctors to manage clinical operations.

**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my data._  
**Acceptance Criteria:**
1. A logout option is clearly available within the dashboard.
2. The session is terminated and patient info is cleared from the screen.
3. Redirection to the homepage to ensure session security.  
**Priority:** High  
**Story Points:** 1  
**Notes:** Essential for maintaining professional confidentiality.

**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._  
**Acceptance Criteria:**
1. A calendar view displaying all confirmed patient visits.
2. Ability to view specific details for each scheduled appointment.
3. The calendar updates automatically as patients book or cancel.  
**Priority:** High  
**Story Points:** 5  
**Notes:** Helps doctors plan their daily and weekly work.

**Title:**
_As a doctor, I want to mark my unavailability, so that I can inform patients only the available slots._  
**Acceptance Criteria:**
1. Interface to select dates or times when the doctor is not available.
2. The system removes these slots from the patient booking view.
3. Confirmation message showing successful update of availability.  
**Priority:** Medium  
**Story Points:** 5  
**Notes:** Crucial for managing doctor schedules and avoiding conflicts.

**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._  
**Acceptance Criteria:**
1. A profile management page where the doctor can edit their details.
2. Fields for updating specialization, bio, and professional phone number.
3. Updates are reflected immediately across the portal.  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** Allows doctors to keep their public-facing information current.

**Title:**
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._   
**Acceptance Criteria:**
1. Access to the name and reason for the visit for each scheduled patient.
2. Information is only visible to the doctor assigned to the visit.
3. Data is presented clearly to allow for a quick review before a consultation.    
**Priority:** High  
**Story Points:** 5  
**Notes:** Vital for providing high-quality clinical care.  

---
