package com.example.cropadvisor.repository;

import com.example.cropadvisor.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
