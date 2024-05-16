package com.techeer.abandoneddog.animal.repository;

import com.techeer.abandoneddog.animal.entity.PetInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetInfoRepository extends JpaRepository<PetInfo,Long> {
    List<PetInfo> findByIsPublicApi(boolean isPublicApi);
}