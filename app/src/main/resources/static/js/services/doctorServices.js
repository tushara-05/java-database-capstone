doctorServices.js
This service module is responsible for handling all API interactions related to doctor data. Instead of writing fetch() calls directly inside your dashboard pages, we organize them here for modularity, reusability, and cleaner code separation.

Open the doctorServices.js file app/src/main/resources/static/js/services/doctorServices.js

Open doctorServices.js in IDE

Import API Base URL
Start by importing the API base URL from your configuration file ../config/config.js.

import { API_BASE_URL } from "../config/config.js";

This ensures you’re not hardcoding URLs in multiple files — you centralize it in one place

Set Doctor API Endpoint
Define a constant for the doctor-related base endpoint.

const DOCTOR_API = API_BASE_URL + '/doctor'

Create a Function to Get All Doctors
Build a function getDoctors() that:

Sends a GET request to the doctor endpoint.
Awaits a response from the server.
Extracts and returns the list of doctors from the response JSON.
Handles any errors using a try-catch block.
Returns an empty list ([]) if something goes wrong to avoid breaking the frontend.
This is the function your Admin dashboard and Patient dashboard will use to load all doctors and display them on the page.

Create a Function to Delete a Doctor
Build a function deleteDoctor(id, token) that:

Takes the doctor’s unique id and an authentication token (for security).
Constructs the full endpoint URL using the ID and token.
Sends a DELETE request to that endpoint.
Parses the JSON response and returns a success status and message.
Catches and handles any errors to prevent frontend crashes.
This allows an authenticated Admin to remove doctors from the system securely.

Create a Function to Save (Add) a New Doctor
This function saveDoctor(doctor , token) should:

Accept a doctor object containing all doctor details (like name, email, availability).
Also take in a token for Admin authentication.
Send a POST request with headers specifying JSON data.
Include the doctor data in the request body (converted to JSON).
Return a structured response with success and message.
Catch and log any errors to help during debugging.
It powers the “Add Doctor” modal in the Admin dashboard and saves new doctor records in the database.

Create a Function to Filter Doctors
This function filterDoctors(name ,time ,specialty):

Accepts parameters like name, time, and specialty.
Constructs a GET request URL by passing these values as route parameters.
Sends a GET request to retrieve matching doctor records.
Returns the filtered list of doctors (or an empty list if none are found).
Handles cases where no filters are applied (pass null or empty strings appropriately).
Uses error handling to alert the user if something fails.
This supports real-time search and filter features in the Admin dashboard.

Task
Always use async/await for fetch calls for better readability.
Use try-catch to gracefully handle network or server errors.
Never hardcode API URLs; instead, use a base config file.
Return consistent data formats (e.g., { success, message }) to simplify frontend logic.
Keep your service layer focused on communication logic, not UI logic.
By organizing all your doctor-related API functions in doctorServices.js, you’re:

Making your code more modular and maintainable
Avoiding repetition of fetch logic
Separating responsibilities between UI and backend logic
Preparing your app to scale with ease
/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
