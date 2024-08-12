package com.techeer.abandoneddog.global.config;//package com.techeer.abandoneddog.global.config;

import com.techeer.abandoneddog.animal.repository.PetInfoRepository;
import com.techeer.abandoneddog.batch.PetInfoTasklet;
import com.techeer.abandoneddog.shelter.repository.ShelterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;


import com.techeer.abandoneddog.animal.repository.PetInfoRepository;
import com.techeer.abandoneddog.batch.PetInfoTasklet;

import com.techeer.abandoneddog.shelter.repository.ShelterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    @Value("${OPEN_API_SECRETKEY}")
    private String secretKey;

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      PetInfoRepository petInfoRepository, ShelterRepository shelterRepository) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new PetInfoTasklet(petInfoRepository, shelterRepository, secretKey), transactionManager)
                .build();
    }
}
