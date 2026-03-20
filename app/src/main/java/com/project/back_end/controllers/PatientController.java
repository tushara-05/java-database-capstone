package com.project.back_end.controllers;

import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.ServiceClass;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.
@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    // 2. Autowire Dependencies:
    //    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
    //    - Inject the shared `Service` class for tasks like token validation and login authentication.
    private final PatientService patientService;
    private final ServiceClass service;

    @Autowired
    public PatientController(PatientService patientService, ServiceClass service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Define the `getPatient` Method:
    //    - Handles HTTP GET requests to retrieve patient details using a token.
    //    - Validates the token for the `"patient"` role using the shared service.
    //    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return patientService.getPatientDetails(token);
    }

    // 4. Define the `createPatient` Method:
    //    - Handles HTTP POST requests for patient registration.
    //    - Accepts a validated `Patient` object in the request body.
    //    - First checks if the patient already exists using the shared service.
    //    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            return ResponseEntity.status(409).body(Map.of("error", "Patient with email id or phone no already exist"));
        }
        int created = patientService.createPatient(patient);
        if (created == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Signup successful"));
        }
        return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }

    // 5. Define the `login` Method:
    //    - Handles HTTP POST requests for patient login.
    //    - Accepts a `Login` DTO containing email/username and password.
    //    - Delegates authentication to the `validatePatientLogin` method in the shared service.
    //    - Returns a response with a token or an error message depending on login success.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 6. Define the `getPatientAppointment` Method:
    //    - Handles HTTP GET requests to fetch appointment details for a specific patient.
    //    - Requires the patient ID, token, and user role as path variables.
    //    - Validates the token using the shared service.
    //    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long id, 
            @PathVariable String token) {
            
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            tokenValidation = service.validateToken(token, "doctor");
            if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
                return tokenValidation;
            }
        }
        return patientService.getPatientAppointment(id, token);
    }

    // 7. Define the `filterPatientAppointment` Method:
    //    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
    //    - Accepts filtering parameters: `id` (path variable), `condition` (query param), `name` (query param), and `token` (query param).
    //    - Token must be valid for a `"patient"` or `"doctor"` role.
    //    - If valid, delegates filtering logic to the shared service and returns the filtered result.
    @GetMapping("/{id}/filter")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String name,
            @RequestParam String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            tokenValidation = service.validateToken(token, "doctor");
            if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
                return tokenValidation;
            }
        }
        return service.filterPatient(id, condition, name, token);
    }
}
