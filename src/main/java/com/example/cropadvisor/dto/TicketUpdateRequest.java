package com.example.cropadvisor.dto;

import jakarta.validation.constraints.NotBlank;

public record TicketUpdateRequest(

        @NotBlank(message = "Crop is required")
        String crop,

        @NotBlank(message = "Symptoms are required")
        String symptoms,

        String photoReference

) {
}