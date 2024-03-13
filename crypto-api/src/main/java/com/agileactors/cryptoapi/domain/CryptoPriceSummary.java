package com.agileactors.cryptoapi.domain;

import java.math.BigDecimal;

public record CryptoPriceSummary(
        String symbol,
        String periodicity,
        String period,
        BigDecimal oldestPrice,
        BigDecimal newestPrice,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Float normalizedRange
) {
}
