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

## Admin User Stories
**User Story 1 — Admin Login**
**Title:**
As an admin, I want to log into the portal with my username and password, so that I can securely manage the platform.
**Acceptance Criteria:**
1.	Admin can enter username and password.
2.	System validates credentials against stored admin data.
3.	Admin is redirected to the dashboard upon successful login.
4.	Invalid credentials show an appropriate error message.
**Priority:** High
**Story Points:** 3
**Notes:**
⦁	Consider lockout after multiple failed attempts.
**User Story 2 — Admin Logout**
**Title:**
As an admin, I want to log out of the portal, so that I can protect access to the system when I’m not using it.
**Acceptance Criteria:**
1.	Logout button is available on all admin dashboard pages.
2.	Clicking logout ends the user session.
3.	Admin is redirected to the login page after logout.
4.	Protected pages cannot be accessed without logging in again.
**Priority:** High
**Story Points:** 2
**Notes:**
⦁	Ensure session invalidation on server-side.
**User Story 3 — Add Doctor**
**Title:**
As an admin, I want to add a doctor to the portal, so that the system can include new healthcare providers.
**Acceptance Criteria:**
1.	Admin can access an “Add Doctor” form.
2.	Required fields include name, email, specialization, and contact information.
3.	System validates the entered details.
4.	Doctor is saved in the MySQL database upon successful submission.
5.	Admin sees a confirmation message after the doctor is added.
**Priority:** High
**Story Points:** 5
**Notes:**
⦁	Validate email format and prevent duplicates.
**User Story 4 — Delete Doctor Profile**
**Title:**
As an admin, I want to delete a doctor’s profile from the portal, so that outdated or invalid doctor records are removed.
**Acceptance Criteria:**
1.	Admin can view a list of registered doctors.
2.	A “Delete” option exists for each doctor.
3.	System asks for confirmation before deletion.
4.	Doctor is removed from the MySQL database.
5.	Admin sees a success message after deletion.
**Priority:** Medium
**Story Points:** 3
**Notes:**
⦁	Consider preventing deletion if appointments are still tied to the doctor.
**User Story 5 — Run Monthly Appointment Statistics Procedure**
**Title:**
As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track system usage statistics.
**Acceptance Criteria:**
1.	Admin can access the MySQL CLI or admin database tools.
2.	Stored procedure returns appointment counts grouped by month.
3.	Results are displayed clearly in tabular form.
4.	Procedure completes with no errors when data is available.
**Priority:** Medium
**Story Points:** 2
**Notes:**
1.	Stored procedure name and parameters should be documented.
2.	Useful for administrative reporting and analytics.

## Patient User Stories
**User Story 1 — View Doctors (Public Access)**
**Title:**
As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering.
**Acceptance Criteria:**
1.	Patient can access a publicly available doctor list page.
2.	List displays key information: doctor name, specialization, and availability.
3.	No login or registration is required.
4.	Page loads quickly and is accessible on mobile devices.
**Priority:** High
**Story Points:** 3
**Notes:**
⦁	Optional filters such as specialization or location can be added later.
**User Story 2 — Patient Registration**
**Title:**
As a patient, I want to sign up using my email and password, so that I can book appointments.
**Acceptance Criteria:**
1.	Registration form includes email, password, and basic personal details.
2.	System validates email format and enforces strong password rules.
3.	System prevents registration if email already exists.
4.	Successful registration creates a patient account in MySQL.
**Priority:** High
**Story Points:** 5
**Notes:**
⦁	Consider sending a verification email in future iterations.
**User Story 3 — Patient Login**
**Title:**
As a patient, I want to log into the portal, so that I can manage my bookings.
**Acceptance Criteria:**
1.	Patient enters valid login credentials.
2.	System authenticates patient via stored credentials.
3.	Patient is redirected to their dashboard upon successful login.
4.	Invalid credentials show an appropriate error message.
**Priority:** High
**Story Points:** 3
**Notes:**
⦁	Implement session management and inactivity timeout.
**User Story 4 — Patient Logout**
**Title:**
As a patient, I want to log out of the portal, so that I can secure my account.
**Acceptance Criteria:**
1.	Logout button visible on all patient dashboard pages.
2.	Clicking logout ends the current session.
3.	Patient is redirected to the login page after logout.
4.	Protected pages cannot be accessed without logging in.
**Priority:** High
**Story Points:** 2
**Notes:**
⦁	Ensure server-side session invalidation.
**User Story 5 — Book a Doctor Appointment**
**Title:**
As a patient, I want to log in and book an hour-long appointment with a doctor, so that I can consult with them.
**Acceptance Criteria:**
1.	Patient must be logged in to book.
2.	Patient can choose a doctor and view available time slots.
3.	Booking duration is fixed at one hour.
4.	System prevents double-booking of the same time slot.
5.	Confirmation message is displayed after booking.
6.	Appointment is saved in MySQL.
**Priority:** High
**Story Points:** 5
**Notes:**
⦁	Future enhancement: appointment reminders via email/SMS.
**User Story 6 — View Upcoming Appointments**
**Title:**
As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.
**Acceptance Criteria:**
1.	Patient can access a “My Appointments” page after logging in.
2.	Page lists upcoming appointments with date, time, and doctor details.
3.	Appointments are sorted chronologically.
4.	Only the logged-in patient’s appointments are shown.
**Priority:** Medium
**Story Points:** 3
**Notes:**
⦁	Later enhancement: allow appointment cancellation or rescheduling.     

## Doctor User Stories
**User Story 1 — Doctor Login**
**Title:** 
As a doctor, I want to log into the portal, so that I can manage my appointments.
**Acceptance Criteria:**
1.	Doctor can enter valid login credentials.
2.	System authenticates credentials stored in the database.
3.	Doctor is redirected to the doctor dashboard after successful login.
4.	Invalid credentials display an appropriate error message.
**Priority:** High
**Story Points**: 3
**Notes:**
⦁	Enforce security measures such as strong password policy and session timeout.
**User Story 2 — Doctor Logout**
**Title:**
As a doctor, I want to log out of the portal, so that I can protect my data.
**Acceptance Criteria:**
1.	Logout button is accessible on all doctor dashboard pages.
2.	Logging out ends the active session.
3.	Doctor is redirected to the login page.
4.	Access to protected pages requires logging in again.
**Priority:** High
**Story Points:** 2
**Notes:**
⦁	Server-side session invalidation must be ensured.
**User Story 3 — View Appointment Calendar**
**Title:**
As a doctor, I want to view my appointment calendar, so that I can stay organized.
**Acceptance Criteria:**
1.	Doctor can see a calendar-style or list-style view of all upcoming appointments.
2.	Appointments display patient name, date, time, and appointment type.
3.	Appointments are presented in chronological order.
4.	Doctor can filter by day, week, or month.
**Priority:** Medium
**Story Points:** 5
**Notes:**
⦁	Future enhancement: integration with Google Calendar or exporting schedule.
**User Story 4 — Mark Unavailability**
**Title:**
As a doctor, I want to mark my unavailability, so that patients can only book available slots.
**Acceptance Criteria:**
1.	Doctor can select specific dates/times to mark as unavailable.
2.	Marked unavailable slots are saved in the database.
3.	Patients cannot book appointments during unavailable times.
4.	Calendar clearly distinguishes between available and unavailable slots.
**Priority:** High
**Story Points:** 5
**Notes:**
⦁	Handle recurring unavailability (e.g., every Friday afternoon) in future iterations.
**User Story 5 — Update Doctor Profile**
**Title:**
As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information.
**Acceptance Criteria:**
1.	Doctor can access an “Edit Profile” page.
2.	Editable fields include specialization, phone number, and description/experience.
3.	Updated information is validated before saving.
4.	Changes are reflected in the patient-facing doctor directory.
**Priority:** Medium
**Story Points:** 3
**Notes:**
⦁	Profile picture upload could be added later.
**User Story 6 — View Patient Details for Upcoming Appointments**
**Title:**
As a doctor, I want to view the patient details for my upcoming appointments, so that I can be prepared.
**Acceptance Criteria:**
1.	Doctor can click on an appointment to view details.
2.	Details include patient name, contact info, and medical notes (if applicable).
3.	Only patient data associated with the doctor is accessible.
4.	Data is read-only for compliance and integrity.
**Priority:** High
**Story Points:** 3
**Notes:**
⦁	Ensure compliance with patient privacy regulations.                   
