package com.agileactors.cryptoimporter.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agileactors.cryptoimporter.JobCompletionNotificationListener;

@Configuration
public class BatchConfig {

    @Bean
    public Job importCryptoJob(JobRepository jobRepository, Step readFile, Step monthlySummary, Step dailySummary,
                               JobCompletionNotificationListener listener) {
        return new JobBuilder("importCryptoJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(readFile)
                .next(monthlySummary)
                .next(dailySummary)
                .build();
    }
    


}
