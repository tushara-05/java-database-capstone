// doctorServices.js
import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Fetch all doctors from the API
 * @returns {Promise<Array>} List of doctor objects
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) throw new Error("Failed to fetch doctors");
    const data = await response.json();
    return data.doctors || [];
  } catch (err) {
    console.error("getDoctors error:", err);
    return [];
  }
}

/**
 * Delete a doctor by ID
 * @param {string} id - Doctor ID
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: "DELETE",
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor deletion completed",
    };
  } catch (err) {
    console.error("deleteDoctor error:", err);
    return { success: false, message: "Failed to delete doctor" };
  }
}

/**
 * Save (add) a new doctor
 * @param {Object} doctor - Doctor data object
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor added successfully",
    };
  } catch (err) {
    console.error("saveDoctor error:", err);
    return { success: false, message: "Failed to add doctor" };
  }
}

/**
 * Filter doctors based on name, time, and specialty
 * @param {string} name - Doctor name to search (optional)
 * @param {string} time - Availability time (AM/PM)
 * @param {string} specialty - Doctor specialty
 * @returns {Promise<{doctors: Array}>}
 */
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    const url = `${DOCTOR_API}/filter/${encodeURIComponent(name)}/${encodeURIComponent(time)}/${encodeURIComponent(specialty)}`;
    const response = await fetch(url);
    if (!response.ok) {
      console.error("filterDoctors fetch failed:", response.status);
      return { doctors: [] };
    }
    const data = await response.json();
    return { doctors: data.doctors || [] };
  } catch (err) {
    console.error("filterDoctors error:", err);
    alert("Failed to filter doctors. Please try again.");
    return { doctors: [] };
  }
}
