package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.back_end.models.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find doctor by email
    Doctor findByEmail(String email);

    // Find doctors by partial name match
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    // Find doctors by name (partial) and speciality (case-insensitive)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.speciality) = LOWER(:speciality)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(@Param("name") String name, @Param("speciality") String speciality);

    // Find doctors by speciality ignoring case
    List<Doctor> findBySpecialityIgnoreCase(String speciality);

}
