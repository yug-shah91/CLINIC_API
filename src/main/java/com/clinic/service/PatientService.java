package com.clinic.service;

import com.clinic.dto.request.PatientRequest;
import com.clinic.dto.response.PatientResponse;
import com.clinic.entity.Patient;
import com.clinic.entity.User;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientResponse createProfile(String email, PatientRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        if (patientRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Patient profile already exists");
        }
        Patient patient = Patient.builder()
                .name(request.getName() != null ? request.getName() : user.getName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .gender(request.getGender())
                .address(request.getAddress())
                .bloodGroup(request.getBloodGroup())
                .user(user)
                .build();
        return toResponse(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse getMyProfile(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        return toResponse(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PatientResponse updateProfile(String email, PatientRequest request) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        if (request.getName() != null) patient.setName(request.getName());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getBloodGroup() != null) patient.setBloodGroup(request.getBloodGroup());
        return toResponse(patientRepository.save(patient));
    }

    private PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .bloodGroup(patient.getBloodGroup())
                .email(patient.getUser().getEmail())
                .registeredAt(patient.getRegisteredAt())
                .build();
    }
}
