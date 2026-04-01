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

/**
 * Service class that handles all business logic related to doctors.
 *
 * <p>This class is responsible for managing doctor records, including registering,
 * updating, deleting doctors, and handling doctor login. It also provides filtering
 * and availability lookup features used by the frontend to search for doctors.</p>
 *
 * <p>The {@code @Service} annotation tells Spring to manage this class as a bean,
 * meaning it can be injected into controllers and other services automatically.</p>
 */
@Service
public class DoctorService {

    /** Repository for performing CRUD operations on doctor records. */
    private final DoctorRepository doctorRepository;

    /** Repository for querying appointment records. */
    private final AppointmentRepository appointmentRepository;

    /** Service for generating and validating JWT tokens. */
    private final TokenService tokenService;

    /**
     * Constructor that injects all required dependencies.
     *
     * @param doctorRepository      the repository for doctor data
     * @param appointmentRepository the repository for appointment data
     * @param tokenService          the service for token operations
     */
    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Returns the list of available appointment time slots for a doctor on a specific date.
     *
     * <p>If the doctor has defined their own available times, those are used.
     * Otherwise, a default schedule of 9:00 AM to 5:00 PM (hourly slots) is applied.</p>
     *
     * <p>Any slots that are already booked for the given date are removed from the list,
     * so only genuinely free slots are returned.</p>
     *
     * @param doctorId the ID of the doctor
     * @param date     the date for which to check availability
     * @return a list of available time slot strings (e.g., "09:00", "10:00")
     */
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

    /**
     * Saves a new doctor record to the database.
     *
     * <p>Before saving, checks if a doctor with the same email already exists.
     * If so, returns -1 to indicate a conflict. Returns 1 on success, 0 on error.</p>
     *
     * @param doctor the doctor to save
     * @return {@code 1} if saved successfully,
     *         {@code -1} if a doctor with that email already exists,
     *         {@code 0} if an internal error occurred
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // conflict: doctor with this email already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // internal error
        }
    }

    /**
     * Updates an existing doctor's information in the database.
     *
     * <p>If no doctor with the given ID exists, returns -1. Returns 1 on success,
     * 0 if an error occurred during the update.</p>
     *
     * @param doctor the doctor object with updated fields (must have a valid ID)
     * @return {@code 1} if updated successfully,
     *         {@code -1} if the doctor was not found,
     *         {@code 0} if an internal error occurred
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (!existing.isPresent()) return -1; // not found
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // internal error
        }
    }

    /**
     * Returns a list of all doctors in the system.
     *
     * <p>This method is marked as {@code @Transactional} to ensure that
     * eagerly loaded fields (like available times) are fully loaded within the
     * same database session.</p>
     *
     * @return a list of all {@link Doctor} objects
     */
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Deletes a doctor and all their associated appointments from the system.
     *
     * <p>First removes all appointments linked to the doctor (to avoid orphan records),
     * then deletes the doctor record itself. Returns -1 if not found, 1 on success,
     * 0 on error.</p>
     *
     * @param id the ID of the doctor to delete
     * @return {@code 1} if deleted successfully,
     *         {@code -1} if the doctor was not found,
     *         {@code 0} if an error occurred
     */
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (!doctorOpt.isPresent()) return -1;  // not found
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // internal error
        }
    }

    /**
     * Validates a doctor's login credentials and returns a JWT token if valid.
     *
     * <p>Looks up the doctor by the email in the {@link Login} object.
     * If the doctor exists and the password matches, a JWT token is generated
     * and returned. Otherwise, an error response is returned.</p>
     *
     * @param login a {@link Login} DTO containing the doctor's email (identifier) and password
     * @return a {@link ResponseEntity} with a token on success,
     *         or an error message if credentials are invalid
     */
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

    /**
     * Searches for doctors by a partial name match.
     *
     * <p>Returns all doctors whose name contains the given keyword
     * (case-sensitive partial match). Results include the total count.</p>
     *
     * @param name the keyword to search for in doctor names
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Filters doctors by name, speciality, and time availability (all three criteria combined).
     *
     * @param name      partial name of the doctor (case-insensitive)
     * @param speciality the speciality to filter by (case-insensitive)
     * @param amOrPm    time filter: "AM", "PM", or a specific range like "09:00-12:00"
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialityandTime(String name, String speciality, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Helper method that parses a time string into a {@link LocalTime} object.
     *
     * <p>Handles both "HH:mm" (e.g., "09:00") and "H:mm" (e.g., "9:00") formats.
     * Returns null if the string cannot be parsed, to avoid crashing on bad input.</p>
     *
     * @param timeStr the time string to parse
     * @return the parsed {@link LocalTime}, or {@code null} if parsing fails
     */
    private LocalTime parseTime(String timeStr) {
        if (timeStr == null) return null;
        timeStr = timeStr.trim();
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

    /**
     * Filters a list of doctors based on their available time slots and a given time filter.
     *
     * <p>The filter can be:</p>
     * <ul>
     *   <li>{@code "AM"} — keep doctors with at least one slot before noon</li>
     *   <li>{@code "PM"} — keep doctors with at least one slot from noon onwards</li>
     *   <li>A range like {@code "09:00-12:00"} — keep doctors with a slot within that range</li>
     *   <li>{@code "all"}, {@code null}, or empty — no filtering, return all doctors</li>
     * </ul>
     *
     * <p>If a doctor has no defined available times, a default 9:00–17:00 schedule is assumed.</p>
     *
     * @param doctors    the list of doctors to filter
     * @param timeFilter the time-of-day filter string
     * @return a filtered list of doctors matching the time criteria
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timeFilter) {
        if (timeFilter == null || timeFilter.isEmpty() || "all".equalsIgnoreCase(timeFilter)) return doctors;
        boolean isRange = timeFilter.contains("-");
        LocalTime rangeStart = null;
        LocalTime rangeEnd = null;
        if (isRange) {
            String[] parts = timeFilter.split("-");
            if (parts.length == 2) {
                rangeStart = parseTime(parts[0]);
                rangeEnd = parseTime(parts[1]);
            }
        }
        final LocalTime finalRangeStart = rangeStart;
        final LocalTime finalRangeEnd = rangeEnd;
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
                // Some slots might be range strings themselves like "09:00-10:00"
                // Extract the start time for comparison
                String slotStartTimeStr = timeStr;
                if (slotStartTimeStr.contains("-")) {
                    slotStartTimeStr = slotStartTimeStr.split("-")[0].trim();
                }
                
                LocalTime t = parseTime(slotStartTimeStr);
                if (t == null) continue; // skip unparseable slot
                if (isRange) {
                    if (finalRangeStart != null && finalRangeEnd != null) {
                        // Check if the slot falls within the requested range
                        if (!t.isBefore(finalRangeStart) && t.isBefore(finalRangeEnd)) return true;
                    }
                } else {
                    if ("AM".equalsIgnoreCase(timeFilter) && t.isBefore(LocalTime.NOON)) return true;
                    if ("PM".equalsIgnoreCase(timeFilter) && !t.isBefore(LocalTime.NOON)) return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * Filters doctors by partial name match and time availability.
     *
     * @param name   the partial name to search for (case-insensitive)
     * @param amOrPm the time filter ("AM", "PM", or a range)
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Filters doctors by partial name match and speciality.
     *
     * @param name      the partial name to search for (case-insensitive)
     * @param speciality the speciality to filter by (case-insensitive)
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpeciality(String name, String speciality) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Filters doctors by speciality and time availability.
     *
     * @param speciality the speciality to filter by (case-insensitive)
     * @param amOrPm     the time filter ("AM", "PM", or a range)
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpeciality(String speciality, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Filters doctors by speciality only.
     *
     * @param speciality the speciality to match (case-insensitive exact match)
     * @return a map with a "doctors" list and a "count" value
     */
    @Transactional
    public Map<String, Object> filterDoctorBySpeciality(String speciality) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

    /**
     * Filters all doctors by time availability only.
     *
     * <p>Loads all doctors from the database and applies the time filter to return
     * only those who have available slots matching the given time period.</p>
     *
     * @param amOrPm the time filter ("AM", "PM", or a specific time range)
     * @return a map with a "doctors" list and a "count" value
     */
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
