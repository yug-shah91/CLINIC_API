package com.clinic.service;

import com.clinic.dto.request.PrescriptionRequest;
import com.clinic.dto.response.PrescriptionResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.Medicine;
import com.clinic.entity.Patient;
import com.clinic.entity.Prescription;
import com.clinic.entity.enums.AppointmentStatus;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PrescriptionResponse writePrescription(PrescriptionRequest request) {

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Appointment not found: " + request.getAppointmentId()));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException(
                "Prescription can only be written for COMPLETED appointments. " +
                "Current status: " + appointment.getStatus());
        }

        if (prescriptionRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new RuntimeException("A prescription already exists for this appointment.");
        }

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .build();

        if (request.getMedicines() != null) {
            List<Medicine> medicines = request.getMedicines().stream()
                    .map(m -> Medicine.builder()
                            .name(m.getName())
                            .dosage(m.getDosage())
                            .frequency(m.getFrequency())
                            .durationDays(m.getDurationDays())
                            .instructions(m.getInstructions())
                            .prescription(prescription) // set back-reference
                            .build())
                    .collect(Collectors.toList());
            prescription.setMedicines(medicines);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByAppointment(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No prescription found for appointment: " + appointmentId));
        return toResponse(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPatientHistory(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        return prescriptionRepository.findAllByPatientId(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getMyPrescription(Long appointmentId, String email) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        String prescriptionOwnerEmail = prescription.getAppointment().getPatient().getUser().getEmail();
        if (!prescriptionOwnerEmail.equals(email)) {
            throw new RuntimeException("Access denied: This prescription does not belong to you.");
        }

        return toResponse(prescription);
    }

    private PrescriptionResponse toResponse(Prescription p) {
        List<PrescriptionResponse.MedicineResponse> medicines =
                p.getMedicines() == null ? Collections.emptyList() :
                p.getMedicines().stream()
                        .map(m -> PrescriptionResponse.MedicineResponse.builder()
                                .id(m.getId())
                                .name(m.getName())
                                .dosage(m.getDosage())
                                .frequency(m.getFrequency())
                                .durationDays(m.getDurationDays())
                                .instructions(m.getInstructions())
                                .build())
                        .collect(Collectors.toList());

        return PrescriptionResponse.builder()
                .id(p.getId())
                .appointmentId(p.getAppointment().getId())
                .patientName(p.getAppointment().getPatient().getName())
                .diagnosis(p.getDiagnosis())
                .notes(p.getNotes())
                .medicines(medicines)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
