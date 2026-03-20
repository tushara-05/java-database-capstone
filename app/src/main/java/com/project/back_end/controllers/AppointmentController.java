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

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.
@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    // 2. Autowire Dependencies:
    //    - Inject `AppointmentService` for handling the business logic specific to appointments.
    //    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.
    private final AppointmentService appointmentService;
    private final ServiceClass service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, ServiceClass service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Define the `getAppointments` Method:
    //    - Handles HTTP GET requests to fetch appointments based on date and patient name.
    //    - Accepts optional filtering parameters: `date`, `patientName`, and `token` as request parameters.
    //    - First validates the token for role `"doctor"` using the `Service`.
    //    - If the token is valid, returns appointments for the given patient on the specified date.
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAppointments(
            @RequestParam String date,
            @RequestParam(required = false) String patientName,
            @RequestParam String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized or invalid token"));
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        
        // Handle "" or "all" patient name requests gracefully depending on front-end
        if (patientName == null || "all".equalsIgnoreCase(patientName) || "null".equals(patientName)) {
            patientName = "";
        }
        
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, appointmentDate, token);
        return ResponseEntity.ok(appointments);
    }

    // 4. Define the `bookAppointment` Method:
    //    - Handles HTTP POST requests to create a new appointment.
    //    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
    //    - Validates the token for the `"patient"` role.
    //    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
    //    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
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

    // 5. Define the `updateAppointment` Method:
    //    - Handles HTTP PUT requests to modify an existing appointment.
    //    - Accepts a validated `Appointment` object and a token as input.
    //    - Validates the token for `"patient"` role.
    //    - Delegates the update logic to the `AppointmentService`.
    //    - Returns an appropriate success or failure response based on the update result.
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

    // 6. Define the `cancelAppointment` Method:
    //    - Handles HTTP DELETE requests to cancel a specific appointment.
    //    - Accepts the appointment ID and a token as path variables.
    //    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
    //    - Calls `AppointmentService` to handle the cancellation process and returns the result.
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

