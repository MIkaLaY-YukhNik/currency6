package com.example.currency5.service;

import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.model.CurrencyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private CurrencyService currencyService;

    private CurrencyRate mockFromRate;
    private CurrencyRate mockToRate;

    @BeforeEach
    void setUp() {
        mockFromRate = mock(CurrencyRate.class);
        when(mockFromRate.getCurrencyCode()).thenReturn("USD");
        when(mockFromRate.getRate()).thenReturn(1.0);

        mockToRate = mock(CurrencyRate.class);
        when(mockToRate.getCurrencyCode()).thenReturn("EUR");
        when(mockToRate.getRate()).thenReturn(0.95);
    }

    @Test
    void convertCurrency_ShouldConvertCurrencySuccessfully() {
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.of(mockFromRate));
        when(currencyRateService.getCurrencyRateByCode("EUR")).thenReturn(Optional.of(mockToRate));

        CurrencyResponse result = currencyService.convertCurrency(100.0, "USD", "EUR");
        assertEquals("USD", result.getFromCurrency()); // Исправлено: getBase() -> getFromCurrency()
        assertEquals(100.0, result.getAmount());
        assertEquals("EUR", result.getToCurrency());
        assertEquals(95.0, result.getConvertedAmount(), 0.01); // 100 * 0.95
        assertEquals(0.95, result.getExchangeRate(), 0.01); // 0.95 / 1.0
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
        verify(currencyRateService, times(1)).getCurrencyRateByCode("EUR");
    }

    @Test
    void convertCurrency_ShouldThrowExceptionWhenFromCurrencyNotFound() {
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> currencyService.convertCurrency(100.0, "USD", "EUR"));
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
    }
}