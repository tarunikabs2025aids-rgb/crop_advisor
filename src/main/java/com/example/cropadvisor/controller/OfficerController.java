package com.example.cropadvisor.controller;

import com.example.cropadvisor.dto.OfficerRequest;
import com.example.cropadvisor.entity.Officer;
import com.example.cropadvisor.service.OfficerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;

    @PostMapping
    public ResponseEntity<Officer> create(
            @Valid @RequestBody OfficerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(officerService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Officer>> getAll() {
        return ResponseEntity.ok(officerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Officer> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                officerService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Officer> update(
            @PathVariable Long id,
            @Valid @RequestBody OfficerRequest request) {

        return ResponseEntity.ok(
                officerService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        officerService.delete(id);

        return ResponseEntity.noContent().build();
    }
}