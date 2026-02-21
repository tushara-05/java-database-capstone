// patientServices.js
import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + "/patient";

/**
 * Register a new patient
 * @param {Object} data - Patient details (name, email, password, etc.)
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (!response.ok) throw new Error(result.message);

    return { success: response.ok, message: result.message };
  } catch (error) {
    console.error("Error :: patientSignup :: ", error);
    return { success: false, message: error.message };
  }
}

/**
 * Patient login
 * @param {Object} data - {email, password}
 * @returns {Promise<Response>} - Full fetch response
 */
export async function patientLogin(data) {
  console.log("patientLogin :: ", data);
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

/**
 * Fetch logged-in patient details
 * @param {string} token - Auth token
 * @returns {Promise<Object|null>} - Patient object or null on failure
 */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();
    return response.ok ? data.patient : null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Fetch patient appointments
 * Works for both patient and doctor views
 * @param {string} id - Patient ID
 * @param {string} token - Auth token
 * @param {string} user - Role: "patient" or "doctor"
 * @returns {Promise<Array|null>} - List of appointments or null
 */
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();
    return response.ok ? data.appointments : null;
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return null;
  }
}

/**
 * Filter patient appointments by condition and/or name
 * @param {string} condition - e.g., "pending", "consulted"
 * @param {string} name - Patient name filter
 * @param {string} token - Auth token
 * @returns {Promise<{appointments: Array}>} - Filtered appointments
 */
export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(
      `${PATIENT_API}/filter/${condition}/${encodeURIComponent(name)}/${token}`,
      { method: "GET", headers: { "Content-Type": "application/json" } }
    );

    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      console.error("Failed to fetch appointments:", response.statusText);
      return { appointments: [] };
    }
  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Something went wrong!");
    return { appointments: [] };
  }
}
