package com.techeer.abandoneddog.animal.entity;


import com.techeer.abandoneddog.animal.Dto.PetInfoRequestDto;
import com.techeer.abandoneddog.shelter.entity.Shelter;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shelter_id", nullable = true)
    private Shelter shelter;

    @Column(name = "pet_name")
    private String petName;

    private String desertionNo;

    private String thumbnailImage; // 사진 축소판

    @Column
    @Enumerated(EnumType.STRING)
    private PetType petType;

    private String breed; //품종

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Float weight;

    private LocalDate happenDt; //접수일

    private String happenPlace; //발견장소

    private String color; //색상

    private int age;

    private String noticeNo; //공고번호

    private LocalDate noticeSdt; //공고시작일

    private LocalDate noticeEdt; //공고종료일

    private String profileImage; //이미지

    @Enumerated(EnumType.STRING)
    private State processState; //상태

    @Enumerated(EnumType.STRING)
    @Column(name = "neuter", length = 10)
    private Neuter neuter; //중성화여부 Y : 예, N : 아니오, U : 미상

    private String specialMark; //특징

    private String title;

    private String address;

    private String content;

    private boolean isPublicApi; // 공공API 사용 여부

    private String orgNm;        // 관할기관

    private String chargeNm;     // 담당자

    private String officetel;    // 담당자연락처



    public void update(PetInfoRequestDto dto) {

//         shelterId=dto.getShelterId();
//         petName=dto.getPetName();
         petType=dto.getPetType();
         breed=dto.getBreed();
        gender=dto.getGender();
        weight=dto.getWeight();

    }
}
