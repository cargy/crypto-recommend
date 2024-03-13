package com.agileactors.cryptoapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.agileactors.cryptoapi.domain.CryptoPriceSummary;
import com.agileactors.cryptoapi.domain.Periodicity;
import com.agileactors.cryptoapi.repository.CryptoPriceSummaryRepository;

@Component
public class CryptoService {
 
    private final CryptoPriceSummaryRepository cryptoPriceSummaryRepository;

    CryptoService(CryptoPriceSummaryRepository cryptoPriceSummaryRepository) {
        this.cryptoPriceSummaryRepository = cryptoPriceSummaryRepository;
    }

    public List<CryptoPriceSummary> findAll() {
        return cryptoPriceSummaryRepository.findByPeriodicityOrderByNormalizedRangeDesc(Periodicity.Monthly);
    }

    public Optional<CryptoPriceSummary> findOne(String symbol) {
        return cryptoPriceSummaryRepository.findByPeriodicityAndSymbol(Periodicity.Monthly, symbol);
    }

    public Optional<CryptoPriceSummary> findHighestRange(LocalDate date) {
        return cryptoPriceSummaryRepository.findFirstByPeriodicityAndPeriodOrderByNormalizedRangeDesc(Periodicity.Daily, date);
    }
    
}
