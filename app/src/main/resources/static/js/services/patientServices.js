// patientServices.js
/**
 * Patient Services Module
 * Centralizes all API communication related to patient data.
 * Handles sign-up, login, appointment management, and data retrieval.
 */

import { API_BASE_URL } from "../config/config.js";

// Base endpoint for all patient-related API requests
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Handle patient registration/signup
 * @param {Object} data - Patient details (name, email, password, etc.)
 * @returns {Promise<Object>} Structured response with success status and message
 */
export async function patientSignup(data) {
  try {
    // Step 1: Send POST request to signup endpoint with patient details
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    // Step 2: Parse JSON response
    const result = await response.json();

    // Step 3: Check if request was successful
    if (!response.ok) {
      throw new Error(result.message);
    }

    // Step 4: Return structured response
    return { success: response.ok, message: result.message };
  } catch (error) {
    // Step 5: Handle errors gracefully
    console.error("Error :: patientSignup :: ", error);
    return { success: false, message: error.message };
  }
}

/**
 * Authenticate patient login
 * @param {Object} data - Login credentials (email, password)
 * @returns {Promise<Response>} Full fetch response for token extraction
 */
export async function patientLogin(data) {
  // Send POST request to login endpoint
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });
}

/**
 * Fetch logged-in patient's profile data
 * @param {string} token - Authentication token from localStorage
 * @returns {Promise<Object|null>} Patient object if successful, null if failed
 */
export async function getPatientData(token) {
  try {
    // Step 1: Send GET request with authentication token
    const response = await fetch(`${PATIENT_API}/${token}`);

    // Step 2: Parse JSON response
    const data = await response.json();

    // Step 3: Return patient data if successful
    if (response.ok) return data.patient;

    return null;
  } catch (error) {
    // Step 4: Handle errors gracefully
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Fetch patient appointments (supports both patient and doctor dashboards)
 * @param {number} id - Patient's unique identifier
 * @param {string} token - Authentication token
 * @param {string} user - Role requesting data ("patient" or "doctor") - currently unused but kept for compatibility
 * @returns {Promise<Array|null>} Appointments array if successful, null if failed
 */
export async function getPatientAppointments(id, token, user) {
  try {
    // Step 1: Construct URL - backend expects /patient/{id}/{token}
    const response = await fetch(`${PATIENT_API}/${id}/${token}`);

    // Step 2: Parse JSON response
    const data = await response.json();

    // Step 3: Return appointments if request successful
    if (response.ok) {
      return data.appointments;
    }
    return null;
  } catch (error) {
    // Step 4: Handle errors and return null
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Filter appointments by condition and/or name
 * @param {string} condition - Filter condition (e.g., "pending", "consulted")
 * @param {string} name - Patient or doctor name to search
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Filtered appointments or empty list with success status
 */
export async function filterAppointments(id, condition, name, token) {
  try {
    const searchCondition = condition || '';
    const searchName = name || '';
    
    // BACKEND expects GET /patient/{id}/filter?condition=...&name=...&token=...
    const response = await fetch(`${PATIENT_API}/${id}/filter?condition=${searchCondition}&name=${searchName}&token=${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      const data = await response.json();
      return { ...data, success: true };
    } else {
      console.error("Failed to fetch appointments:", response.statusText);
      return { appointments: [], success: false, message: "Failed to filter appointments" };
    }
  } catch (error) {
    console.error("Error:", error);
    return { appointments: [], success: false, message: "Error filtering appointments. Please try again." };
  }
}

