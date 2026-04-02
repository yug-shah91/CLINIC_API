package com.clinic.service;

import com.clinic.dto.request.AppointmentRequest;
import com.clinic.dto.response.AppointmentResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.Patient;
import com.clinic.entity.enums.AppointmentStatus;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    // Book an appointment
    @Transactional
    public AppointmentResponse bookAppointment(String patientEmail, AppointmentRequest request) {

        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found. Please create your profile first."));

        LocalDateTime requestedTime = request.getAppointmentTime();

        if (requestedTime.getDayOfWeek().getValue() == 7) {
            throw new RuntimeException("Clinic is closed on Sundays.");
        }

        LocalTime time = requestedTime.toLocalTime();
        if (time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(16, 30))) {
            throw new RuntimeException("Appointments only available between 9:00 AM and 4:30 PM.");
        }

        boolean alreadyBooked = appointmentRepository
                .existsByAppointmentTimeAndStatus(requestedTime, AppointmentStatus.SCHEDULED);

        if (alreadyBooked) {
            throw new RuntimeException("This time slot is already booked. Please choose another time.");
        }

        // All checks passed — create the appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .appointmentTime(requestedTime)
                .reason(request.getReason())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    // Patient views their own appointments
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return appointmentRepository
                .findByPatientIdOrderByAppointmentTimeDesc(patient.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Admin views all appointments
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getTodayAppointments() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsBetween(start, end)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Only SCHEDULED appointments can be cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Only SCHEDULED appointments can be completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .reason(a.getReason())
                .patientName(a.getPatient().getName())
                .patientId(a.getPatient().getId())
                .bookedAt(a.getBookedAt())
                .build();
    }
}
