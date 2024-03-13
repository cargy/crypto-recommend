package com.agileactors.cryptoimporter.domain;

import java.math.BigDecimal;

public record CryptoSummary(
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
