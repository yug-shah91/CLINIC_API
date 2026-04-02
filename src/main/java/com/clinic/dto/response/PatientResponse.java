package com.clinic.dto.response;

import com.clinic.entity.enums.BloodGroup;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponse {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String phone;
    private String gender;
    private String address;
    private BloodGroup bloodGroup;
    private String email;           // from linked User
    private LocalDateTime registeredAt;
}
