package com.example.cropadvisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CloseTicketRequest(
        @NotNull(message = "Officer is required") Long officerId,
        @NotBlank(message = "Recommendation is required") String recommendation
) {}
