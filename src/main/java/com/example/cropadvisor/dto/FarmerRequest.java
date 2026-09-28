package com.example.cropadvisor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FarmerRequest(
        @NotBlank(message = "Farmer name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email,
        @NotBlank(message = "Phone number is required") String phone,
        @NotNull(message = "Region is required") Long regionId
) {}
