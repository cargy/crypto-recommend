package com.agileactors.cryptoimporter.processor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.agileactors.cryptoimporter.domain.CryptoPrice;
import com.agileactors.cryptoimporter.domain.CryptoFile;

public class CryptoFileProcessor implements ItemProcessor<CryptoFile, CryptoPrice>{

    @Override
    @Nullable
    public CryptoPrice process(CryptoFile item) {
        return new CryptoPrice(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(item.timestamp()), ZoneOffset.UTC),
                item.symbol(),
                item.price()
        );
    }
    
}
