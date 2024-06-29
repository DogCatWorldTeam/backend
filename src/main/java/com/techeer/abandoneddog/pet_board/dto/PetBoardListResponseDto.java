package com.techeer.abandoneddog.pet_board.dto;

import com.techeer.abandoneddog.bookmark.dto.BookmarkResponseDto;
import com.techeer.abandoneddog.bookmark.entity.Bookmark;
import com.techeer.abandoneddog.pet_board.entity.PetBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetBoardListResponseDto {
    private List<PetBoardResponseDto> petBoards;
    private List<BookmarkResponseDto> bookmarks;

    public static PetBoardListResponseDto fromEntities(List<PetBoard> petBoards, List<Bookmark> bookmarks) {
        List<PetBoardResponseDto> petBoardResponseDtos = petBoards.stream()
                .map(PetBoardResponseDto::fromEntity)
                .collect(Collectors.toList());
        List<BookmarkResponseDto> bookmarkResponseDtos = bookmarks.stream()
                .map(BookmarkResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PetBoardListResponseDto(petBoardResponseDtos, bookmarkResponseDtos);
    }

    public static PetBoardListResponseDto fromPetBoards(List<PetBoard> petBoards) {
        List<PetBoardResponseDto> petBoardResponseDtos = petBoards.stream()
                .map(PetBoardResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PetBoardListResponseDto(petBoardResponseDtos, null);
    }
}