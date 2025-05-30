package com.example.currency5.service;

import com.example.currency5.dto.ConversionHistoryBulkDTO;
import com.example.currency5.entity.ConversionHistory;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.entity.User;
import com.example.currency5.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversionHistoryServiceTest {

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    private ConversionHistory mockHistory;
    private User mockUser;
    private CurrencyRate mockRate;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn("testuser");

        mockRate = mock(CurrencyRate.class);
        when(mockRate.getCurrencyCode()).thenReturn("USD");
        when(mockRate.getRate()).thenReturn(1.0);

        mockHistory = mock(ConversionHistory.class);
        when(mockHistory.getId()).thenReturn(1L);
        when(mockHistory.getFromCurrency()).thenReturn("USD");
        when(mockHistory.getToCurrency()).thenReturn("EUR");
        when(mockHistory.getAmount()).thenReturn(100.0);
        when(mockHistory.getConvertedAmount()).thenReturn(95.0);
        when(mockHistory.getConvertedAt()).thenReturn(LocalDateTime.now());
        when(mockHistory.getUser()).thenReturn(mockUser);
        when(mockHistory.getCurrencyRates()).thenReturn(new HashSet<>(Arrays.asList(mockRate)));
    }

    @Test
    void getAllConversionHistories_ShouldReturnAllHistories() {
        when(conversionHistoryRepository.findAll()).thenReturn(Arrays.asList(mockHistory));
        List<ConversionHistory> result = conversionHistoryService.getAllConversionHistories();
        assertEquals(1, result.size());
        assertTrue(result.contains(mockHistory));
        verify(conversionHistoryRepository, times(1)).findAll();
    }

    @Test
    void getConversionHistoryById_ShouldReturnHistoryWhenExists() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(mockHistory));
        Optional<ConversionHistory> result = conversionHistoryService.getConversionHistoryById(1L);
        assertTrue(result.isPresent());
        assertEquals(mockHistory, result.get());
        verify(conversionHistoryRepository, times(1)).findById(1L);
    }

    @Test
    void createConversionHistory_ShouldSaveAndReturnHistory() {
        when(conversionHistoryRepository.save(mockHistory)).thenReturn(mockHistory);
        ConversionHistory result = conversionHistoryService.createConversionHistory(mockHistory);
        assertEquals(mockHistory, result);
        verify(conversionHistoryRepository, times(1)).save(mockHistory);
    }

    @Test
    void createBulkConversionHistories_ShouldSaveAllHistories() {
        ConversionHistoryBulkDTO mockBulkDTO = mock(ConversionHistoryBulkDTO.class);
        ConversionHistoryBulkDTO.ConversionHistoryDTO mockDTO1 = mock(ConversionHistoryBulkDTO.ConversionHistoryDTO.class);
        when(mockDTO1.getFromCurrency()).thenReturn("USD");
        when(mockDTO1.getToCurrency()).thenReturn("EUR");
        when(mockDTO1.getAmount()).thenReturn(100.0);
        when(mockDTO1.getConvertedAmount()).thenReturn(95.0);
        when(mockDTO1.getConvertedAt()).thenReturn(LocalDateTime.now());
        when(mockDTO1.getUserId()).thenReturn(1L);
        when(mockDTO1.getCurrencyRateCodes()).thenReturn(new HashSet<>(Arrays.asList("USD")));
        when(mockBulkDTO.getConversionHistories()).thenReturn(Arrays.asList(mockDTO1));

        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.of(mockRate));
        when(conversionHistoryRepository.saveAll(anyList())).thenReturn(Arrays.asList(mockHistory));

        List<ConversionHistory> result = conversionHistoryService.createBulkConversionHistories(mockBulkDTO);
        assertEquals(1, result.size());
        assertTrue(result.contains(mockHistory));
        verify(userService, times(1)).getUserById(1L);
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
        verify(conversionHistoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateConversionHistory_ShouldUpdateAndReturnHistory() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(mockHistory));
        when(conversionHistoryRepository.save(mockHistory)).thenReturn(mockHistory);
        ConversionHistory mockUpdatedHistory = mock(ConversionHistory.class);
        when(mockUpdatedHistory.getFromCurrency()).thenReturn("EUR");
        when(mockUpdatedHistory.getToCurrency()).thenReturn("USD");
        ConversionHistory result = conversionHistoryService.updateConversionHistory(1L, mockUpdatedHistory);
        assertEquals("EUR", result.getFromCurrency());
        verify(conversionHistoryRepository, times(1)).findById(1L);
        verify(conversionHistoryRepository, times(1)).save(mockHistory);
    }

    @Test
    void deleteConversionHistory_ShouldDeleteHistory() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(mockHistory));
        conversionHistoryService.deleteConversionHistory(1L);
        verify(conversionHistoryRepository, times(1)).findById(1L);
        verify(conversionHistoryRepository, times(1)).delete(mockHistory);
    }
}