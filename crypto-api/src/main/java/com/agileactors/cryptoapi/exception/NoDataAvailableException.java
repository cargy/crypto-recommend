package com.agileactors.cryptoapi.exception;

import java.time.LocalDate;

public class NoDataAvailableException extends RuntimeException {

    public NoDataAvailableException(LocalDate date) {
        super("No data available for " + date);
    }
    
}
