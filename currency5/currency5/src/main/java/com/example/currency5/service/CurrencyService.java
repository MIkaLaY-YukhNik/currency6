package com.example.currency5.service;

import com.example.currency5.model.CurrencyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private static final String API_KEY = "your_openexchangerates_api_key";
    private static final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=" + API_KEY;
    private Map<String, Double> exchangeRates = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        fetchExchangeRates();
    }

    private void fetchExchangeRates() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(API_URL, String.class);
            Map<String, Object> json = objectMapper.readValue(response, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Double> rates = (Map<String, Double>) json.get("rates");
            if (rates != null) {
                exchangeRates.putAll(rates);
            }
            exchangeRates.putIfAbsent("BYN", 1.0);
        } catch (Exception e) {
            exchangeRates.put("USD", 3.25);
            exchangeRates.put("EUR", 3.50);
            exchangeRates.put("BYN", 1.0);
        }
    }

    public CurrencyResponse convertCurrency(Double amount, String fromCurrency, String toCurrency) {
        if (amount == null || amount < 0 || fromCurrency == null || toCurrency == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        Double rateFrom = exchangeRates.get(from);
        Double rateTo = exchangeRates.get(to);
        if (rateFrom == null || rateTo == null) {
            throw new IllegalStateException("Unsupported currency: " + (rateFrom == null ? from : to));
        }
        Double exchangeRate = rateTo / rateFrom;
        Double convertedAmount = amount * exchangeRate;
        return new CurrencyResponse(amount, from, to, convertedAmount, exchangeRate);
    }
}