package com.example.cropadvisor.controller;

import com.example.cropadvisor.entity.Region;
import com.example.cropadvisor.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<Region> create(@Valid @RequestBody Region region) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regionService.create(region));
    }

    @GetMapping
    public ResponseEntity<List<Region>> getAll() {
        return ResponseEntity.ok(regionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Region> update(
            @PathVariable Long id,
            @Valid @RequestBody Region region) {

        return ResponseEntity.ok(
                regionService.update(id, region)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        regionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}