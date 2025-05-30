package com.example.currency5.service;

import com.example.currency5.model.CurrencyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class CurrencyServiceTest {

    private CurrencyService currencyService;

    @BeforeEach
    public void setUp() {
        currencyService = new CurrencyService();
        // Инициализируем курсы валют для теста вручную
        Map<String, Double> dummyRates = new HashMap<>();
        dummyRates.put("USD", 1.0);
        dummyRates.put("EUR", 0.9);
        dummyRates.put("BYN", 2.5);
        try {
            java.lang.reflect.Field field = currencyService.getClass().getDeclaredField("exchangeRates");
            field.setAccessible(true);
            field.set(currencyService, dummyRates);
        } catch (Exception e) {
            fail("Не удалось задать тестовые курсы валют: " + e.getMessage());
        }
    }

    @Test
    public void testConvertCurrencyValid() {
        CurrencyResponse response = currencyService.convertCurrency(100.0, "USD", "EUR");
        assertNotNull(response);
        assertEquals(90.0, response.getConvertedAmount(), 0.001);
    }

    @Test
    public void testConvertCurrencyInvalidAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                currencyService.convertCurrency(-10.0, "USD", "EUR"));
        String expectedMessage = "Invalid input parameters";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testConvertCurrencyUnsupportedCurrency() {
        Exception exception = assertThrows(IllegalStateException.class, () ->
                currencyService.convertCurrency(100.0, "XYZ", "EUR"));
        String expectedMessage = "Unsupported currency: XYZ";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
