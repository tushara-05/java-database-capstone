package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.ServiceClass;

/**
 * REST Controller that handles HTTP requests related to doctor operations.
 *
 * <p>This controller exposes endpoints for managing doctor data including
 * registration, login, updating, deleting, availability lookup, and filtering.
 * Admin-level operations (add, update, delete) require a valid admin token.
 * Doctor login uses the doctor's own credentials.</p>
 *
 * <p>All routes are prefixed with the configured API path followed by {@code /doctor}.</p>
 */
@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    /** Service for handling doctor-specific business logic. */
    private final DoctorService doctorService;

    /** Shared service for token validation and filtering logic. */
    private final ServiceClass service;

    /**
     * Constructor that injects the required service dependencies.
     *
     * @param doctorService the service for doctor operations
     * @param service       the shared service for validation and filtering
     */
    @Autowired
    public DoctorController(DoctorService doctorService, ServiceClass service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * Returns the available appointment time slots for a specific doctor on a given date.
     *
     * <p>Endpoint: {@code GET /doctor/availability/{user}/{doctorId}/{date}/{token}}</p>
     *
     * <p>The requesting user can be either a doctor or a patient (specified by the {@code user} path variable).
     * The token is validated for the specified user role before returning availability data.</p>
     *
     * @param user     the role of the requesting user ("doctor" or "patient")
     * @param doctorId the ID of the doctor whose availability to check
     * @param date     the date to check availability for (format: YYYY-MM-DD)
     * @param token    the JWT token of the requesting user
     * @return a {@link ResponseEntity} with a list of available time slots,
     *         or an error response if the token is invalid
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", validation.getBody().get("error")));
        }
        LocalDate appointmentDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        return ResponseEntity.ok(Map.of("availability", availability));
    }

    /**
     * Returns a list of all doctors registered in the system.
     *
     * <p>Endpoint: {@code GET /doctor}</p>
     *
     * <p>This is a public endpoint — no token is required to view the list of doctors.</p>
     *
     * @return a {@link ResponseEntity} with a map containing the "doctors" list
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    /**
     * Registers a new doctor in the system.
     *
     * <p>Endpoint: {@code POST /doctor/{token}}</p>
     *
     * <p>Only accessible by authenticated admins (admin token required).
     * If a doctor with the same email already exists, a conflict (HTTP 409) is returned.</p>
     *
     * @param doctor the doctor details to register (from the request body)
     * @param token  the admin's JWT token (as a URL path variable)
     * @return a {@link ResponseEntity} with a success message if registered (HTTP 200),
     *         HTTP 409 if the doctor already exists,
     *         HTTP 500 if an internal error occurred
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", validation.getBody().get("error")));
        }
        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor added to db"));
        } else if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("error", "Doctor already exists"));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * Handles doctor login and returns a JWT token on success.
     *
     * <p>Endpoint: {@code POST /doctor/login}</p>
     *
     * <p>Accepts a {@link Login} DTO containing the doctor's email and password.
     * Delegates validation to the {@link DoctorService}.</p>
     *
     * @param login the login credentials (email as identifier, password)
     * @return a {@link ResponseEntity} with a JWT token on successful login,
     *         or an error message if the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * Updates an existing doctor's information.
     *
     * <p>Endpoint: {@code PUT /doctor/{token}}</p>
     *
     * <p>Only accessible by authenticated admins (admin token required).
     * The doctor to update is identified by the ID within the request body.</p>
     *
     * @param doctor the doctor object with updated information (must include the doctor's ID)
     * @param token  the admin's JWT token (as a URL path variable)
     * @return a {@link ResponseEntity} with a success message if updated (HTTP 200),
     *         HTTP 404 if the doctor does not exist,
     *         HTTP 500 if an internal error occurred
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", validation.getBody().get("error")));
        }
        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("error", "Doctor not found"));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * Deletes a doctor and all their associated appointments.
     *
     * <p>Endpoint: {@code DELETE /doctor/{id}/{token}}</p>
     *
     * <p>Only accessible by authenticated admins (admin token required).
     * Removing a doctor also removes all appointments linked to that doctor
     * to prevent orphan records in the database.</p>
     *
     * @param id    the ID of the doctor to delete (as a URL path variable)
     * @param token the admin's JWT token (as a URL path variable)
     * @return a {@link ResponseEntity} with a success message if deleted (HTTP 200),
     *         HTTP 404 if the doctor does not exist,
     *         HTTP 500 if an internal error occurred
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", validation.getBody().get("error")));
        }
        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("error", "Doctor not found with id"));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * Filters and returns a list of doctors based on name, time availability, and speciality.
     *
     * <p>Endpoint: {@code GET /doctor/filter/{name}/{time}/{speciality}}</p>
     *
     * <p>Any combination of these three filters can be used. Use "all" or "null" to skip a filter.
     * This is a public endpoint — no token is required.</p>
     *
     * @param name      the doctor's name to search for (partial, case-insensitive); use "all" to skip
     * @param time      the time filter: "AM", "PM", or a range (e.g., "09:00-12:00"); use "all" to skip
     * @param speciality the medical speciality to filter by; use "all" to skip
     * @return a {@link ResponseEntity} with the matching "doctors" list and "count"
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {
        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }
}
