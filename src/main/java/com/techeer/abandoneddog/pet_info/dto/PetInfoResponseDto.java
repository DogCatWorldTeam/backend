package com.techeer.abandoneddog.pet_info.dto;


import com.techeer.abandoneddog.pet_info.entity.Gender;
import com.techeer.abandoneddog.pet_info.entity.PetType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetInfoResponseDto {

    private int shelterId;
    private String petName;
    private PetType petType;
    private String breed;
    private Gender gender;
    private Float weight;





}

