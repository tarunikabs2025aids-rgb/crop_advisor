package com.example.cropadvisor.repository;

import com.example.cropadvisor.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfficerRepository extends JpaRepository<Officer, Long> {

    @Query("""
            SELECT o
            FROM Officer o
            WHERE o.region.id = :regionId
            AND o.active = true
            ORDER BY o.id ASC
            """)
    List<Officer> findByRegionIdAndActiveTrueOrderByIdAsc(
            @Param("regionId") Long regionId
    );

    boolean existsByEmailIgnoreCase(String email);
}