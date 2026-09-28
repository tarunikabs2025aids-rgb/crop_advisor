package com.example.cropadvisor.repository;

import com.example.cropadvisor.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByIdDesc();

    List<Ticket> findByStatusNotAndTimerStartedAtBeforeOrderByTimerStartedAtAsc(
            String status,
            LocalDateTime cutoff
    );

    long countByStatus(String status);
}
