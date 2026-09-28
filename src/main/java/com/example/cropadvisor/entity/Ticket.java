package com.example.cropadvisor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_officer_id", nullable = false)
    private Officer assignedOfficer;

    @NotBlank(message = "Crop is required")
    @Column(nullable = false)
    private String crop;

    @NotBlank(message = "Symptoms are required")
    @Column(nullable = false, length = 2000)
    private String symptoms;

    private String photoReference;

    @Column(nullable = false)
    private String status;

    @Column(length = 3000)
    private String recommendation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime timerStartedAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    private LocalDateTime reopenedAt;

    @Column(nullable = false)
    private Boolean escalated = false;
}
