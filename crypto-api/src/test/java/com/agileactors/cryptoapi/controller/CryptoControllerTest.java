package com.agileactors.cryptoapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.agileactors.cryptoapi.domain.CryptoPriceSummary;
import com.agileactors.cryptoapi.service.CryptoService;

@SpringBootTest
@AutoConfigureMockMvc
class CryptoControllerTest {
    private static final List<CryptoPriceSummary> CRYPTOS = Arrays.asList(
            new CryptoPriceSummary("CRY1", "Monthly", "20240310", BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.5), BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.25), 0.25f),
            new CryptoPriceSummary("CRY2", "Weekly", "20240310", BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.5), BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.25), 0.25f)
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoService cryptoService;

    @Test
    void testGetAllCryptos() throws Exception {
        when(cryptoService.findAll())
                .thenReturn(CRYPTOS);

        mockMvc.perform(get("/cryptos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].symbol", contains(CRYPTOS.get(0).symbol(), CRYPTOS.get(1).symbol())))
                .andExpect(jsonPath("$[*].periodicity", contains(CRYPTOS.get(0).periodicity(), CRYPTOS.get(1).periodicity())))
                .andExpect(jsonPath("$[*].period", contains(CRYPTOS.get(0).period(), CRYPTOS.get(1).period())))
                .andExpect(jsonPath("$[*].oldestPrice", contains(CRYPTOS.get(0).oldestPrice().doubleValue(), CRYPTOS.get(1).oldestPrice().doubleValue())))
                .andExpect(jsonPath("$[*].newestPrice", contains(CRYPTOS.get(0).newestPrice().doubleValue(), CRYPTOS.get(1).newestPrice().doubleValue())))
                .andExpect(jsonPath("$[*].minPrice", contains(CRYPTOS.get(0).minPrice().doubleValue(), CRYPTOS.get(1).minPrice().doubleValue())))
                .andExpect(jsonPath("$[*].maxPrice", contains(CRYPTOS.get(0).maxPrice().doubleValue(), CRYPTOS.get(1).maxPrice().doubleValue())))
                .andExpect(jsonPath("$[*].normalizedRange", contains(CRYPTOS.get(0).normalizedRange().doubleValue(), CRYPTOS.get(1).normalizedRange().doubleValue())));
    }

    @Test
    void testGetCryptoFound() throws Exception {
        when(cryptoService.findOne("CRY1"))
                .thenReturn(Optional.of(CRYPTOS.get(0)));

        mockMvc.perform(get("/cryptos/CRY1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value(CRYPTOS.get(0).symbol()))
                .andExpect(jsonPath("$.periodicity").value(CRYPTOS.get(0).periodicity()))
                .andExpect(jsonPath("$.period").value(CRYPTOS.get(0).period()))
                .andExpect(jsonPath("$.oldestPrice").value(CRYPTOS.get(0).oldestPrice().doubleValue()))
                .andExpect(jsonPath("$.newestPrice").value(CRYPTOS.get(0).newestPrice().doubleValue()))
                .andExpect(jsonPath("$.minPrice").value(CRYPTOS.get(0).minPrice().doubleValue()))
                .andExpect(jsonPath("$.maxPrice").value(CRYPTOS.get(0).maxPrice().doubleValue()))
                .andExpect(jsonPath("$.normalizedRange").value(CRYPTOS.get(0).normalizedRange()));
    }

    @Test
    void testGetCryptoNotFound() throws Exception {
        when(cryptoService.findOne("UNKNOWN"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/cryptos/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testCryptoWithHighestRange() throws Exception {
        LocalDate date = LocalDate.parse("2024-03-10");
        when(cryptoService.findHighestRange(date))
                .thenReturn(Optional.of(CRYPTOS.get(0)));

        mockMvc.perform(get("/cryptos/highest-range/2024-03-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value(CRYPTOS.get(0).symbol()))
                .andExpect(jsonPath("$.periodicity").value(CRYPTOS.get(0).periodicity()))
                .andExpect(jsonPath("$.period").value(CRYPTOS.get(0).period()))
                .andExpect(jsonPath("$.oldestPrice").value(CRYPTOS.get(0).oldestPrice().doubleValue()))
                .andExpect(jsonPath("$.newestPrice").value(CRYPTOS.get(0).newestPrice().doubleValue()))
                .andExpect(jsonPath("$.minPrice").value(CRYPTOS.get(0).minPrice().doubleValue()))
                .andExpect(jsonPath("$.maxPrice").value(CRYPTOS.get(0).maxPrice().doubleValue()))
                .andExpect(jsonPath("$.normalizedRange").value(CRYPTOS.get(0).normalizedRange()));
    }

    @Test
    void testCryptoWithHighestRangeNoData() throws Exception {
        LocalDate date = LocalDate.parse("2030-03-10");
        when(cryptoService.findHighestRange(date))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/cryptos/highest-range/2024-03-10"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}