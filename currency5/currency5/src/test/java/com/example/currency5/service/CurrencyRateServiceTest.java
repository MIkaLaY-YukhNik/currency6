package com.example.currency5.service;

import com.example.currency5.dto.CurrencyRateBulkDTO;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    private CurrencyRate mockRate1;
    private CurrencyRate mockRate2;

    @BeforeEach
    void setUp() {
        mockRate1 = mock(CurrencyRate.class);
        when(mockRate1.getCurrencyCode()).thenReturn("USD");
        when(mockRate1.getRate()).thenReturn(1.0);
        when(mockRate1.getLastUpdated()).thenReturn(LocalDateTime.now());
        when(mockRate1.getSource()).thenReturn("OpenExchangeRates");

        mockRate2 = mock(CurrencyRate.class);
        when(mockRate2.getCurrencyCode()).thenReturn("EUR");
        when(mockRate2.getRate()).thenReturn(0.95);
        when(mockRate2.getLastUpdated()).thenReturn(LocalDateTime.now());
        when(mockRate2.getSource()).thenReturn("OpenExchangeRates");
    }

    @Test
    void getAllCurrencyRates_ShouldReturnAllRates() {
        when(currencyRateRepository.findAll()).thenReturn(Arrays.asList(mockRate1, mockRate2));
        List<CurrencyRate> result = currencyRateService.getAllCurrencyRates();
        assertEquals(2, result.size());
        assertTrue(result.contains(mockRate1));
        assertTrue(result.contains(mockRate2));
        verify(currencyRateRepository, times(1)).findAll();
    }

    @Test
    void getCurrencyRateByCode_ShouldReturnRateWhenExists() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(mockRate1));
        Optional<CurrencyRate> result = currencyRateService.getCurrencyRateByCode("USD");
        assertTrue(result.isPresent());
        assertEquals(mockRate1, result.get());
        verify(currencyRateRepository, times(1)).findById("USD");
    }

    @Test
    void createCurrencyRate_ShouldSaveAndReturnRate() {
        when(currencyRateRepository.save(mockRate1)).thenReturn(mockRate1);
        CurrencyRate result = currencyRateService.createCurrencyRate(mockRate1);
        assertEquals(mockRate1, result);
        verify(currencyRateRepository, times(1)).save(mockRate1);
    }

    @Test
    void createBulkCurrencyRates_ShouldSaveAllRates() {
        CurrencyRateBulkDTO mockBulkDTO = mock(CurrencyRateBulkDTO.class);
        CurrencyRateBulkDTO.CurrencyRateDTO mockRateDTO1 = mock(CurrencyRateBulkDTO.CurrencyRateDTO.class);
        when(mockRateDTO1.getCurrencyCode()).thenReturn("USD");
        when(mockRateDTO1.getRate()).thenReturn(1.0);
        when(mockRateDTO1.getLastUpdated()).thenReturn(LocalDateTime.now());
        when(mockRateDTO1.getSource()).thenReturn("OpenExchangeRates");

        CurrencyRateBulkDTO.CurrencyRateDTO mockRateDTO2 = mock(CurrencyRateBulkDTO.CurrencyRateDTO.class);
        when(mockRateDTO2.getCurrencyCode()).thenReturn("EUR");
        when(mockRateDTO2.getRate()).thenReturn(0.95);
        when(mockRateDTO2.getLastUpdated()).thenReturn(LocalDateTime.now());
        when(mockRateDTO2.getSource()).thenReturn("OpenExchangeRates");

        when(mockBulkDTO.getCurrencyRates()).thenReturn(Arrays.asList(mockRateDTO1, mockRateDTO2));
        when(currencyRateRepository.saveAll(anyList())).thenReturn(Arrays.asList(mockRate1, mockRate2));


    }

    @Test
    void updateCurrencyRate_ShouldUpdateAndReturnRate() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(mockRate1));
        when(currencyRateRepository.save(mockRate1)).thenReturn(mockRate1);
        CurrencyRate mockUpdatedRate = mock(CurrencyRate.class);
        when(mockUpdatedRate.getCurrencyCode()).thenReturn("USD");
        when(mockUpdatedRate.getRate()).thenReturn(1.05);
        CurrencyRate result = currencyRateService.updateCurrencyRate("USD", mockUpdatedRate);
        assertEquals(1.05, result.getRate());
        verify(currencyRateRepository, times(1)).findById("USD");
        verify(currencyRateRepository, times(1)).save(mockRate1);
    }

    @Test
    void deleteCurrencyRate_ShouldDeleteRate() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(mockRate1));
        currencyRateService.deleteCurrencyRate("USD");
        verify(currencyRateRepository, times(1)).findById("USD");
        verify(currencyRateRepository, times(1)).delete(mockRate1);
    }
}