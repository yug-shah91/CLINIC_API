package com.clinic.repository;

import com.clinic.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByAppointmentId(Long appointmentId);
    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT pr FROM Prescription pr " +
           "WHERE pr.appointment.patient.id = :patientId " +
           "ORDER BY pr.createdAt DESC")
    List<Prescription> findAllByPatientId(@Param("patientId") Long patientId);
}
