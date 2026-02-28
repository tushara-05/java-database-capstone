adminDashboard.js - Managing Doctors
Open the JS File:
Open adminDashboard.js located at app/src/main/resources/static/js/adminDashboard.js.
Open adminDashboard.js in IDE

Import Required Modules : At the top of the file, import:
The openModal function from the modal component file ../components/modals.js.
The getDoctors , filterDoctors , saveDoctor functions from the doctorServices component file ./services/doctorServices.js.
The createDoctorCard function from the doctorCard component file ./components/doctorCard.js.
Event Binding :

When the admin clicks the “Add Doctor” button, it triggers openModal(‘addDoctor’)

  document.getElementById('addDocBtn').addEventListener('click', () => {
 openModal('addDoctor');
});

Load Doctor Cards on Page Load:

Use DOMContentLoaded or window.onload to trigger loadDoctorCards() on page load.
loadDoctorCards(): Fetch all doctors and display them in the dashboard.
Calls getDoctors() to fetch doctor list from backend.
Clears existing content (innerHTML = "").

 const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = ""; 

Iterates through results and injects them using createDoctorCard() of './components/doctorCard.js'.
Appends each card to the contentDiv.
Implement Search and Filter Logic:

Set up event listeners on:
#searchBar
Filter dropdowns

document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

Create filterDoctorsOnChange() function:
Gathers current filter/search values
Fetches and displays filtered results using filterDoctors() from './services/doctorServices.js'.
If no match, show message “No doctors found”.
renderDoctorCards(doctors) : Utility function to render doctor cards when passed a list.

It iterates through the list passed doctors and display the cards.
Handle Add Doctor Modal:

When the “Add Doctor” button is clicked:
openModal() opens the modal
The modal form is populated with input fields for:
Name, specialty, email, password, mobile no., availability time.
Collects any checkbox values for doctor availability
On form submission:
Use adminAddDoctor() to collect data.
Verifies that a valid login token exists (to authenticate the admin).
Send a POST request using saveDoctor() from './services/doctorServices.js'
If successful, closes the modal, reloads the page or doctor list, and shows a success message and refresh the doctor list.
If failed, alerts the user with an error message.
Notes
Ensure the openModal() function is imported from ./components/modals.js.
Ensure the createDoctorCard function is imported from ./components/doctorCard.js.
Ensure the getDoctors , filterDoctors , saveDoctor function is imported from ./services/doctorServices.js'.
Use async/await for Api Call functions.
/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
