package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceClass {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public ServiceClass(TokenService tokenService,
                        AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        DoctorService doctorService,
                        PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // -------------------- Validate Token --------------------
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean valid = tokenService.validateToken(token, user);

            if (!valid) {
                response.put("message", "Token is invalid or expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error validating token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- Validate Admin Login --------------------
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error validating admin");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- Filter Doctors --------------------
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        try {
            return doctorService.filterDoctorsByNameSpecialtyAndTime(name, specialty, time);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Error filtering doctors");
            return result;
        }
    }

    // -------------------- Validate Appointment --------------------
    public int validateAppointment(Appointment appointment) {
        try {
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);

            if (doctor == null) return -1;

            List<String> availableSlots = doctorService.getDoctorAvailability(
                    doctor.getId(),
                    appointment.getAppointmentTime().toLocalDate()
            );

            String slot = String.format("%02d:00", appointment.getAppointmentTime().getHour());

            return availableSlots.contains(slot) ? 1 : 0;

        } catch (Exception e) {
            return 0;
        }
    }

    // -------------------- Validate Patient Registration --------------------
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository
                .findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    // -------------------- Validate Patient Login --------------------
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());

            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error validating patient login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- Filter Patient Appointments --------------------
    public ResponseEntity<Map<String, Object>> filterPatient(String condition,
                                                             String name,
                                                             String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<AppointmentDTO> appointments;

            if (condition != null && !condition.isEmpty()
                    && name != null && !name.isEmpty()) {

                appointments = patientService
                        .filterAppointmentsByDoctorAndCondition(condition, name, patient.getId());

            } else if (condition != null && !condition.isEmpty()) {

                appointments = patientService
                        .filterAppointmentsByCondition(condition, patient.getId());

            } else if (name != null && !name.isEmpty()) {

                appointments = patientService
                        .filterAppointmentsByDoctor(name, patient.getId());

            } else {

                appointments = patientService
                        .getPatientAppointments(patient.getId());
            }

            response.put("appointments", appointments);
            response.put("count", appointments.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
