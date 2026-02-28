Patient Controller
This controller handles the operations related to the Patient entity. It allows patient registration, login, fetching patient details, getting and filtering patient appointments.

Open the PatientController.java file.
Open PatientController.java in IDE

Methods and Endpoints
Set Up the Controller Class

Annotate the class with @RestController to designate it as a REST controller
Use @RequestMapping("/patient") to set the base URL path for all methods in this controller
Autowired Dependencies

Autowire PatientService to handle business logic related to patient operations
Autowire Service to handle token validation and other common functionality
Method Definitions
1. Get Patient Details
Method: @GetMapping("/{token}")
Parameters
token: The authentication token for the patient.
Process: Validates the token using service.validateToken(). If the token is valid, fetches the patient details using patientService.getPatientDetails().
Response
If token is invalid: Returns an error message with appropriate HTTP status.
If successful: Returns the patient's details.
2. Create a New Patient
Method: @PostMapping()
Parameters
patient: The patient details to be created
Process
Validates if the patient already exists by checking email or phone number
If the validation passes, calls patientService.createPatient() to create a new patient record
Response
Success: "Signup successful"
Conflict: "Patient with email id or phone no already exist"
Internal error: "Internal server error"
3. Patient Login
Method: @PostMapping("/login")
Parameters
login: The login credentials (email, password)
Process: Calls service.validatePatientLogin() to validate the patient's login credentials
Response: Returns the result of the login validation (success or failure)
4. Get Patient Appointments
Method: @GetMapping("/{id}/{token}")
Parameters
id: The ID of the patient
token: The authentication token for the patient
Process
Validates the token using service.validateToken()
If valid, fetches the patient's appointments using patientService.getPatientAppointment()
Response
Returns the list of patient appointments or an error message
5. Filter Patient Appointments
Method: @GetMapping("/filter/{condition}/{name}/{token}")
Parameters
condition: The condition to filter appointments (for example, "upcoming", "past")
name: The name or description for filtering (for example, doctor name, appointment type)
token: The authentication token for the patient
Process
Validates the token using service.validateToken()
If valid, calls service.filterPatient() to filter the patient's appointments based on the given criteria
Response
Returns the filtered appointments or an error message

package com.project.back_end.controllers;

public class PatientController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.



}


