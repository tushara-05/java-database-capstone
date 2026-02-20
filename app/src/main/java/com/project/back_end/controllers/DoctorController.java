package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.ServiceClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final ServiceClass service;

    public DoctorController(DoctorService doctorService, ServiceClass service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        LocalDate appointmentDate = LocalDate.parse(date); // Convert date string to LocalDate
        List<String> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        return ResponseEntity.ok(Map.of("availability", availability));
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor);
        switch (result) {
            case 1:
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Doctor added to db"));
            case 0:
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Doctor already exists"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.updateDoctor(doctor);
        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor updated"));
            case 0:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Doctor not found"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.deleteDoctor(id);
        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
            case 0:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Doctor not found with id"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialty) { 

        Map<String, Object> filteredDoctors = service.filterDoctor(name, specialty, time);
        return ResponseEntity.ok(filteredDoctors);
    }
}
