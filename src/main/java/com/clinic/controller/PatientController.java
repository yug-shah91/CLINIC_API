package com.clinic.controller;

import com.clinic.dto.request.PatientRequest;
import com.clinic.dto.response.PatientResponse;
import com.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/profile")
    public ResponseEntity<PatientResponse> createProfile(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createProfile(getLoggedInEmail(), request));
    }

    // View my profile
    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile() {
        return ResponseEntity.ok(patientService.getMyProfile(getLoggedInEmail()));
    }

    // Update my profile
    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateProfile(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updateProfile(getLoggedInEmail(), request));
    }

    // Get all patients (doctor uses this)
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // Get patient by ID (doctor uses this)
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getById(id));
    }
}
