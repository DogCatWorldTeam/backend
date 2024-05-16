package com.techeer.abandoneddog.shelter.repository;

import com.techeer.abandoneddog.shelter.entity.Shelter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Optional<Shelter> findByNameAndPhoneNumberAndAddress(String name, String phoneNumber, String address);
}
