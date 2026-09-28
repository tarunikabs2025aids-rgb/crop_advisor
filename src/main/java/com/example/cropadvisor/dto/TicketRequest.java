package com.example.cropadvisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @NotNull(message = "Farmer is required") Long farmerId,
        @NotBlank(message = "Crop is required") String crop,
        @NotBlank(message = "Symptoms are required") String symptoms,
        String photoReference
) {}
