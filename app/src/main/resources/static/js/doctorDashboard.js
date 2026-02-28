doctorDashboard.js – Managing Appointments
Open the JS File:
Open doctorDashboard.js located at app/src/main/resources/static/js/doctorDashboard.js.
Open doctorDashboard.js in IDE

Import Required Modules : At the top of the file, import:
The  getAllAppointments function from the modal component file ./services/appointmentRecordService.js.
The createPatientRow  function from the doctorCard component file ./components/patientRows.js.
Initialize Global Variables:

Define and store:

A reference to the appointment table body where rows will be rendered (#patientTableBody).
selectedDate, initialized to today’s date.
token, retrieved from localStorage (used for authentication).
patientName, initialized as null, for search filtering.
Setup Search Bar Functionality:

Add an event listener to the search bar (#searchBar):

On input change, update the patientName variable.
If the search input is empty, default patientName to "null".
Call loadAppointments() to refresh the list with the filtered data.
Bind Event Listeners to Filter Controls:

“Today’s Appointments” button (#todayButton):

Resets the selectedDate to today.
Updates the date picker field to reflect today’s date.
Calls loadAppointments().
Date picker (#datePicker):

Updates the selectedDate variable when changed.
Calls loadAppointments() to fetch and display appointments for the selected date.
Define loadAppointments() Function:

This function:

Uses getAllAppointments(selectedDate, patientName, token) to fetch appointment data.

Clears existing content in the table.

If no appointments are found:

Displays a row with a “No Appointments found for today” message.
If appointments exist:

For each appointment, extract the patient’s details.
Use createPatientRow() to create a <tr> for each.
Append each row to the appointment table body.
Wrap this logic in a try-catch block:

In case of error, display a fallback error message row in the table.
Initial Render on Page Load:

When the page is loaded (DOMContentLoaded):

Call renderContent() (if used).
Call loadAppointments() to load today’s appointments by default.
Notes
The getAllAppointments() function is responsible for backend API calls based on the selected date and search term.
The createPatientRow() component is used to dynamically build each row of the appointments table.
All API calls should include the doctor’s token for authentication (retrieved from localStorage).
Always use async/await syntax when working with fetch() to ensure proper flow control and error handling.
Ensure meaningful fallback messages are shown if no appointments are found or if the API fails.
/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
