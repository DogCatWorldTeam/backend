package com.techeer.abandoneddog.animal.service;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.techeer.abandoneddog.animal.PetInfoDto.PetInfoResponseDto;
import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.animal.repository.PetInfoRepository;
import com.techeer.abandoneddog.shelter.entity.Shelter;
import com.techeer.abandoneddog.shelter.repository.ShelterRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetInfoService {

    private final PetInfoRepository petInfoRepository;
    private final ShelterRepository shelterRepository;

    public PetInfoService(PetInfoRepository petInfoRepository, ShelterRepository shelterRepository) {
        this.petInfoRepository = petInfoRepository;
        this.shelterRepository = shelterRepository;
    }

    public PetInfoResponseDto getPetInfoById(Long id) {
        PetInfo petInfo = petInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PetInfo not found with id: " + id));
        return PetInfoResponseDto.fromEntity(petInfo);
    }

    public void deletePetInfo(Long id) {
        PetInfo entity = petInfoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found"));
        petInfoRepository.delete(entity);
    }

    public Page<PetInfo> getAllPetInfos(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return petInfoRepository.findAll(pageRequest);
    }

    public List<Long> findAllDesertionNos() {
        return petInfoRepository.findAllDesertionNos();
    }

    public Optional<Shelter> findByCareNm(String careNm) {
        return shelterRepository.findByCareNm(careNm);
    }
}
