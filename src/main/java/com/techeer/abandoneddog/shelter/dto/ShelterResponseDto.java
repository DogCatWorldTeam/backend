package com.techeer.abandoneddog.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShelterResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
}
