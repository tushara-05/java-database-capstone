package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.
@Service
public class DoctorService {

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        List<String> allSlots;
        
        if (doctorOpt.isPresent() && doctorOpt.get().getAvailableTimes() != null 
            && !doctorOpt.get().getAvailableTimes().isEmpty()) {
            allSlots = new ArrayList<>(doctorOpt.get().getAvailableTimes());
        } else {
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            allSlots = new ArrayList<>();
            while (!startTime.isAfter(endTime.minusHours(1))) {
                allSlots.add(startTime.toString());
                startTime = startTime.plusHours(1);
            }
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(23, 59, 59);

        List<Appointment> booked = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, dayStart, dayEnd);

        Set<String> bookedSlots = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        allSlots.removeAll(bookedSlots);
        return allSlots;
    }

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return 0; // conflict: doctor with this email already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // internal error
        }
    }

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (!existing.isPresent()) return 0; // not found
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // internal error
        }
    }

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (!doctorOpt.isPresent()) return 0;  // not found
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // internal error
        }
    }

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        if (login == null || login.getIdentifier() == null) {
            response.put("error", "Invalid request");
            return ResponseEntity.badRequest().body(response);
        }
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialityAndTime(String name, String speciality, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
    /**
     * Parses a time string tolerantly, handling both "HH:mm" and "H:mm" formats.
     * Returns null if the string cannot be parsed.
     */
    private LocalTime parseTime(String timeStr) {
        if (timeStr != null && timeStr.contains("-")) {
            timeStr = timeStr.split("-")[0].trim();
        }
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            try {
                return LocalTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("H:mm"));
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isEmpty()) return doctors;

        return doctors.stream().filter(d -> {
            List<String> times = d.getAvailableTimes();

            // If no availableTimes stored, use the same default 9:00–17:00 slots
            // that getDoctorAvailability() generates, so filter results stay consistent
            if (times == null || times.isEmpty()) {
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(17, 0);
                times = new ArrayList<>();
                while (!startTime.isAfter(endTime.minusHours(1))) {
                    times.add(startTime.toString());
                    startTime = startTime.plusHours(1);
                }
            }

            for (String timeStr : times) {
                LocalTime t = parseTime(timeStr);
                if (t == null) continue; // skip unparseable slot
                if ("AM".equalsIgnoreCase(amOrPm) && t.isBefore(LocalTime.NOON)) return true;
                if ("PM".equalsIgnoreCase(amOrPm) && !t.isBefore(LocalTime.NOON)) return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpeciality(String name, String speciality) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpeciality(String speciality, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
    @Transactional
    public Map<String, Object> filterDoctorBySpeciality(String speciality) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }
}
