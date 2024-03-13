package com.agileactors.cryptoapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.agileactors.cryptoapi.domain.CryptoPriceSummary;
import com.agileactors.cryptoapi.domain.Periodicity;

public interface CryptoPriceSummaryRepository  extends Repository<CryptoPriceSummary, String> {

    List<CryptoPriceSummary> findByPeriodicityOrderByNormalizedRangeDesc(Periodicity periodicity);

    Optional<CryptoPriceSummary> findByPeriodicityAndSymbol(Periodicity periodicity, String symbol);

    @Query("""
        SELECT *
        FROM crypto_price_summary
        WHERE periodicity = :periodicity
        AND period = TO_CHAR(:date::date, 'YYYYMMDD')
        ORDER BY normalized_range DESC
        LIMIT 1
    """)
    Optional<CryptoPriceSummary> findFirstByPeriodicityAndPeriodOrderByNormalizedRangeDesc(Periodicity periodicity, LocalDate date);
}
