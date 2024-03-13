package com.agileactors.cryptoimporter.domain;

import java.math.BigDecimal;

public record CryptoFile(long timestamp, String symbol, BigDecimal price) {
    
}
