package com.agileactors.cryptoapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agileactors.cryptoapi.domain.CryptoPriceSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Past;

@RestController
@RequestMapping("/default")
@Tag(name = "crypto-api", description = "The Crypto Recommendations API")
public interface CryptoControllerApi {

    @Operation(summary = "Get all cryptos", description = "Get all Cryptos, sorted by their monthly normalized range.", tags = { "crypto-api" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CryptoPriceSummary.class))))
    })
    @GetMapping(produces = { "application/json" })
    List<CryptoPriceSummary> all();

    @Operation(summary = "Find crypto by symbol", description = "Returns a single crypto with it's monthly oldest/newest/min/max values.",
            tags = { "crypto-api" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CryptoPriceSummary.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Could not find crypto ", content = @Content) })
    @GetMapping("/{symbol}")
    CryptoPriceSummary one(@Parameter(description = "The symbol of the crypto to be fetched", required = true)
                           @PathVariable String symbol);

    @Operation(summary = "Find highest crypto normalized range", description = "Return the crypto with the highest normalized range for a specific day.",
            tags = { "crypto-api" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CryptoPriceSummary.class))),
            @ApiResponse(responseCode = "400", description = "No data available", content = @Content) })
    @GetMapping("/highest-range/{date}")
    CryptoPriceSummary date(@Parameter(description = "The data, in YYYY-MM-DD format, for which the highest normalized range crypto will be returned.", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Past LocalDate date);



}
