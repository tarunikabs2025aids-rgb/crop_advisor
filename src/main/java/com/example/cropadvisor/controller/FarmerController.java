package com.example.cropadvisor.controller;

import com.example.cropadvisor.dto.FarmerRequest;
import com.example.cropadvisor.entity.Farmer;
import com.example.cropadvisor.service.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping
    public ResponseEntity<Farmer> create(
            @Valid @RequestBody FarmerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(farmerService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Farmer>> getAll() {
        return ResponseEntity.ok(farmerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Farmer> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                farmerService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Farmer> update(
            @PathVariable Long id,
            @Valid @RequestBody FarmerRequest request) {

        return ResponseEntity.ok(
                farmerService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        farmerService.delete(id);

        return ResponseEntity.noContent().build();
    }
}