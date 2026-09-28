package com.example.cropadvisor.service;

import com.example.cropadvisor.dto.CloseTicketRequest;
import com.example.cropadvisor.dto.TicketRequest;
import com.example.cropadvisor.dto.TicketUpdateRequest;
import com.example.cropadvisor.entity.Farmer;
import com.example.cropadvisor.entity.Officer;
import com.example.cropadvisor.entity.Ticket;
import com.example.cropadvisor.repository.FarmerRepository;
import com.example.cropadvisor.repository.OfficerRepository;
import com.example.cropadvisor.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private static final String OPEN = "OPEN";
    private static final String CLOSED = "CLOSED";
    private static final long ESCALATION_HOURS = 48;

    private final TicketRepository ticketRepository;
    private final FarmerRepository farmerRepository;
    private final OfficerRepository officerRepository;

    @Transactional
    public Ticket createTicket(TicketRequest request) {

        Farmer farmer = farmerRepository.findById(request.farmerId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Farmer not found with id: " + request.farmerId()
                        ));

        List<Officer> officers = officerRepository
                .findByRegionIdAndActiveTrueOrderByIdAsc(
                        farmer.getRegion().getId()
                );

        if (officers.isEmpty()) {
            throw new IllegalStateException(
                    "No active agriculture officer is available for region: "
                            + farmer.getRegion().getName()
            );
        }

        Officer assignedOfficer = officers.get(0);

        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .farmer(farmer)
                .assignedOfficer(assignedOfficer)
                .crop(request.crop().trim())
                .symptoms(request.symptoms().trim())
                .photoReference(blankToNull(request.photoReference()))
                .status(OPEN)
                .createdAt(now)
                .timerStartedAt(now)
                .updatedAt(now)
                .escalated(false)
                .build();

        return ticketRepository.save(ticket);
    }

    @Transactional
    public List<Ticket> getAllTickets() {

        refreshEscalations();

        return ticketRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Ticket getTicket(Long id) {

        refreshEscalations();

        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id: " + id
                        ));
    }

    // ================= UPDATE TICKET =================

    @Transactional
    public Ticket updateTicket(
            Long id,
            TicketUpdateRequest request) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id: " + id
                        ));

        if (CLOSED.equals(ticket.getStatus())) {
            throw new IllegalStateException(
                    "Closed ticket cannot be updated. Reopen the ticket first."
            );
        }

        ticket.setCrop(request.crop().trim());
        ticket.setSymptoms(request.symptoms().trim());
        ticket.setPhotoReference(
                blankToNull(request.photoReference())
        );
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    // ================= DELETE TICKET =================

    @Transactional
    public void deleteTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id: " + id
                        ));

        ticketRepository.delete(ticket);
    }

    // ================= CLOSE TICKET =================

    @Transactional
    public Ticket closeTicket(
            Long id,
            CloseTicketRequest request) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id: " + id
                        ));

        if (CLOSED.equals(ticket.getStatus())) {
            throw new IllegalStateException(
                    "Ticket is already closed."
            );
        }

        if (!ticket.getAssignedOfficer()
                .getId()
                .equals(request.officerId())) {

            throw new IllegalStateException(
                    "Only the assigned officer can close this ticket."
            );
        }

        Officer officer = officerRepository.findById(
                        request.officerId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Officer not found with id: "
                                        + request.officerId()
                        ));

        if (!Boolean.TRUE.equals(officer.getActive())) {
            throw new IllegalStateException(
                    "Assigned officer is not active."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        ticket.setRecommendation(
                request.recommendation().trim()
        );

        ticket.setStatus(CLOSED);
        ticket.setClosedAt(now);
        ticket.setUpdatedAt(now);
        ticket.setEscalated(false);

        return ticketRepository.save(ticket);
    }

    // ================= REOPEN TICKET =================

    @Transactional
    public Ticket reopenTicket(
            Long id,
            Long farmerId) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id: " + id
                        ));

        if (!ticket.getFarmer()
                .getId()
                .equals(farmerId)) {

            throw new IllegalStateException(
                    "Only the farmer who owns the ticket can reopen it."
            );
        }

        if (!CLOSED.equals(ticket.getStatus())) {
            throw new IllegalStateException(
                    "Only a closed ticket can be reopened."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        ticket.setStatus(OPEN);
        ticket.setReopenedAt(now);

        // Reopening resets the 48-hour escalation timer
        ticket.setTimerStartedAt(now);

        ticket.setUpdatedAt(now);
        ticket.setClosedAt(null);
        ticket.setRecommendation(null);
        ticket.setEscalated(false);

        return ticketRepository.save(ticket);
    }

    // ================= ESCALATION =================

    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void refreshEscalations() {

        LocalDateTime cutoff =
                LocalDateTime.now()
                        .minusHours(ESCALATION_HOURS);

        List<Ticket> overdueTickets =
                ticketRepository
                        .findByStatusNotAndTimerStartedAtBeforeOrderByTimerStartedAtAsc(
                                CLOSED,
                                cutoff
                        );

        for (Ticket ticket : overdueTickets) {

            ticket.setEscalated(true);
            ticket.setUpdatedAt(LocalDateTime.now());
        }

        if (!overdueTickets.isEmpty()) {
            ticketRepository.saveAll(overdueTickets);
        }
    }

    // ================= GET ESCALATED TICKETS =================

    public List<Ticket> getEscalatedTickets() {

        refreshEscalations();

        LocalDateTime cutoff =
                LocalDateTime.now()
                        .minusHours(ESCALATION_HOURS);

        return ticketRepository
                .findByStatusNotAndTimerStartedAtBeforeOrderByTimerStartedAtAsc(
                        CLOSED,
                        cutoff
                );
    }

    // ================= HELPER =================

    private String blankToNull(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}