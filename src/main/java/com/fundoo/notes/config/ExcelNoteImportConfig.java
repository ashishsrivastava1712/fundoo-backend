
package com.fundoo.notes.config;

/*
 * Class Name  : ExcelNoteImportConfig
 * Description : Configures Spring Batch job for importing notes
 *               from Excel files into the database.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

import com.fundoo.notes.dto.request.NoteExcelRow;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.Iterator;

@Configuration
public class ExcelNoteImportConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(ExcelNoteImportConfig.class);

    @Bean
    @StepScope
    public ItemReader<NoteExcelRow> excelNoteReader(
            @Value("#{jobParameters['filePath']}") String filePath) throws Exception {

        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        DataFormatter formatter = new DataFormatter();

        // Skip Excel header row.
        if (rows.hasNext()) {
            rows.next();
        }

        return () -> {
            while (rows.hasNext()) {
                Row row = rows.next();

                String title = formatter.formatCellValue(
                        row.getCell(0)).trim();

                String description = formatter.formatCellValue(
                        row.getCell(1)).trim();

                // Skip completely empty rows.
                if (title.isEmpty() && description.isEmpty()) {
                    continue;
                }

                return new NoteExcelRow(title, description);
            }

            workbook.close();
            return null;
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<NoteExcelRow, Note> excelNoteProcessor(
            UserRepository userRepository,
            @Value("#{jobParameters['userId']}") Long userId) {

        return row -> {

            if (row.title() == null || row.title().isBlank()) {
                logger.warn("Skipping Excel row: title is missing");
                return null;
            }

            if (row.title().length() > 255) {
                logger.warn("Skipping Excel row: title exceeds 255 characters");
                return null;
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found"));

            return new Note(
                    row.title().trim(),
                    row.description(),
                    user
            );
        };
    }

    @Bean
    public ItemWriter<Note> excelNoteWriter(
            NoteRepository noteRepository) {

        return chunk -> {
            noteRepository.saveAll(chunk.getItems());

            logger.info(
                    "Successfully imported {} notes",
                    chunk.size()
            );
        };
    }

    @Bean
    public Step excelNoteImportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<NoteExcelRow> excelNoteReader,
            ItemProcessor<NoteExcelRow, Note> excelNoteProcessor,
            ItemWriter<Note> excelNoteWriter) {

        return new StepBuilder("excelNoteImportStep", jobRepository)
                .<NoteExcelRow, Note>chunk(20, transactionManager)
                .reader(excelNoteReader)
                .processor(excelNoteProcessor)
                .writer(excelNoteWriter)
                .build();
    }

    @Bean
    public Job excelNoteImportJob(
            JobRepository jobRepository,
            Step excelNoteImportStep) {

        return new JobBuilder("excelNoteImportJob", jobRepository)
                .start(excelNoteImportStep)
                .build();
    }
}
