package com.clinic.repository;

import com.clinic.entity.Appointment;
import com.clinic.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);

    boolean existsByAppointmentTimeAndStatus(LocalDateTime appointmentTime, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime BETWEEN :start AND :end ORDER BY a.appointmentTime ASC")
    List<Appointment> findAppointmentsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
