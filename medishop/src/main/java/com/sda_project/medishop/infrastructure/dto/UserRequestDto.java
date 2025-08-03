package com.sda_project.medishop.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9\\-+()\\s]*$", message = "Invalid contact number format")
    private String contactNumber;
}

