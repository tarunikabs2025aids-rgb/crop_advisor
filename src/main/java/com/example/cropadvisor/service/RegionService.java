package com.example.cropadvisor.service;

import com.example.cropadvisor.entity.Region;
import com.example.cropadvisor.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public Region create(Region region) {
        return regionRepository.save(region);
    }

    public List<Region> getAll() {
        return regionRepository.findAll();
    }

    public Region getById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Region not found with id: " + id));
    }

    public Region update(Long id, Region updatedRegion) {

        Region existingRegion = getById(id);

        existingRegion.setName(updatedRegion.getName());

        return regionRepository.save(existingRegion);
    }

    public void delete(Long id) {

        Region region = getById(id);

        regionRepository.delete(region);
    }
}