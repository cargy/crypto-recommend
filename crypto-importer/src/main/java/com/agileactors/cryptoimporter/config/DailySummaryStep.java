package com.agileactors.cryptoimporter.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.agileactors.cryptoimporter.domain.CryptoSummary;

@Configuration
public class DailySummaryStep {

    @Value("${crypto.summary.read-chunk-size}")
    private int readChunkSize;

    @Bean
    public Step dailySummary(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                             ItemReader<CryptoSummary> dailySummaryReader, ItemWriter<CryptoSummary> summaryWriter) {
        return new StepBuilder("dailySummary", jobRepository)
                .<CryptoSummary, CryptoSummary> chunk(readChunkSize, transactionManager)
                .reader(dailySummaryReader)
                .writer(summaryWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<CryptoSummary> dailySummaryReader(DataSource dataSource,
                                                                  @Value("#{jobParameters[crypto_symbol]}") String crypto) {
        String sql = """
                SELECT
                    symbol,
                    'Daily'::periodicity,
                    period,
                    oldest_price,
                    newest_price,
                    min_price,
                    max_price,
                    (max_price - min_price) / min_price AS normalized_range
                FROM crypto_prices_daily_view
                WHERE symbol = ?
                """;
        return new JdbcCursorItemReaderBuilder<CryptoSummary>()
                .name("monthlySummaryReader")
                .dataSource(dataSource)
                .sql(sql)
                .queryArguments(crypto)
                .rowMapper(new DataClassRowMapper<>(CryptoSummary.class))
                .build();
    }

}
