package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * General-purpose service class that provides shared business logic
 * used across multiple controllers.
 *
 * <p>This class acts as a utility hub that combines functionality from
 * different services and repositories to handle cross-cutting concerns like:</p>
 * <ul>
 *   <li>Token validation for all user roles (admin, doctor, patient)</li>
 *   <li>Login validation for admins and patients</li>
 *   <li>Doctor filtering by name, speciality, and available time</li>
 *   <li>Appointment validation (checking if a time slot is available)</li>
 *   <li>Patient filtering with access control</li>
 * </ul>
 *
 * <p>The {@code @Service} annotation marks this class as a Spring-managed service bean.</p>
 */
@Service
public class ServiceClass {

    /** Service for generating and validating JWT tokens. */
    private final TokenService tokenService;

    /** Repository for looking up admin records. */
    private final AdminRepository adminRepository;

    /** Repository for looking up doctor records. */
    private final DoctorRepository doctorRepository;

    /** Repository for looking up patient records. */
    private final PatientRepository patientRepository;

    /** Service for doctor-related business logic and filtering. */
    private final DoctorService doctorService;

    /** Service for patient-related business logic and appointment filtering. */
    private final PatientService patientService;

    /**
     * Constructor that injects all required dependencies.
     *
     * <p>Spring automatically provides all required objects via constructor injection,
     * which is the recommended approach as it promotes immutability and testability.</p>
     *
     * @param tokenService      the service for token operations
     * @param adminRepository   the repository for admin data
     * @param doctorRepository  the repository for doctor data
     * @param patientRepository the repository for patient data
     * @param doctorService     the service for doctor-related logic
     * @param patientService    the service for patient-related logic
     */
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

    /**
     * Validates whether a JWT token is valid and belongs to the specified user role.
     *
     * <p>Delegates validation to {@link TokenService#validateToken(String, String)}.
     * If the token is invalid or expired, returns a 401 Unauthorized response.
     * This method is used by all controllers to protect secured endpoints.</p>
     *
     * @param token the JWT token string to validate
     * @param user  the expected role of the token owner: "admin", "doctor", or "patient"
     * @return a {@link ResponseEntity} with HTTP 200 and "Token valid" message if valid,
     *         HTTP 401 if the token is invalid or expired,
     *         or HTTP 500 if an unexpected error occurs
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = tokenService.validateToken(token, user);
            if (!isValid) {
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(401).body(response);
            }
            response.put("message", "Token valid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Validates admin login credentials and returns a JWT token on success.
     *
     * <p>Looks up the admin by their username. If found, compares the provided password
     * to the stored one. If they match, a JWT token is generated using the admin's username
     * and returned in the response. If not, appropriate error responses are returned.</p>
     *
     * @param receivedAdmin an {@link Admin} object containing the username and password from the login form
     * @return a {@link ResponseEntity} with a JWT token on successful login,
     *         HTTP 401 with an error message if credentials are wrong,
     *         or HTTP 500 if an unexpected error occurs
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            if (receivedAdmin == null || receivedAdmin.getUsername() == null) {
                response.put("error", "Invalid request");
                return ResponseEntity.badRequest().body(response);
            }
            
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                response.put("error", "Admin not found");
                return ResponseEntity.status(401).body(response);
            }
            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("error", "Incorrect password");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Filters doctors based on any combination of name, speciality, and time availability.
     *
     * <p>Delegates to the appropriate method in {@link DoctorService} depending on
     * which combination of filters is provided. Supports all 7 possible filter combinations:</p>
     * <ul>
     *   <li>All three: name + speciality + time</li>
     *   <li>Two: name + speciality, name + time, speciality + time</li>
     *   <li>One: name only, speciality only, time only</li>
     *   <li>None: returns all doctors</li>
     * </ul>
     *
     * <p>Values of {@code "all"} or {@code "null"} are treated as empty (no filter).</p>
     *
     * @param name      the doctor's name to search for (partial, case-insensitive); use "all" or "" for no filter
     * @param speciality the doctor's speciality to filter by; use "all" or "" for no filter
     * @param time      the time filter ("AM", "PM", or a range like "09:00-12:00"); use "all" or "" for no filter
     * @return a map with a "doctors" list and a "count" value
     */
    public Map<String, Object> filterDoctor(String name, String speciality, String time) {
        if ("all".equalsIgnoreCase(name) || "null".equals(name)) name = "";
        if ("all".equalsIgnoreCase(speciality) || "null".equals(speciality)) speciality = "";
        if ("all".equalsIgnoreCase(time) || "null".equals(time)) time = "";
        if (name != null && !name.isEmpty() && speciality != null && !speciality.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByNameSpecialityandTime(name, speciality, time);
        } else if (name != null && !name.isEmpty() && speciality != null && !speciality.isEmpty()) {
            return doctorService.filterDoctorByNameAndSpeciality(name, speciality);
        } else if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (speciality != null && !speciality.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByTimeAndSpeciality(speciality, time);
        } else if (name != null && !name.isEmpty()) {
            return doctorService.findDoctorByName(name);
        } else if (speciality != null && !speciality.isEmpty()) {
            return doctorService.filterDoctorBySpeciality(speciality);
        } else if (time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            List<Doctor> doctors = doctorService.getDoctors();
            Map<String, Object> result = new HashMap<>();
            result.put("doctors", doctors);
            result.put("count", doctors.size());
            return result;
        }
    }

    /**
     * Validates whether the requested appointment time is available for the doctor.
     *
     * <p>This method performs the following checks in order:</p>
     * <ol>
     *   <li>Returns {@code -1} if the doctor does not exist in the database.</li>
     *   <li>Returns {@code -2} if the appointment time is in the past.</li>
     *   <li>Returns {@code 0} if the doctor does not have the requested time slot available.</li>
     *   <li>Returns {@code 1} if the time slot is valid and available.</li>
     * </ol>
     *
     * @param appointment the appointment to validate (must include a doctor and appointment time)
     * @return {@code 1} if the appointment is valid and the slot is available,
     *         {@code 0} if the slot is unavailable or already booked,
     *         {@code -1} if the doctor does not exist,
     *         {@code -2} if the appointment time is in the past
     */
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || !doctorRepository.findById(appointment.getDoctor().getId()).isPresent()) {
            return -1; // doctor doesn't exist
        }
        if (appointment.getAppointmentTime() != null && appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            return -2; // Appointment date cannot be in the past
        }
        
        Long doctorId = appointment.getDoctor().getId();
        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, appointment.getAppointmentTime().toLocalDate());
        
        java.time.LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
        
        // Use a consistent comparison by parsing available slots into LocalTime objects
        boolean isAvailable = availableSlots.stream()
            .map(slot -> {
                try {
                    // Extract start time if slot is a range (e.g., "09:00-10:00")
                    String timeStr = slot.contains("-") ? slot.split("-")[0].trim() : slot;
                    // Try parsing with leading zero (HH:mm) first, then without (H:mm)
                    try {
                        return java.time.LocalTime.parse(timeStr);
                    } catch (Exception e) {
                        return java.time.LocalTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("H:mm"));
                    }
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(java.util.Objects::nonNull)
            .anyMatch(t -> t.equals(appointmentTime));
        
        return isAvailable ? 1 : 0;
    }

    /**
     * Checks whether a new patient can be registered (i.e., no duplicate exists).
     *
     * <p>Looks up the database for any existing patient with the same email or phone number.
     * If a match is found, the patient is considered a duplicate and registration is rejected.</p>
     *
     * @param patient the patient attempting to register
     * @return {@code true} if the patient does not already exist (safe to register),
     *         {@code false} if a patient with the same email or phone already exists
     */
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null; // true if patient does not exist (meaning valid to create)
    }

    /**
     * Validates patient login credentials and returns a JWT token on success.
     *
     * <p>Looks up the patient by their email address. If found, checks whether the
     * provided password matches. On success, generates and returns a JWT token.
     * On failure, returns a 401 Unauthorized response with an appropriate error message.</p>
     *
     * @param login a {@link Login} DTO containing the patient's email (identifier) and password
     * @return a {@link ResponseEntity} with a JWT token on successful login,
     *         HTTP 401 with an error message if credentials are wrong,
     *         or HTTP 500 if an unexpected error occurs
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            if (login == null || login.getIdentifier() == null) {
                response.put("error", "Invalid request");
                return ResponseEntity.badRequest().body(response);
            }
            
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(401).body(response);
            }
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("error", "Incorrect password");
                return ResponseEntity.status(401).body(response);
            }
            
            // Generate token with email
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Filters patient appointments based on doctor name and time condition, with access control.
     *
     * <p>This method enforces role-based access control:</p>
     * <ul>
     *   <li>If the token belongs to a <b>patient</b>, they can only access their OWN records
     *       (i.e., the token owner's ID must match the given {@code id}).</li>
     *   <li>If the token belongs to a <b>doctor</b>, they can access any patient's records.</li>
     *   <li>Anyone else gets a 401 Unauthorized response.</li>
     * </ul>
     *
     * <p>Filtering combinations supported:</p>
     * <ul>
     *   <li>Both condition and name → filters by doctor name AND time condition</li>
     *   <li>Only condition → filters by time condition only</li>
     *   <li>Only name → filters by doctor name only</li>
     *   <li>Neither → returns all appointments</li>
     * </ul>
     *
     * @param id        the ID of the patient whose appointments to retrieve
     * @param condition the time filter ("past", "future", "all", etc.); use null or "all" for no filter
     * @param name      the doctor name to filter by; use null or "all" for no filter
     * @param token     the JWT token of the requesting user
     * @return a {@link ResponseEntity} with the filtered list of appointments and count,
     *         or an error response if access is denied or an error occurs
     */
    public ResponseEntity<Map<String, Object>> filterPatient(Long id, String condition, String name, String token) {
        if ("all".equalsIgnoreCase(condition) || "null".equals(condition)) condition = "";
        if ("all".equalsIgnoreCase(name) || "null".equals(name)) name = "";
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient tokenOwner = patientRepository.findByEmail(email);
            boolean isDoctor = (doctorRepository.findByEmail(email) != null);
            // Access Control
            if (tokenOwner != null) {
                // If the user is a patient, they must be the owner of the record they are trying to access.
                if (!tokenOwner.getId().equals(id)) {
                    response.put("error", "Unauthorized access to patient records");
                    return ResponseEntity.status(403).body(response);
                }
            } else if (!isDoctor) {
                // Not a patient and not a doctor? Unauthorized.
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            // Perform filtering using the provided patient id (not just the token owner's id)
            if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
                return patientService.filterByDoctorAndCondition(condition, name, id);
            } else if (condition != null && !condition.isEmpty()) {
                return patientService.filterByCondition(condition, id);
            } else if (name != null && !name.isEmpty()) {
                return patientService.filterByDoctor(name, id);
            } else {
                return patientService.getPatientAppointment(id, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }
}
