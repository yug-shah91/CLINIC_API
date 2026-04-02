package com.clinic.dto.response;

import com.clinic.entity.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String reason;
    private String patientName;
    private Long patientId;
    private LocalDateTime bookedAt;
}
