
package com.fundoo.notes.controller;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job noteProcessingJob;

    public BatchJobController(
            JobLauncher jobLauncher,
            Job noteProcessingJob) {
        this.jobLauncher = jobLauncher;
        this.noteProcessingJob = noteProcessingJob;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runBatchJob()
            throws Exception {

        JobExecution execution = jobLauncher.run(
                noteProcessingJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters()
        );

        return ResponseEntity.ok(Map.of(
                "jobName", execution.getJobInstance().getJobName(),
                "executionId", execution.getId(),
                "status", execution.getStatus().toString()
        ));
    }
}
