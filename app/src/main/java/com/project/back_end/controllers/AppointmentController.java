Appointment Controller
This controller handles all CRUD operations related to appointments. It provides endpoints for booking, retrieving, updating, and canceling appointments. It also performs validation on tokens and ensures proper actions are taken based on user roles.

Open the AppointmentController.java file.
Open AppointmentController.java in IDE

Set Up the Controller Class
Annotate the class with @RestController to designate it as a REST controller for handling HTTP requests.
Use @RequestMapping("/appointments") to set the base URL path for all methods in this controller.
Autowired Dependencies
Autowire the necessary services.
AppointmentService for handling the business logic related to appointments (booking, retrieving, updating, and canceling appointments)
Service for validation logic (token validation and appointment validation)
Define the getAppointments Method
Annotate this method with @GetMapping("/{date}/{patientName}/{token}").
It takes the date, patientName, and token as path variables.
It calls service.validateToken(token, "doctor") to validate the token, ensuring that only doctors can access appointment data.
If token validation fails, return an error response.
If token validation is successful, fetch the appointments using appointmentService.getAppointment() and return the appointments in the response.
Define the bookAppointment Method
Annotate this method with @PostMapping("/{token}").
It accepts an Appointment object in the request body.
It uses service.validateToken(token, "patient") to ensure that the request is from a valid patient.
It validates the appointment using service.validateAppointment().
If valid, it proceeds to book the appointment using appointmentService.bookAppointment().
If booking is successful, return a success message with HTTP status 201 Created.
If there is an error, return an appropriate error message and status.
Define the updateAppointment Method
Annotate this method with @PutMapping("/{token}").
It accepts the token as a path variable and the Appointment object in the request body.
It validates the token using service.validateToken(token, "patient").
If valid, it updates the appointment using appointmentService.updateAppointment() and returns the result.
Define the cancelAppointment Method
Annotate this method with @DeleteMapping("/{id}/{token}").
It accepts the id of the appointment and the token as path variables.
It validates the token using service.validateToken(token, "patient").
If valid, it cancels the appointment using appointmentService.cancelAppointment() and returns the result.

package com.project.back_end.controllers;


public class AppointmentController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.


}
