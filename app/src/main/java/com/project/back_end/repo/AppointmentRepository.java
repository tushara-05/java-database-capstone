package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.models.Appointment;

/**
 * Repository interface for performing database operations on {@link Appointment} entities.
 *
 * <p>This interface extends {@link JpaRepository}, which provides basic CRUD methods automatically.
 * Additional custom queries are defined here to support filtering appointments by doctor,
 * patient, date range, and status.</p>
 *
 * <p>Uses JPQL (Java Persistence Query Language) with LEFT JOIN FETCH to avoid lazy-loading issues
 * by eagerly loading the associated doctor and patient data in a single query.</p>
 *
 * <p>Appointment data is stored in the MySQL (relational) database.</p>
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Finds all appointments for a specific doctor within a given date-time range.
     *
     * <p>Fetches the associated doctor and patient in the same query to avoid extra database calls.
     * Used to load a doctor's schedule for a specific day.</p>
     *
     * @param doctorId the ID of the doctor
     * @param start    the start of the time range (usually the beginning of the day)
     * @param end      the end of the time range (usually the end of the day)
     * @return a list of appointments for the doctor within the given range
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Finds all appointments for a specific doctor within a date-time range,
     * filtered by a partial patient name match (case-insensitive).
     *
     * <p>Useful when a doctor wants to search for a specific patient's appointments on a given day.</p>
     *
     * @param doctorId    the ID of the doctor
     * @param patientName a partial or full name of the patient to search (case-insensitive)
     * @param start       the start of the time range
     * @param end         the end of the time range
     * @return a list of matching appointments
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Finds all appointments for a specific doctor where the patient's name contains the given keyword,
     * regardless of date range. Results are sorted newest first.
     *
     * <p>Used when searching appointments by patient name without any date filter.</p>
     *
     * @param doctorId    the ID of the doctor
     * @param patientName a partial or full name of the patient (case-insensitive)
     * @return a list of matching appointments, ordered by appointment time descending
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "ORDER BY a.appointmentTime DESC")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCase(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName);

    /**
     * Deletes all appointments associated with a specific doctor.
     *
     * <p>Used when removing a doctor from the system to ensure all their appointments
     * are also removed, preventing orphan records.</p>
     *
     * <p>Annotated with {@code @Modifying} and {@code @Transactional} because this
     * operation modifies the database and must be executed within a transaction.</p>
     *
     * @param doctorId the ID of the doctor whose appointments should be deleted
     */
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    /**
     * Finds all past appointments for a specific patient (appointments whose time has already passed).
     * Results are sorted newest first (most recent past appointment at the top).
     *
     * @param patientId the ID of the patient
     * @return a list of past appointments for the patient, sorted by date descending
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE a.patient.id = :patientId AND a.appointmentTime < CURRENT_TIMESTAMP ORDER BY a.appointmentTime DESC")
    List<Appointment> findPastAppointmentsByPatientId(@Param("patientId") Long patientId);

    /**
     * Finds all upcoming appointments for a specific patient (appointments in the future).
     * Results are sorted earliest first.
     *
     * @param patientId the ID of the patient
     * @return a list of future appointments for the patient, sorted by date ascending
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE a.patient.id = :patientId AND a.appointmentTime >= CURRENT_TIMESTAMP ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatientId(@Param("patientId") Long patientId);

    /**
     * Finds all appointments (past and future) for a specific patient, sorted by date ascending.
     *
     * @param patientId the ID of the patient
     * @return a complete list of all appointments for the patient
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE a.patient.id = :patientId ORDER BY a.appointmentTime ASC")
    List<Appointment> findByPatient_Id(@Param("patientId") Long patientId);

    /**
     * Finds all appointments for a patient where the doctor's name contains the given keyword
     * (case-insensitive, across all dates).
     *
     * <p>Used to let a patient filter their appointments by the doctor's name.</p>
     *
     * @param doctorName the partial or full name of the doctor to search (case-insensitive)
     * @param patientId  the ID of the patient
     * @return a list of appointments matching the doctor name for the given patient
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId);

    /**
     * Finds past appointments for a patient where the doctor's name contains the given keyword.
     * Results are sorted newest first.
     *
     * @param doctorName the partial or full name of the doctor (case-insensitive)
     * @param patientId  the ID of the patient
     * @return a list of past appointments filtered by doctor name, sorted descending
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.appointmentTime < CURRENT_TIMESTAMP ORDER BY a.appointmentTime DESC")
    List<Appointment> filterPastByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId);

    /**
     * Finds upcoming appointments for a patient where the doctor's name contains the given keyword.
     * Results are sorted earliest first.
     *
     * @param doctorName the partial or full name of the doctor (case-insensitive)
     * @param patientId  the ID of the patient
     * @return a list of upcoming appointments filtered by doctor name, sorted ascending
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.appointmentTime >= CURRENT_TIMESTAMP ORDER BY a.appointmentTime ASC")
    List<Appointment> filterUpcomingByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId);

    /**
     * Updates the status of a specific appointment.
     *
     * <p>Uses a JPQL UPDATE query to directly change the status value in the database
     * without needing to load the full entity first.</p>
     *
     * <p>Annotated with {@code @Modifying} and {@code @Transactional} because this
     * is a database write operation.</p>
     *
     * @param id     the ID of the appointment to update
     * @param status the new status value (0 = Scheduled, 1 = Completed)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") int status);
}
