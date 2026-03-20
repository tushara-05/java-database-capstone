package com.project.back_end.repo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.models.Appointment;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("patientName") String patientName, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);
    // Date-based past/future filtering
    List<Appointment> findByPatient_IdAndAppointmentTimeBeforeOrderByAppointmentTimeDesc(Long patientId, LocalDateTime cutoff);
    List<Appointment> findByPatient_IdAndAppointmentTimeAfterOrderByAppointmentTimeAsc(Long patientId, LocalDateTime cutoff);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(@Param("doctorName") String doctorName, @Param("patientId") Long patientId);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(@Param("doctorName") String doctorName, @Param("patientId") Long patientId, @Param("status") int status);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.appointmentTime < :cutoff " +
           "ORDER BY a.appointmentTime DESC")
    List<Appointment> filterByDoctorNameAndPatientIdAndTimeBefore(@Param("doctorName") String doctorName, @Param("patientId") Long patientId, @Param("cutoff") LocalDateTime cutoff);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.appointmentTime >= :cutoff " +
           "ORDER BY a.appointmentTime ASC")
    List<Appointment> filterByDoctorNameAndPatientIdAndTimeAfter(@Param("doctorName") String doctorName, @Param("patientId") Long patientId, @Param("cutoff") LocalDateTime cutoff);
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, Long id);
}
