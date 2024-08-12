package com.techeer.abandoneddog.animal.Controller;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techeer.abandoneddog.animal.PetInfoDto.PetInfoResponseDto;
import com.techeer.abandoneddog.animal.entity.PetInfo;
import com.techeer.abandoneddog.animal.service.PetInfoService;

import jakarta.persistence.EntityNotFoundException;

@RequestMapping("/api/v1/petinfo")
@RestController
public class AnimalInfoController {

	private final PetInfoService petInfoService;

	public AnimalInfoController(PetInfoService petInfoService) {
		this.petInfoService = petInfoService;
	}

	@GetMapping
	public ResponseEntity<Page<PetInfo>> getAllPetInfos(
		@RequestParam(name = "page", required = false, defaultValue = "0") int page,
		@RequestParam(name = "size", required = false, defaultValue = "10") int size) {
		try {
			Page<PetInfo> petInfos = petInfoService.getAllPetInfos(page, size);
			return ResponseEntity.ok(petInfos);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

//	@GetMapping("/update")//test용
//	public void update() {
//		petInfoService.updatePetInfoDaily();
//
//	}
@Autowired
private JobLauncher jobLauncher;

	@Autowired
	private Job importUserJob;

	@GetMapping("/launchjob")
	public String handle() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis()).toJobParameters();
		jobLauncher.run(importUserJob, jobParameters);
		return "Batch job has been invoked";
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getPetInfo(@PathVariable Long id) {
		try {
			PetInfoResponseDto petInfoDto = petInfoService.getPetInfoById(id);
			return ResponseEntity.ok(petInfoDto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("펫 정보를 찾을 수 없습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 정보를 조회하는 동안 오류가 발생했습니다.");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePetInfo(@PathVariable Long id) {
		try {
			petInfoService.deletePetInfo(id);
			return ResponseEntity.ok("펫 정보를 삭제했습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("펫 정보를 삭제할 수 없습니다.");
		}
	}
}
