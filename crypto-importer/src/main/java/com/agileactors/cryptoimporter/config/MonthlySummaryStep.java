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
public class MonthlySummaryStep {

    @Value("${crypto.summary.read-chunk-size}")
    private int readChunkSize;

    @Bean
    public Step monthlySummary(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                               ItemReader<CryptoSummary> monthlySummaryReader, ItemWriter<CryptoSummary> summaryWriter) {
        return new StepBuilder("monthlySummary", jobRepository)
                .<CryptoSummary, CryptoSummary> chunk(readChunkSize, transactionManager)
                .reader(monthlySummaryReader)
                .writer(summaryWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<CryptoSummary> monthlySummaryReader(DataSource dataSource,
                                                                    @Value("#{jobParameters[crypto_symbol]}") String crypto) {
        String sql = """
                SELECT
                    symbol,
                    'Monthly'::periodicity,
                    period,
                    oldest_price,
                    newest_price,
                    min_price,
                    max_price,
                    (max_price - min_price) / min_price AS normalized_range
                FROM crypto_prices_monthly_view
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

    @Bean
    public JdbcBatchItemWriter<CryptoSummary> summaryWriter(DataSource dataSource) {
        String sql = """
                INSERT INTO crypto_price_summary (symbol, periodicity, period, oldest_price, newest_price, min_price, max_price, normalized_range)
                VALUES (:symbol, :periodicity::periodicity, :period, :oldestPrice, :newestPrice, :minPrice, :maxPrice, :normalizedRange)
                """;
        return new JdbcBatchItemWriterBuilder<CryptoSummary>()
                .sql(sql)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

}
