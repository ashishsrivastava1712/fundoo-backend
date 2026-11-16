
package com.fundoo.notes.controller;

/*
 * Class Name  : ExcelImportController
 * Description : Accepts Excel uploads and launches the
 *               Spring Batch note import job.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

import com.fundoo.notes.entity.User;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.TokenService;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class ExcelImportController {

    private final JobLauncher jobLauncher;
    private final Job excelNoteImportJob;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public ExcelImportController(
            JobLauncher jobLauncher,
            @Qualifier("excelNoteImportJob") Job excelNoteImportJob,
            TokenService tokenService,
            UserRepository userRepository) {

        this.jobLauncher = jobLauncher;
        this.excelNoteImportJob = excelNoteImportJob;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/import/notes")
    public ResponseEntity<Map<String, Object>> importNotes(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file) throws Exception {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid authorization header"));
        }

        String token = authorization.substring(7);

        if (!tokenService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        String email = tokenService.extractSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please upload an Excel file"));
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Only .xlsx files are supported"));
        }

        Path tempFile = Files.createTempFile("fundoo-notes-import-", ".xlsx");

        try {
            file.transferTo(tempFile);

            JobExecution execution = jobLauncher.run(
                    excelNoteImportJob,
                    new JobParametersBuilder()
                            .addString("filePath", tempFile.toAbsolutePath().toString())
                            .addLong("userId", user.getId())
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Excel import job executed",
                    "jobName", execution.getJobInstance().getJobName(),
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
