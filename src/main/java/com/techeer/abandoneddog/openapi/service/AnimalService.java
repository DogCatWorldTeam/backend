package com.techeer.abandoneddog.openapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.abandoneddog.animal.entity.*;
import com.techeer.abandoneddog.animal.repository.PetInfoRepository;
import com.techeer.abandoneddog.shelter.entity.Shelter;
import com.techeer.abandoneddog.shelter.service.ShelterService;
import com.techeer.abandoneddog.openapi.util.AnimalDataExtractor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final ShelterService shelterService;
    private final PetInfoRepository petInfoRepository;
    private final RestTemplate restTemplate;
    private final String decodeServiceKey;

    public AnimalService(EntityManager entityManager,
                         ShelterService shelterService,
                         PetInfoRepository petInfoRepository,
                         RestTemplate restTemplate,
                         @Value("${api.serviceKey}") String decodeServiceKey) {
        this.entityManager = entityManager;
        this.shelterService = shelterService;
        this.petInfoRepository = petInfoRepository;
        this.restTemplate = restTemplate;
        this.decodeServiceKey = decodeServiceKey;
    }

    @Transactional
    public List<PetInfo> fetchAnimals(String pageNo, String numOfRows) {
        String encodedServiceKey;
        try {
            encodedServiceKey = URLEncoder.encode(decodeServiceKey, "UTF-8");
        } catch (Exception e) {
            System.out.println("Encoding failed: " + e.getMessage());
            return new ArrayList<>();
        }

        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://apis.data.go.kr/1543061/abandonmentPublicSrvc/abandonmentPublic")
                .queryParam("serviceKey", encodedServiceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        String jsonResponse = restTemplate.getForObject(uri, String.class);
        System.out.println(uri);

        AnimalDataExtractor.processAndDeleteResponseFile(jsonResponse);
        return parseAndSaveAnimals(jsonResponse);
    }

    @Transactional
    public List<PetInfo> parseAndSaveAnimals(String json) {
        List<PetInfo> pets = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.path("response").path("body").path("items").path("item");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    Shelter shelter = shelterService.saveShelter(
                            item.path("careNm").asText(),
                            item.path("careTel").asText(),
                            item.path("careAddr").asText()
                    );
                    PetInfo pet = AnimalDataExtractor.buildPetInfoFromJson(item, formatter, shelter);
                    pets.add(pet);
                }
            }

            persistPets(pets);

        } catch (Exception e) {
            System.err.println("Error parsing or saving animals: " + e.getMessage());
        }
        return pets;
    }

    @Transactional
    public void persistPets(List<PetInfo> pets) {
        int batchSize = 725;
        for (int i = 0; i < pets.size(); i++) {
            entityManager.persist(pets.get(i));
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }


    @Transactional
    public void fetchAllAnimals() {
        final int numOfRows = 1000;
        int pageNo = 1;
        List<PetInfo> pets;
        long fetchStartTime, fetchEndTime;

        do {
            fetchStartTime = System.currentTimeMillis();
            pets = fetchAnimals(String.valueOf(pageNo), String.valueOf(numOfRows));
            petInfoRepository.saveAll(pets);
            fetchEndTime = System.currentTimeMillis();

            System.out.println("Time taken to fetch and save page " + pageNo + ": " + (fetchEndTime - fetchStartTime) + " milliseconds");
            pageNo++;
        } while (!pets.isEmpty());
    }
}
