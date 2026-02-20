package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.ServiceClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final ServiceClass service;

    public PatientController(PatientService patientService, ServiceClass service) {
        this.patientService = patientService;
        this.service = service;
    }

    // -------------------- 1. Get Patient Details --------------------
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            Patient patient = patientService.getPatientDetails(token);
            return ResponseEntity.ok(Map.of("patient", patient));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // -------------------- 2. Create a New Patient --------------------
    @PostMapping()
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {

        boolean valid = service.validatePatient(patient);

        if (!valid) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "Patient with email or phone already exists"));
        }

        boolean created = patientService.createPatient(patient);

        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    // -------------------- 3. Patient Login --------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // -------------------- 4. Get Patient Appointments --------------------
    @GetMapping("/appointments/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            List<AppointmentDTO> appointments =
                    patientService.getPatientAppointments(id);

            return ResponseEntity.ok(Map.of("appointments", appointments));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // -------------------- 5. Filter Patient Appointments --------------------
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        // IMPORTANT: just return service response directly
        return service.filterPatient(condition, name, token);
    }
}
