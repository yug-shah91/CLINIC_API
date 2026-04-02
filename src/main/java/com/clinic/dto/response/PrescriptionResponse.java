package com.clinic.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String diagnosis;
    private String notes;
    private List<MedicineResponse> medicines;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class MedicineResponse {
        private Long id;
        private String name;
        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;
    }
}
