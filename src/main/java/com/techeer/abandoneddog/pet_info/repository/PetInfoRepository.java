package com.techeer.abandoneddog.pet_info.repository;

import com.techeer.abandoneddog.pet_info.entity.PetInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetInfoRepository extends JpaRepository<PetInfo,Long> {

}