Prescription Controller
This controller is responsible for handling operations related to prescriptions in the system. It allows doctors to save prescriptions and retrieve prescriptions based on the appointment ID.

Open the PrescriptionController.java file.
Open PrescriptionController.java in IDE

Methods and Endpoints
Set Up the Controller Class

Annotate the class with @RestController to designate it as a REST controller
Use @RequestMapping("${api.path}" + "prescription") to set the base URL path for all methods in this controller
Autowired Dependencies

Autowire PrescriptionService to handle the business logic of managing prescriptions
Autowire Service to handle common functionality, like token validation
Method Definitions
1. Save Prescription
Method: @PostMapping("/{token}")
Parameters
token: The authentication token for the doctor
prescription: The prescription details to be saved (passed in the request body)
Process
The token is validated to ensure that the request is made by a doctor using service.validateToken()
If the token is valid, the prescription is saved using prescriptionService.savePrescription()
Response
If the token is invalid: Returns an error message with appropriate HTTP status
If the prescription is successfully saved: Returns a success message
2. Get Prescription by Appointment ID
Method: @GetMapping("/{appointmentId}/{token}")
Parameters
appointmentId: The ID of the appointment to retrieve the prescription for
token: The authentication token for the doctor
Process
The token is validated using service.validateToken() to ensure the request is from a valid doctor
If the token is valid, the prescription is retrieved for the given appointmentId using prescriptionService.getPrescription()
Response
If the token is invalid: Returns an error message with appropriate HTTP status
If the prescription is found, returns the prescription details
If no prescription is found, returns a message indicating no prescription exists for that appointment
Explanation of the ValidationFailed Class
The ValidationFailed class is a custom exception handler that handles validation errors in a Spring Boot application. It is annotated with @RestControllerAdvice, which makes it a global exception handler for REST controllers.

This class handles the MethodArgumentNotValidException which occurs when a validation fails during the binding of request parameters or request body fields to the method parameters in a controller. Typically, this happens when input data does not meet the constraints defined by annotations such as @NotNull, @Size, @Email, and so on, in the model class.

Key Points
@RestControllerAdvice

This annotation is a combination of @ControllerAdvice and @ResponseBody, which makes it a global exception handler that can return the response directly as JSON (or any other format) in case of errors.
Exception Handler Method

The @ExceptionHandler(MethodArgumentNotValidException.class) annotation specifies that this method will handle exceptions of type MethodArgumentNotValidException. This exception is thrown when a validation error occurs on the request body (such as when data in a @RequestBody doesn't match the required constraints).
Handling Validation Errors

The method handleValidationException is invoked when a MethodArgumentNotValidException is thrown.
Inside the method, the exception object (ex) provides access to the binding result of the validation errors, which includes the field errors (for example, which fields failed validation).
Creating the Error Response

The FieldError object contains information about the specific field that failed validation and the corresponding error message.
We loop through all the field errors in the exception and map them to a Map<String, String>, where the key is "message" and the value is the actual error message associated with the field.
Returning the Response

After processing all validation errors, the method returns a ResponseEntity with an HTTP status of BAD_REQUEST (400) and a body containing the validation error messages.


package com.project.back_end.controllers;

public class PrescriptionController {
    
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.


// 2. Autowire Dependencies:
//    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
//    - Inject the shared `Service` class for token validation and role-based access control.
//    - Inject `AppointmentService` to update appointment status after a prescription is issued.


// 3. Define the `savePrescription` Method:
//    - Handles HTTP POST requests to save a new prescription for a given appointment.
//    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
//    - Validates the token for the `"doctor"` role.
//    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
//    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.


// 4. Define the `getPrescription` Method:
//    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
//    - Accepts the appointment ID and a doctor’s token as path variables.
//    - Validates the token for the `"doctor"` role using the shared service.
//    - If the token is valid, fetches the prescription using the `PrescriptionService`.
//    - Returns the prescription details or an appropriate error message if validation fails.


}
