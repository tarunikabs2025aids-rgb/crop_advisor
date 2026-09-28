package com.example.cropadvisor.service;

import com.example.cropadvisor.dto.FarmerRequest;
import com.example.cropadvisor.entity.Farmer;
import com.example.cropadvisor.entity.Region;
import com.example.cropadvisor.repository.FarmerRepository;
import com.example.cropadvisor.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final RegionRepository regionRepository;

    public Farmer create(FarmerRequest request) {

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Region not found with id: " + request.regionId()
                        ));

        Farmer farmer = new Farmer();

        farmer.setName(request.name());
        farmer.setPhone(request.phone());
        farmer.setEmail(request.email());
        farmer.setRegion(region);

        return farmerRepository.save(farmer);
    }

    public List<Farmer> getAll() {
        return farmerRepository.findAll();
    }

    public Farmer getById(Long id) {

        return farmerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Farmer not found with id: " + id
                        ));
    }

    public Farmer update(
            Long id,
            FarmerRequest request) {

        Farmer existingFarmer = getById(id);

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Region not found with id: " + request.regionId()
                        ));

        existingFarmer.setName(request.name());
        existingFarmer.setPhone(request.phone());
        existingFarmer.setEmail(request.email());
        existingFarmer.setRegion(region);

        return farmerRepository.save(existingFarmer);
    }

    public void delete(Long id) {

        Farmer farmer = getById(id);

        farmerRepository.delete(farmer);
    }
}