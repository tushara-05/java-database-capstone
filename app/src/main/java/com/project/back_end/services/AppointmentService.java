package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // -------------------- Book Appointment --------------------
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // -------------------- Update Appointment --------------------
    @Transactional
    public int updateAppointment(Appointment appointment) {

        Optional<Appointment> existingOpt =
                appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) return 0;

        Appointment existing = existingOpt.get();

        Long doctorId = appointment.getDoctor().getId();
        LocalDateTime start = appointment.getAppointmentTime();
        LocalDateTime end = start.plusHours(1);

        List<Appointment> conflicts =
                appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        conflicts.removeIf(a -> a.getId().equals(appointment.getId()));

        if (!conflicts.isEmpty()) return -1;

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(appointment.getDoctor());
        existing.setPatient(appointment.getPatient());

        appointmentRepository.save(existing);
        return 1;
    }

    // -------------------- Cancel Appointment --------------------
    @Transactional
    public int cancelAppointment(Long id) {

        Optional<Appointment> existingOpt =
                appointmentRepository.findById(id);

        if (existingOpt.isEmpty()) return 0;

        appointmentRepository.delete(existingOpt.get());
        return 1;
    }

    // -------------------- Get Appointments for Doctor --------------------
    @Transactional(readOnly = true)
    public List<Appointment> getAppointment(String patientName,
                                            LocalDate date,
                                            Long doctorId) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        if (patientName == null || patientName.isEmpty()) {
            return appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            return appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, patientName, start, end);
        }
    }

    // -------------------- Change Status (FIXED) --------------------
    @Transactional
    public boolean changeStatus(Long appointmentId, int status) {
        try {
            Optional<Appointment> appointmentOpt =
                    appointmentRepository.findById(appointmentId);

            if (appointmentOpt.isEmpty()) return false;

            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status);

            appointmentRepository.save(appointment);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
