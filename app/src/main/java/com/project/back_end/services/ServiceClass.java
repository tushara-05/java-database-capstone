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
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.
@Service
public class ServiceClass {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    // 2. **Constructor Injection for Dependencies**
    // The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
    // and ensures that all required dependencies are provided at object creation time.
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
    // 3. **validateToken Method**
    // This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
    // If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
    // unauthorized access to protected resources.
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
    // 4. **validateAdmin Method**
    // This method validates the login credentials for an admin user.
    // - It first searches the admin repository using the provided username.
    // - If an admin is found, it checks if the password matches.
    // - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
    // - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
    // - If no admin is found, it also returns a 401 Unauthorized.
    // - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
    // This method ensures that only valid admin users can access secured parts of the system.
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
    // 5. **filterDoctor Method**
    // This method provides filtering functionality for doctors based on name, speciality, and available time slots.
    // - It supports various combinations of the three filters.
    // - If none of the filters are provided, it returns all available doctors.
    // This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
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
    // 6. **validateAppointment Method**
    // This method validates if the requested appointment time for a doctor is available.
    // - It first checks if the doctor exists in the repository.
    // - Then, it retrieves the list of available time slots for the doctor on the specified date.
    // - It compares the requested appointment time with the start times of these slots.
    // - If a match is found, it returns 1 (valid appointment time).
    // - If no matching time slot is found, it returns 0 (invalid).
    // - If the doctor doesn’t exist, it returns -1.
    // This logic prevents overlapping or invalid appointment bookings.
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || !doctorRepository.findById(appointment.getDoctor().getId()).isPresent()) {
            return -1; // doctor doesn't exist
        }
        if (appointment.getAppointmentTime() != null && appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            return 0; // time is unavailable (past date)
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
    // 7. **validatePatient Method**
    // This method checks whether a patient with the same email or phone number already exists in the system.
    // - If a match is found, it returns false (indicating the patient is not valid for new registration).
    // - If no match is found, it returns true.
    // This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null; // true if patient does not exist (meaning valid to create)
    }
    // 8. **validatePatientLogin Method**
    // This method handles login validation for patient users.
    // - It looks up the patient by email.
    // - If found, it checks whether the provided password matches the stored one.
    // - On successful validation, it generates a JWT token and returns it with a 200 OK status.
    // - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
    // - If an exception occurs, it returns a 500 Internal Server Error.
    // This method ensures only legitimate patients can log in and access their data securely.
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
    // 9. **filterPatient Method**
    // This method handles filtering of patient appointments based on the provided condition and doctor's name.
    // - Authorization: If the token belongs to a patient, they can only view THEIR OWN records (id must match).
    // - Doctors: If the token belongs to a doctor, they are allowed to view any patient's filtered appointments.
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
