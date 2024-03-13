package com.agileactors.cryptoapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.agileactors.cryptoapi.domain.CryptoPriceSummary;
import com.agileactors.cryptoapi.service.CryptoService;

@RestController
@RequestMapping("/cryptos")
public class CryptoController implements CryptoControllerApi {

    private final CryptoService cryptoService;

    CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public List<CryptoPriceSummary> all() {
        return cryptoService.findAll();
    }

    public CryptoPriceSummary one(String symbol) {
        return cryptoService.findOne(symbol)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Could not find crypto " + symbol));
    }

    public CryptoPriceSummary date(LocalDate date) {
        return cryptoService.findHighestRange(date)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No data available for " + date));
    }
    
}
