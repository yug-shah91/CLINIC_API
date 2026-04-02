package com.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String notes;

    private List<MedicineRequest> medicines;

    @Data
    public static class MedicineRequest {
        @NotBlank(message = "Medicine name is required")
        private String name;

        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;
    }
}
