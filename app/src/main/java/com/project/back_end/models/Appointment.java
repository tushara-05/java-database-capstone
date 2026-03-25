package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Doctor cannot be null")
    @JsonIgnoreProperties({"availableTimes", "password", "email", "phone"})
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "Patient cannot be null")
    @JsonIgnoreProperties({"password", "address"})
    private Patient patient;

    @NotNull(message = "Appointment time cannot be null")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Status cannot be null")
    private int status; // 0 = Scheduled, 1 = Completed

    // No-argument constructor required by JPA
    public Appointment() {
    }

    // Optional constructor
    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Helper Methods

    @Transient
    public LocalDateTime getEndTime() {
        if (appointmentTime == null) return null;
        return appointmentTime.plusHours(1);
    }

    @Transient
    public LocalDate getAppointmentDate() {
        if (appointmentTime == null) return null;
        return appointmentTime.toLocalDate();
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        if (appointmentTime == null) return null;
        return appointmentTime.toLocalTime();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
