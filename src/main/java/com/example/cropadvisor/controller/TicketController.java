package com.example.cropadvisor.controller;

import com.example.cropadvisor.dto.CloseTicketRequest;
import com.example.cropadvisor.dto.TicketRequest;
import com.example.cropadvisor.dto.TicketUpdateRequest;
import com.example.cropadvisor.entity.Ticket;
import com.example.cropadvisor.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @Valid @RequestBody TicketRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.createTicket(request));
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(
                ticketService.getAllTickets()
        );
    }

    @GetMapping("/escalated")
    public ResponseEntity<List<Ticket>> getEscalatedTickets() {
        return ResponseEntity.ok(
                ticketService.getEscalatedTickets()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ticketService.getTicket(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request) {

        return ResponseEntity.ok(
                ticketService.updateTicket(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id) {

        ticketService.deleteTicket(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Ticket> closeTicket(
            @PathVariable Long id,
            @Valid @RequestBody CloseTicketRequest request) {

        return ResponseEntity.ok(
                ticketService.closeTicket(id, request)
        );
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<Ticket> reopenTicket(
            @PathVariable Long id,
            @RequestParam Long farmerId) {

        return ResponseEntity.ok(
                ticketService.reopenTicket(id, farmerId)
        );
    }
}