package com.clinic.dto.request;

import com.clinic.entity.enums.BloodGroup;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    @Size(min = 2, max = 100)
    private String name;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    private String gender;
    private String address;
    private BloodGroup bloodGroup;
}
