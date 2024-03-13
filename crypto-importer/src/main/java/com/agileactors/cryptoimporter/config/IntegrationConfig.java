package com.agileactors.cryptoimporter.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.LoggingHandler;

import com.agileactors.cryptoimporter.tranformer.FileMessageToJobRequest;

@Configuration
public class IntegrationConfig {
    private static final String CSV_FILTER = "*.csv";

    @Value("${crypto.bucket.path}")
    private String bucketPath;

    protected DirectChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow integrationFlow(JobRepository jobRepository, Job importCryptoJob) {
        return IntegrationFlow
                .from(fileReadingMessageSource(), c -> c.poller(Pollers.fixedDelay(1000)))
                .channel(inputChannel())
                .transform(fileMessageToJobRequest(importCryptoJob))
                .handle(jobLaunchingGateway(jobRepository))
                .log(LoggingHandler.Level.WARN)
                .handle(jobExecution -> {
                    System.out.println(jobExecution.getPayload());
                })
                .get();
    }

    @Bean
    public FileInboundChannelAdapterSpec fileReadingMessageSource() {
        return Files.inboundAdapter(new File(bucketPath))
                .preventDuplicates(true)
                .filter(new SimplePatternFileListFilter(CSV_FILTER))
                .useWatchService(true)
                .watchEvents(FileReadingMessageSource.WatchEventType.CREATE);
    }

    @Bean
    public FileMessageToJobRequest fileMessageToJobRequest(Job importCryptoJob) {
        FileMessageToJobRequest fileMessageToJobRequest = new FileMessageToJobRequest();
        fileMessageToJobRequest.setJob(importCryptoJob);
        return fileMessageToJobRequest;
    }

    @Bean
    public JobLaunchingGateway jobLaunchingGateway(JobRepository jobRepository) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());
        return new JobLaunchingGateway(jobLauncher);
    }

}
