package com.agileactors.cryptoimporter.tranformer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

import java.io.File;
import java.util.Objects;

public class FileMessageToJobRequest {
    private static final String FILE_NAME = "file_name";
    private static final String FILE_PATH = "file_path";
    private static final String CRYPTO_SYMBOL = "crypto_symbol";
    private static final String TIMESTAMP = "timestamp";
    private Job job;

    public void setJob(Job job) {
        this.job = job;
    }

    @Transformer
    public JobLaunchRequest toRequest(Message<File> message) {
        String fileName = Objects.requireNonNull(message.getHeaders().get(FILE_NAME, String.class));
        String cryptoSymbol = fileName.contains("_") ? fileName.split("_")[0] : null;

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addString(FILE_NAME, fileName)
                .addString(FILE_PATH, message.getPayload().getAbsolutePath())
                .addString(CRYPTO_SYMBOL, Objects.requireNonNull(cryptoSymbol))
                .addLong(TIMESTAMP, Objects.requireNonNull(message.getHeaders().getTimestamp()));

        return new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());
    }
}
