package com.techeer.abandoneddog.animal.Dto;

import com.techeer.abandoneddog.animal.entity.Gender;
import com.techeer.abandoneddog.animal.entity.Neuter;
import com.techeer.abandoneddog.animal.entity.State;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PetInfoCreateDto {
    private String title;
    private String petName;
    private Gender gender;
    private int age;
    private Float weight;
    private String breed;
    private Neuter neuter;
    private String address;
    private String content;
    private String thumbnailImage;
    private String profileImage;
    private State processState;
    private LocalDate noticeSdt;
    private LocalDate noticeEdt;
    @Builder.Default
    private boolean isPublicApi = false;
}