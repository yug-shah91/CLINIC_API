package com.clinic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;
    private String reason;
}
