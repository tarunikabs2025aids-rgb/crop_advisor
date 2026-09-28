package com.example.cropadvisor.service;

import com.example.cropadvisor.dto.OfficerRequest;
import com.example.cropadvisor.entity.Officer;
import com.example.cropadvisor.entity.Region;
import com.example.cropadvisor.repository.OfficerRepository;
import com.example.cropadvisor.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final RegionRepository regionRepository;

    public Officer create(OfficerRequest request) {

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Region not found with id: " + request.regionId()
                        )
                );

        Officer officer = new Officer();

        officer.setName(request.name());
        officer.setEmail(request.email());
        officer.setPhone(request.phone());
        officer.setRegion(region);
        officer.setActive(true);

        return officerRepository.save(officer);
    }

    public List<Officer> getAll() {
        return officerRepository.findAll();
    }

    public Officer getById(Long id) {
        return officerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Officer not found with id: " + id
                        )
                );
    }

    public Officer update(Long id, OfficerRequest request) {

        Officer existingOfficer = getById(id);

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Region not found with id: " + request.regionId()
                        )
                );

        existingOfficer.setName(request.name());
        existingOfficer.setEmail(request.email());
        existingOfficer.setPhone(request.phone());
        existingOfficer.setRegion(region);

        return officerRepository.save(existingOfficer);
    }

    public void delete(Long id) {

        Officer officer = getById(id);

        officerRepository.delete(officer);
    }
}