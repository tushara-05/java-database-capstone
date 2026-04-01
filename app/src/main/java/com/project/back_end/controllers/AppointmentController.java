package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.ServiceClass;

/**
 * REST Controller that handles HTTP requests related to appointment operations.
 *
 * <p>This controller exposes endpoints for booking, updating, retrieving, and canceling
 * appointments. It validates user tokens before processing any request to ensure
 * only authorized users can perform each operation.</p>
 *
 * <p>All routes in this controller are prefixed with the configured API path
 * followed by {@code /appointments}.</p>
 */
@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    /** Service for handling appointment-specific business logic. */
    private final AppointmentService appointmentService;

    /** Shared service for token validation and appointment data validation. */
    private final ServiceClass service;

    /**
     * Constructor that injects the required service dependencies.
     *
     * @param appointmentService the service for appointment operations
     * @param service            the shared service for validation logic
     */
    @Autowired
    public AppointmentController(AppointmentService appointmentService, ServiceClass service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Retrieves appointments for a doctor on a specific date, optionally filtered by patient name.
     *
     * <p>Endpoint: {@code GET /appointments?date=...&patientName=...&token=...}</p>
     *
     * <p>Only accessible by authenticated doctors (validated via the token).
     * The doctor is identified from the token itself — no doctor ID needs to be passed.</p>
     *
     * <p>If {@code patientName} is provided, results are filtered by patient name regardless of date.
     * If {@code date} is "today" or empty, today's date is used. Otherwise, the given date is parsed.</p>
     *
     * @param date        the date to fetch appointments for (format: YYYY-MM-DD, or "today")
     * @param patientName (optional) a partial patient name to filter appointments by
     * @param token       the JWT token of the logged-in doctor
     * @return a {@link ResponseEntity} with a list of appointments and count,
     *         or an error response if the token is invalid or the date format is wrong
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAppointments(
            @RequestParam String date,
            @RequestParam(required = false) String patientName,
            @RequestParam String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized or invalid token"));
        }

        LocalDate appointmentDate;
        try {
            if (date == null || date.isEmpty() || "today".equalsIgnoreCase(date)) {
                appointmentDate = LocalDate.now();
            } else {
                appointmentDate = LocalDate.parse(date);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format. Use YYYY-MM-DD or 'today'"));
        }
        
        // Handle "" or "all" patient name requests gracefully depending on front-end
        if (patientName == null || "all".equalsIgnoreCase(patientName) || "null".equals(patientName)) {
            patientName = "";
        }
        
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, appointmentDate, token);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Books a new appointment for a patient.
     *
     * <p>Endpoint: {@code POST /appointments/{token}}</p>
     *
     * <p>Only accessible by authenticated patients (token must belong to a patient).
     * Before saving, validates that:</p>
     * <ul>
     *   <li>The doctor exists in the system</li>
     *   <li>The appointment time is not in the past</li>
     *   <li>The requested time slot is still available for the doctor</li>
     * </ul>
     *
     * @param appointment the appointment details in the request body (must pass validation constraints)
     * @param token       the JWT token of the logged-in patient (as a URL path variable)
     * @return a {@link ResponseEntity} with HTTP 201 and a success message if booked,
     *         or an error response if validation fails or the slot is unavailable
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized or invalid token"));
        }

        int result = service.validateAppointment(appointment);
        if (result == -1) {
            return ResponseEntity.badRequest().body(Map.of("error", "Doctor does not exist"));
        } else if (result == -2) {
            return ResponseEntity.badRequest().body(Map.of("error", "Appointment date cannot be in the past"));
        } else if (result == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Appointment time unavailable"));
        }

        int bookingResult = appointmentService.bookAppointment(appointment);
        if (bookingResult == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to book appointment"));
        }
    }

    /**
     * Updates an existing appointment's details.
     *
     * <p>Endpoint: {@code PUT /appointments/{token}}</p>
     *
     * <p>Only accessible by authenticated patients (token must belong to a patient).
     * The patient can update the appointed doctor or appointment time, provided the
     * new time is available. The appointment must belong to the requesting patient.</p>
     *
     * @param appointment the updated appointment details (must include the appointment ID)
     * @param token       the JWT token of the logged-in patient (as a URL path variable)
     * @return a {@link ResponseEntity} with a success message if updated,
     *         or an error response if the token is invalid or validation fails
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized or invalid token"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * Cancels (deletes) an appointment.
     *
     * <p>Endpoint: {@code DELETE /appointments/{id}/{token}}</p>
     *
     * <p>Only accessible by authenticated patients (token must belong to a patient).
     * The patient can only cancel their OWN appointments — an ownership check is performed
     * inside the service before deletion.</p>
     *
     * @param id    the ID of the appointment to cancel (as a URL path variable)
     * @param token the JWT token of the logged-in patient (as a URL path variable)
     * @return a {@link ResponseEntity} with a success message if canceled,
     *         or an error response if the token is invalid, the appointment is not found,
     *         or the patient does not own that appointment
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized or invalid token"));
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
