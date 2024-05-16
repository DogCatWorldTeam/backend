package com.techeer.abandoneddog.shelter.service;

import com.techeer.abandoneddog.shelter.dto.ShelterResponseDto;
import com.techeer.abandoneddog.shelter.entity.Shelter;
import com.techeer.abandoneddog.shelter.repository.ShelterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShelterService {

    @Autowired
    private ShelterRepository shelterRepository;

    public Shelter saveShelter(String name, String phoneNumber, String address) {
        Optional<Shelter> existingShelter = shelterRepository.findByNameAndPhoneNumberAndAddress(name, phoneNumber, address);
        return existingShelter.orElseGet(() -> shelterRepository.save(new Shelter(null, name, phoneNumber, address, new ArrayList<>(), null, null)));
    }

    public List<ShelterResponseDto> findAllShelters() {
        return shelterRepository.findAll().stream()
                .map(shelter -> new ShelterResponseDto(
                        shelter.getId(),
                        shelter.getName(),
                        shelter.getPhoneNumber(),
                        shelter.getAddress(),
                        shelter.getLatitude(),
                        shelter.getLongitude()))
                .collect(Collectors.toList());
    }
}
