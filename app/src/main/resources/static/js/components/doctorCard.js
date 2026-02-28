doctorCard.js
This component creates a dynamic, reusable card for displaying doctor information on the Admin and Patient dashboards. Each card can show a doctor's name, specialty, availability, and contact info, along with action buttons like "Delete" or "Book Appointment."

It improves separation of concerns by encapsulating UI rendering and interactivity in a single module.

Task:
 pen doctorCard.js in IDE

Define the Function

export function createDoctorCard(doctor) 

Create a named export so other files can import and use this function.
Accept one argument: a doctor object containing info like name, specialty, etc.

Create the Main Card Container

const card = document.createElement("div");
card.classList.add("doctor-card");

Dynamically create a <div> element.
Add a CSS class doctor-card for styling purposes.

Fetch the User’s Role

const role = localStorage.getItem("userRole");

Read the current user’s role (admin, patient, loggedPatient) from localStorage.
You’ll use this later to decide which buttons to show.

Create Doctor Info Section

const infoDiv = document.createElement("div");
infoDiv.classList.add("doctor-info");

Make a nested container to hold the doctor’s name, specialty, email, and availability.
Then add individual elements:

const name = document.createElement("h3");
name.textContent = doctor.name;

Create a heading element and set the text to the doctor’s name.

Repeat similarly for:

specialization
email
availability (you can join an array with join(", ") to display multiple times)

Then append them all:

infoDiv.appendChild(name);
infoDiv.appendChild(specialization);
infoDiv.appendChild(email);
infoDiv.appendChild(availability);

Create Button Container

const actionsDiv = document.createElement("div");
actionsDiv.classList.add("card-actions");

A new <div> to hold buttons like “Delete” or “Book Now”.
Conditionally Add Buttons Based on Role

Admin:
if (role === "admin") {
  const removeBtn = document.createElement("button");
  removeBtn.textContent = "Delete";

Add a delete button only for admins.
Attach an event:

removeBtn.addEventListener("click", async () => {
  // 1. Confirm deletion
  // 2. Get token from localStorage
  // 3. Call API to delete
  // 4. On success: remove the card from the DOM
});

Patient (not logged in):
else if (role === "patient") {
  const bookNow = document.createElement("button");
  bookNow.textContent = "Book Now";
  bookNow.addEventListener("click", () => {
    alert("Patient needs to login first.");
  });
}

Shows a button but alerts the user that login is required.

Logged-in Patient:

else if (role === "loggedPatient") {
  const bookNow = document.createElement("button");
  bookNow.textContent = "Book Now";
  bookNow.addEventListener("click", async (e) => {
    const token = localStorage.getItem("token");
    const patientData = await getPatientData(token);
    showBookingOverlay(e, doctor, patientData);
  });
}

Allows real booking by calling a function from another module.
Fetches patient data first
- Final Assembly
Add all the created pieces to the card:

card.appendChild(infoDiv);
card.appendChild(actionsDiv);

Return the final card:
return card;

Notes:
This component uses helper functions imported from service files that will be implemented in the next lab:

deleteDoctor() from: /js/services/doctorServices.js
getPatientData() from: /js/services/patientServices.js

These service modules will handle API interactions and are part of the modular architecture designed for better maintainability and code reuse.

/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
