Doctor Controller
This controller handles all operations related to the Doctor entity. It allows adding, updating, deleting, fetching, and filtering doctors. It also manages login functionality for doctors and validates their tokens.

Open the DoctorController.java file.
Open DoctorController.java in IDE

Steps and Endpoints
Set Up the Controller Class

Annotate the class with @RestController to designate it as a REST controller.
Use @RequestMapping("${api.path}" + "doctor") to set the base URL path for all methods in this controller.
Autowired Dependencies

Autowire the DoctorService for handling business logic related to doctor operations.
Autowire the Service class for handling token validation and filtering operations.
Method Definitions
1. Get Doctor Availability
Method: @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
Parameters
user: Role of the user (doctor, patient, admin, and so on)
doctorId: The unique ID of the doctor
date: The date for which the availability needs to be fetched
token: The authentication token for validating the user
Process: Validates the token using service.validateToken() and fetches the doctor's availability using doctorService.getDoctorAvailability()
Response: Returns a map with the doctor's availability or an error message
2. Get List of Doctors
Method: @GetMapping
Process: Fetches a list of all doctors from the doctorService.getDoctors() method
Response: Returns a list of doctors in the response map
3. Add New Doctor
Method: @PostMapping("/{token}")
Parameters
doctor: The doctor details to be added
token: The authentication token for validation
Process: Validates the token with service.validateToken()
If the token is valid (admin), attempts to save the doctor using doctorService.saveDoctor()
Returns success or error messages based on the result of the save operation
Response
Success: "Doctor added to db"
Conflict: "Doctor already exists"
Internal error: "Some internal error occurred"
4. Doctor Login
Method: @PostMapping("/login")
Parameters
login: The login details (email, password)
Process: Calls doctorService.validateDoctor() to validate the doctor's credentials and returns the corresponding response
Response: Returns the result of the login validation
5. Update Doctor Details
Method: @PutMapping("/{token}")
Parameters
doctor: The doctor object with updated details
token: The authentication token for validation
Process: Validates the token using service.validateToken().
If the token is valid, attempts to update the doctor using doctorService.updateDoctor()
Returns success or error messages based on the result of the update operation
Response
Success: "Doctor updated"
Not found: "Doctor not found"
Internal error: "Some internal error occurred"
6. Delete Doctor
Method: @DeleteMapping("/{id}/{token}")
Parameters
id: The ID of the doctor to be deleted
token: The authentication token for validation
Process: Validates the token using service.validateToken()
If valid, attempts to delete the doctor using doctorService.deleteDoctor()
Returns success or error messages based on the result of the delete operation
Response
Success: "Doctor deleted successfully"
Not found: "Doctor not found with id"
Internal error: "Some internal error occurred"
7. Filter Doctors
Method: @GetMapping("/filter/{name}/{time}/{speciality}")
Parameters
name: The name of the doctor (can be partial)
time: The available time for filtering
speciality: The specialty of the doctor
Process: Uses service.filterDoctor() to filter doctors based on the given parameters (name, time, and specialty)
Response: Returns a map of filtered doctor data


package com.project.back_end.controllers;


public class DoctorController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
