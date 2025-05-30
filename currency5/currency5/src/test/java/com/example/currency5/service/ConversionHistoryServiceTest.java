package com.example.currency5.service;

import com.example.currency5.dto.ConversionHistoryBulkDTO;
import com.example.currency5.dto.ConversionHistoryBulkDTO.ConversionHistoryDTO;
import com.example.currency5.entity.ConversionHistory;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.entity.User;
import com.example.currency5.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConversionHistoryServiceTest {

    private ConversionHistoryRepository historyRepository;
    private UserService userService;
    private CurrencyRateService currencyRateService;
    private ConversionHistoryService historyService;

    @BeforeEach
    public void setUp() {
        historyRepository = mock(ConversionHistoryRepository.class);
        userService = mock(UserService.class);
        currencyRateService = mock(CurrencyRateService.class);
        historyService = new ConversionHistoryService();
        try {
            java.lang.reflect.Field historyField = historyService.getClass().getDeclaredField("conversionHistoryRepository");
            historyField.setAccessible(true);
            historyField.set(historyService, historyRepository);
            java.lang.reflect.Field userField = historyService.getClass().getDeclaredField("userService");
            userField.setAccessible(true);
            userField.set(historyService, userService);
            java.lang.reflect.Field currencyField = historyService.getClass().getDeclaredField("currencyRateService");
            currencyField.setAccessible(true);
            currencyField.set(historyService, currencyRateService);
        } catch (Exception e) {
            fail("Не удалось инжектить зависимости через рефлексию: " + e.getMessage());
        }
    }

    @Test
    public void testCreateBulkConversionHistories() {
        ConversionHistoryBulkDTO bulkDTO = new ConversionHistoryBulkDTO();
        ConversionHistoryDTO dto = new ConversionHistoryDTO();
        dto.setFromCurrency("USD");
        dto.setToCurrency("EUR");
        dto.setAmount(100.0);
        dto.setConvertedAmount(90.0);
        dto.setConvertedAt(LocalDateTime.now());
        dto.setNotes("Test");
        dto.setStatus("COMPLETED");
        dto.setUserId(1L);
        dto.setCurrencyRateCodes(new HashSet<>(Arrays.asList("USD", "EUR")));
        bulkDTO.setConversionHistories(Arrays.asList(dto));

        User dummyUser = new User("testuser");
        dummyUser.setId(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(dummyUser));

        CurrencyRate rateUSD = new CurrencyRate();
        rateUSD.setCurrencyCode("USD");
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.of(rateUSD));

        CurrencyRate rateEUR = new CurrencyRate();
        rateEUR.setCurrencyCode("EUR");
        when(currencyRateService.getCurrencyRateByCode("EUR")).thenReturn(Optional.of(rateEUR));

        when(historyRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        java.util.List<ConversionHistory> results = historyService.createBulkConversionHistories(bulkDTO);

        assertNotNull(results);
        assertEquals(1, results.size());
        ConversionHistory created = results.get(0);
        assertEquals("USD", created.getFromCurrency());
        assertEquals("EUR", created.getToCurrency());
        verify(userService, times(1)).getUserById(1L);
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
        verify(currencyRateService, times(1)).getCurrencyRateByCode("EUR");
    }
}
