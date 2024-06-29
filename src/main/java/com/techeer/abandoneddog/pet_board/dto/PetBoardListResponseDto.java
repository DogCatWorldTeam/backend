package com.techeer.abandoneddog.pet_board.dto;

import com.techeer.abandoneddog.bookmark.dto.BookmarkResponseDto;
import com.techeer.abandoneddog.bookmark.entity.Bookmark;
import com.techeer.abandoneddog.pet_board.entity.PetBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetBoardListResponseDto {
    private Page<PetBoardResponseDto> petBoards;
    private List<BookmarkResponseDto> bookmarks;

    public static PetBoardListResponseDto fromEntities(Page<PetBoard> petBoards, List<Bookmark> bookmarks) {
        Page<PetBoardResponseDto> petBoardResponseDtos = petBoards.map(PetBoardResponseDto::fromEntity);
        List<BookmarkResponseDto> bookmarkResponseDtos = bookmarks.stream()
                .map(BookmarkResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PetBoardListResponseDto(petBoardResponseDtos, bookmarkResponseDtos);
    }

    public static PetBoardListResponseDto fromPetBoards(Page<PetBoard> petBoards) {
        Page<PetBoardResponseDto> petBoardResponseDtos = petBoards.map(PetBoardResponseDto::fromEntity);
        return new PetBoardListResponseDto(petBoardResponseDtos, null);
    }
}