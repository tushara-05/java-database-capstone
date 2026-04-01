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

/**
 * REST Controller that handles HTTP requests related to patient operations.
 *
 * <p>This controller provides endpoints for patient registration, login,
 * viewing patient profile, retrieving appointment history, and filtering appointments.</p>
 *
 * <p>All routes are prefixed with the configured API path followed by {@code /patient}.</p>
 */
@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    /** Service for handling patient-specific business logic. */
    private final PatientService patientService;

    /** Shared service for token validation and patient login authentication. */
    private final ServiceClass service;

    /**
     * Constructor that injects the required service dependencies.
     *
     * @param patientService the service for patient operations
     * @param service        the shared service for validation logic
     */
    @Autowired
    public PatientController(PatientService patientService, ServiceClass service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Returns the profile details of the logged-in patient.
     *
     * <p>Endpoint: {@code GET /patient/{token}}</p>
     *
     * <p>The patient is identified from their JWT token. The token is validated
     * before fetching the patient's information.</p>
     *
     * @param token the JWT token of the logged-in patient (as a URL path variable)
     * @return a {@link ResponseEntity} with the patient's profile data,
     *         or an error response if the token is invalid
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return patientService.getPatientDetails(token);
    }

    /**
     * Registers a new patient in the system.
     *
     * <p>Endpoint: {@code POST /patient}</p>
     *
     * <p>This is an open endpoint — no token is required. Before saving, checks whether
     * a patient with the same email or phone number already exists.
     * If so, returns HTTP 409 (Conflict).</p>
     *
     * @param patient the patient details to register (from the request body);
     *                the {@code @Valid} annotation triggers validation constraints
     *                defined in the {@link Patient} model
     * @return a {@link ResponseEntity} with HTTP 201 and success message if registered,
     *         HTTP 409 if a patient with the same email or phone already exists,
     *         HTTP 500 if an internal error occurred
     */
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

    /**
     * Handles patient login and returns a JWT token on success.
     *
     * <p>Endpoint: {@code POST /patient/login}</p>
     *
     * <p>Accepts a {@link Login} DTO with the patient's email (as {@code identifier}) and password.
     * Delegates authentication to the shared service.</p>
     *
     * @param login the login credentials (email and password)
     * @return a {@link ResponseEntity} with a JWT token on successful login,
     *         or an error message if the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    /**
     * Returns all appointments for a specific patient.
     *
     * <p>Endpoint: {@code GET /patient/{id}/{token}}</p>
     *
     * <p>The requesting user can be either the patient (who can only see their own appointments)
     * or a doctor (who can view any patient's appointments). The token is validated for both roles.</p>
     *
     * @param id    the ID of the patient whose appointments to retrieve (as a URL path variable)
     * @param token the JWT token of the requesting user — either a patient or a doctor
     * @return a {@link ResponseEntity} with the list of appointments and count,
     *         or an error response if the token is invalid or access is denied
     */
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

    /**
     * Filters a patient's appointments based on optional condition and doctor name criteria.
     *
     * <p>Endpoint: {@code GET /patient/{id}/filter?condition=...&name=...&token=...}</p>
     *
     * <p>The requesting user can be either the patient (who can only filter their own appointments)
     * or a doctor (who can filter any patient's appointments).</p>
     *
     * <p>Supported filter combinations:</p>
     * <ul>
     *   <li>Both {@code condition} and {@code name} → filter by doctor name AND time condition</li>
     *   <li>Only {@code condition} → filter by time condition (past, future, all)</li>
     *   <li>Only {@code name} → filter by doctor name only</li>
     *   <li>Neither → returns all appointments for the patient</li>
     * </ul>
     *
     * @param id        the ID of the patient whose appointments to filter (path variable)
     * @param condition (optional) time filter: "past", "future", "upcoming", "scheduled", or "all"
     * @param name      (optional) a partial doctor name to filter by
     * @param token     the JWT token of the requesting user (patient or doctor)
     * @return a {@link ResponseEntity} with the filtered appointments and count,
     *         or an error response if the token is invalid or access is denied
     */
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
