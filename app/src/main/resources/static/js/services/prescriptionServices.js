// prescriptionServices.js
import { API_BASE_URL } from '../config/config.js'

const PRESCRIPTION_API = API_BASE_URL + "/prescription"

/**
 * Save a prescription to the database
 * @param {Object} prescription - The prescription object
 * @param {string} token - Authentication token
 * @returns {Object} Response with success status and message
 */
export async function savePrescription(prescription, token) {
  try {
    const response = await fetch(`${PRESCRIPTION_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(prescription)
    });
    const result = await response.json();
    return { success: response.ok, message: result.message };
  }
  catch (error) {
    console.error("Error :: savePrescription :: ", error);
    return { success: false, message: error.message };
  }
}

/**
 * Get prescription by appointment ID
 * @param {number} appointmentId - The appointment ID
 * @param {string} token - Authentication token
 * @returns {Object} Response containing prescription object
 */
export async function getPrescription(appointmentId, token) {
  try {
    const response = await fetch(`${PRESCRIPTION_API}/${appointmentId}/${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Unable to fetch prescription");
    }

    const result = await response.json();
    return result; // Returns { prescription: {...} } - single object
  } catch (error) {
    if (error.message && error.message.includes("not found")) {
      console.log("No existing prescription.");
    } else {
      console.error("Error :: getPrescription ::", error);
    }
    throw error;
  }
}
