
package com.fundoo.notes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class BatchConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    public ItemReader<String> itemReader() {
        return new ListItemReader<>(List.of(
                "Fundoo Notes",
                "Spring Boot",
                "Spring Batch",
                "Redis Cache",
                "RabbitMQ Messaging"
        ));
    }

    @Bean
    public ItemProcessor<String, String> itemProcessor() {
        return item -> {
            logger.info("Processing record: {}", item);
            return item.toUpperCase();
        };
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return chunk -> {
            for (String item : chunk.getItems()) {
                logger.info("Writing processed record: {}", item);
            }
        };
    }

    @Bean
    public Step noteProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<String> itemReader,
            ItemProcessor<String, String> itemProcessor,
            ItemWriter<String> itemWriter) {

        return new StepBuilder("noteProcessingStep", jobRepository)
                .<String, String>chunk(2, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job noteProcessingJob(
            JobRepository jobRepository,
            Step noteProcessingStep) {

        return new JobBuilder("noteProcessingJob", jobRepository)
                .start(noteProcessingStep)
                .build();
    }
}
