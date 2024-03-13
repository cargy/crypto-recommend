package com.agileactors.cryptoimporter.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CryptoPrice(LocalDateTime timestamp, String symbol, BigDecimal price) {
    
}
