// AnimalController.java

package com.techeer.abandoneddog.openapi.controller;

import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.openapi.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnimalController {

    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/api/v1/animals")
    public ResponseEntity<List<PetInfo>> getAnimals(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "1000") String numOfRows) {

        animalService.fetchAllAnimals();
        List<PetInfo> allAnimals = animalService.fetchAnimals(pageNo, numOfRows);
        return allAnimals.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(allAnimals);
    }
}
