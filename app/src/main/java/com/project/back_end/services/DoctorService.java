package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

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

    // -------------------- Doctor Availability --------------------
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> allSlots = new ArrayList<>();
        for (int hour = 8; hour <= 17; hour++) {
            allSlots.add(String.format("%02d:00", hour));
        }

        LocalDateTime start = date.atTime(8, 0);
        LocalDateTime end = date.atTime(17, 0);

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<String> bookedSlots = appointments.stream()
                .map(a -> String.format("%02d:00", a.getAppointmentTime().getHour()))
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // -------------------- Save Doctor --------------------
    @Transactional
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            return 0; // doctor already exists
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // error saving doctor
        }
    }

    // -------------------- Update Doctor --------------------
    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (doctorRepository.findById(doctor.getId()).isEmpty()) {
            return 0; // doctor not found
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // error updating
        }
    }

    // -------------------- Get All Doctors --------------------
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // -------------------- Delete Doctor --------------------
    @Transactional
    public int deleteDoctor(long id) {
        if (doctorRepository.findById(id).isEmpty()) return 0;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // -------------------- Validate Doctor Login --------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // -------------------- Filter Doctors By Name, Specialty, and Time --------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        filtered = filterDoctorByTime(filtered, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        result.put("count", filtered.size());
        return result;
    }

    // -------------------- Filter Doctors By Availability (AM/PM) --------------------
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(d -> {
            List<String> slots = getDoctorAvailability(d.getId(), LocalDate.now());
            if ("AM".equalsIgnoreCase(amOrPm)) {
                return slots.stream().anyMatch(s -> Integer.parseInt(s.split(":")[0]) < 12);
            } else if ("PM".equalsIgnoreCase(amOrPm)) {
                return slots.stream().anyMatch(s -> Integer.parseInt(s.split(":")[0]) >= 12);
            }
            return true;
        }).collect(Collectors.toList());
    }

    // -------------------- Find Doctor By Name --------------------
    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }
}
