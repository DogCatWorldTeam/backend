package com.techeer.abandoneddog.shelter.controller;

import com.techeer.abandoneddog.shelter.dto.ShelterResponseDto;
import com.techeer.abandoneddog.shelter.entity.Shelter;
import com.techeer.abandoneddog.shelter.service.ShelterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @GetMapping
    public ResponseEntity<List<ShelterResponseDto>> getAllShelters() {
        List<ShelterResponseDto> shelters = shelterService.findAllShelters();
        return ResponseEntity.ok(shelters);
    }
}
