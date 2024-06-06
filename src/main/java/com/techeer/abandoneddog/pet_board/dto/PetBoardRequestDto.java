package com.techeer.abandoneddog.pet_board.dto;

import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.pet_board.entity.PetBoard;
import com.techeer.abandoneddog.pet_board.entity.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetBoardRequestDto {
    private String title;
    private String description;
    private PetInfo petInfo;

    public PetBoard toEntity() {
        return PetBoard.builder()
                .title(title)
                .description(description)
                .petInfo(petInfo)
                .status(Status.fromProcessState(petInfo.getProcessState()))
                .build();
    }
}
