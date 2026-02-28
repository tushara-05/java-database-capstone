patientServices.js
This module centralizes all API communication related to patient data. It handles sign-up, login, appointment management. Keeping this logic separated from UI code improves reusability and maintainability.

Open the patientServices.js file app/src/main/resources/static/js/services/patientServices.js

Open patientServices.js in IDE

Import the API Base URL
Start by importing the base URL from your configuration file.

import { API_BASE_URL } from "../config/config.js";

You’ll use this base to construct specific endpoint URLs for patient-related actions.

Set the Base Patient API Endpoint
Define a constant (e.g., PATIENT_API) that represents the base path for all patient-related requests — typically /api/patient.

const PATIENT_API = API_BASE_URL + '/patient'

This avoids duplicating the path in multiple places and makes future updates easier.

Create a Function to Handle Patient Signup
This function patientSignup(data) will:

Accept a data object with patient details (name, email, password, etc.).
Send a POST request to the signup endpoint.
Include the patient details as JSON in the request body.
Wait for the response, extract the message, and return a structured object with success and message properties.
Handle any failures with a try-catch block and return an appropriate error message.
This function allows users to register as a patient through your frontend app.

Create a Function for Patient Login
This function patientLogin(data):

Accepts login credentials (typically email and password).
Sends a POST request to the login endpoint.
Includes headers indicating JSON content and passes the login data in the body.
Returns the full fetch response so the frontend can check status, extract token, etc.
Logging the input data can help during development (but should be removed in production).
Used during login to authenticate patients and allow secure access to dashboards or features.

Create a Function to Fetch Logged-in Patient Data
This function getPatientData(token):

Takes an authentication token (from localStorage).
Sends a GET request using this token to retrieve the patient’s details (name, id, etc.).
Returns the patient object if successful.
Handles any errors gracefully and returns null if the request fails.
Used when booking appointments or viewing patient profile information.

Create a Function to Fetch Patient Appointments
This function getPatientAppointments(id, token ,user) is a bit more dynamic:

Accepts three parameters:

id: Patient’s unique identifier
token: Authentication token
user: String indicating who’s requesting (e.g., "patient" or "doctor")
Constructs a dynamic API URL that works for both dashboards — doctor and patient.

Sends a GET request and returns the appointments array.

If unsuccessful, logs the error and returns null.

A single, shared API call supports both dashboards with role-based behavior on the backend.

Create a Function to Filter Appointments
This function filterAppointments(condition ,name ,token):

Accepts condition (like "pending" or "consulted"), name, and a token.
Sends a GET request to a filtered endpoint.
Returns the list of filtered appointments if the request is successful.
Returns an empty list if something fails, and logs errors for easier debugging.
Alerts the user if the error is unexpected.
Helps in real-time filtering and searching of appointments, improving the user experience.

Task
Use clear function names (patientSignup, getPatientAppointments) that reflect the purpose.
Wrap all async code in try-catch to handle API or network errors.
Return structured, consistent outputs from service functions (e.g., { success, message }).
Avoid repeating base URLs; build them from a central config.js.
Use comments inside each function to indicate what step is happening — especially useful for collaboration or learning teams.
By organizing all patient-related API communication in patientServices.js, you:

Keep your UI code (like in dashboards) cleaner and easier to read.
Make the app easier to debug, extend, and maintain.
Enable code reusability across different user roles.
Reduce the risk of introducing bugs by avoiding repeated logic.
// patientServices
import { API_BASE_URL } from "../config/config.js";
const PATIENT_API = API_BASE_URL + '/patient'


//For creating a patient in db
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`,
      {
        method: "POST",
        headers: {
          "Content-type": "application/json"
        },
        body: JSON.stringify(data)
      }
    );
    const result = await response.json();
    if (!response.ok) {
      throw new Error(result.message);
    }
    return { success: response.ok, message: result.message }
  }
  catch (error) {
    console.error("Error :: patientSignup :: ", error)
    return { success: false, message: error.message }
  }
}

//For logging in patient
export async function patientLogin(data) {
  console.log("patientLogin :: ", data)
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });


}

// For getting patient data (name ,id , etc ). Used in booking appointments
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();
    if (response.ok) return data.patient;
    return null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

// the Backend API for fetching the patient record(visible in Doctor Dashboard) and Appointments (visible in Patient Dashboard) are same based on user(patient/doctor).
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();
    console.log(data.appointments)
    if (response.ok) {
      return data.appointments;
    }
    return null;
  }
  catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(`${PATIENT_API}/filter/${condition}/${name}/${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      const data = await response.json();
      return data;

    } else {
      console.error("Failed to fetch doctors:", response.statusText);
      return { appointments: [] };

    }
  } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong!");
    return { appointments: [] };
  }
}
