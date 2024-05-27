package com.techeer.abandoneddog.pet_board.service;

import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.animal.repository.PetInfoRepository;
import com.techeer.abandoneddog.pet_board.dto.PetBoardRequestDto;
import com.techeer.abandoneddog.pet_board.dto.PetBoardResponseDto;
import com.techeer.abandoneddog.pet_board.entity.PetBoard;
import com.techeer.abandoneddog.pet_board.repository.PetBoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PetBoardService {
    private final PetBoardRepository petBoardRepository;
    private final PetInfoRepository petInfoRepository;


    @Transactional
    public Long createPetBoard(PetBoardRequestDto petBoardRequestDto) {
       // petInfoRepository.save()
        petInfoRepository.save(petBoardRequestDto.getPetInfo());
        return petBoardRepository.save(petBoardRequestDto.toEntity()).getPetBoardId();
    }

    @Transactional
    public PetBoardResponseDto updatePetBoard(Long petBoardId, PetBoardRequestDto requestDto) {
        PetBoard petBoard = petBoardRepository.findById(petBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + petBoardId));

        petBoard.update(requestDto);
        PetInfo petInfo = petBoard.getPetInfo();
        petInfo.update(requestDto.getPetInfo());


        return PetBoardResponseDto.fromEntity(petBoard);
    }

    @Transactional
    public PetBoardResponseDto getPetBoard(Long petBoardId) {
        PetBoard petBoard = petBoardRepository.findById(petBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + petBoardId));
        return PetBoardResponseDto.fromEntity(petBoard);
    }

    @Transactional
    public Page<PetBoardResponseDto> getPetBoards(Pageable pageable) {
        Page<PetBoard> petBoardPage = petBoardRepository.findAll(pageable);
        return petBoardPage.map(PetBoardResponseDto::fromEntity);
    }

    @Transactional
    public void deletePetBoard(Long petBoardId) {
        PetBoard petBoard = petBoardRepository.findById(petBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + petBoardId));
        PetInfo petInfo = petBoard.getPetInfo();

        petInfoRepository.delete(petInfo);
        petBoardRepository.delete(petBoard);
    }
}
