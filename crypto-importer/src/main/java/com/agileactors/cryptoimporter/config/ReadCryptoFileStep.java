package com.agileactors.cryptoimporter.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.agileactors.cryptoimporter.domain.CryptoFile;
import com.agileactors.cryptoimporter.domain.CryptoPrice;
import com.agileactors.cryptoimporter.processor.CryptoFileProcessor;

@Configuration
public class ReadCryptoFileStep {

    @Value("${crypto.bucket.read-chunk-size}")
    private int readChunkSize;

    @Bean
    public Step readFile(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                         FlatFileItemReader<CryptoFile> csvFileReader, CryptoFileProcessor processor,
                         JdbcBatchItemWriter<CryptoPrice> cryptoPriceWriter) {
        return new StepBuilder("readCryptoFile", jobRepository)
                .<CryptoFile, CryptoPrice> chunk(readChunkSize, transactionManager)
                .reader(csvFileReader)
                .processor(processor)
                .writer(cryptoPriceWriter)
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<CryptoFile> csvFileReader(@Value("#{jobParameters[file_path]}") String filePath) {
        return new FlatFileItemReaderBuilder<CryptoFile>()
                .name("csvFileReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1) // skip CSV header
                .delimited()
                .names("timestamp", "symbol", "price")
                .targetType(CryptoFile.class)
                .build();
    }

    @Bean
    public CryptoFileProcessor processor() {
        return new CryptoFileProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CryptoPrice> cryptoPriceWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CryptoPrice>()
                .sql("INSERT INTO crypto_prices (timestamp, symbol, price) VALUES (:timestamp, :symbol, :price)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
