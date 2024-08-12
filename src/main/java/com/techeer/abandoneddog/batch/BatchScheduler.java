package com.techeer.abandoneddog.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.Job;


@Component
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;


    @Autowired
    private Job importUserJob; // Job 빈 주입

    @Scheduled(fixedRate = 600000) // 10분 간격
    public void perform() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobParameter", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(importUserJob, jobParameters);
    }
}
