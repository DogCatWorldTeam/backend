package com.techeer.abandoneddog.shelter.entity;

import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.global.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.*;

import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE shelter_id = ?")
@Where(clause = "deleted = false")
@Table(name = "shelter")
public class Shelter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelter_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;        // 보호소 이름

    @Column(nullable = false, length = 14)
    private String phoneNumber; // 보호소 전화번호

    @Column(nullable = false, length = 200)
    private String address;     // 보호소 주소

    @Column(nullable = true)
    private Double latitude;    // 위도

    @Column(nullable = true)
    private Double longitude;   // 경도

    @OneToMany(mappedBy = "shelter")
    private List<PetInfo> pets = new ArrayList<>(); // 이 보호소에 속한 펫 정보

    public Shelter(Long id, String name, String phoneNumber, String address, List<PetInfo> pets, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.pets = pets;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
