package com.agileactors.cryptoapi.exception;

public class CryptoNotFoundException extends RuntimeException {

    public CryptoNotFoundException(String symbol) {
        super("Counld not find crypto " + symbol);
    }  

}
