package com.agileactors.cryptoapi.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.agileactors.cryptoapi.exception.CryptoNotFoundException;
import com.agileactors.cryptoapi.exception.NoDataAvailableException;

public class GlobalExceptionHandler {
    
    @ResponseBody
    @ExceptionHandler(CryptoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String cryptoNotFoundHandler(CryptoNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoDataAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noDataAvailableHandler(NoDataAvailableException ex) {
        return ex.getMessage(); 
    }
    
}
