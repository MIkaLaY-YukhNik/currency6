package com.example.currency5.service;

import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurrencyRateService {

    @Value("${openexchangerates.app.id}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepository;

    @Autowired
    public CurrencyRateService(RestTemplate restTemplate, CurrencyRateRepository currencyRateRepository) {
        this.restTemplate = restTemplate;
        this.currencyRateRepository = currencyRateRepository;
    }

    @Cacheable(value = "currencyRateCache")
    public List<CurrencyRate> getAllCurrencyRates() {
        updateRatesFromOpenExchange(); // Автоматическое обновление перед возвратом
        return currencyRateRepository.findAll();
    }

    @Cacheable(value = "currencyRateCache", key = "#currencyCode")
    public Optional<CurrencyRate> getCurrencyRateByCode(String currencyCode) {
        Optional<CurrencyRate> rate = currencyRateRepository.findById(currencyCode);
        if (rate.isEmpty()) {
            updateRatesFromOpenExchange(); // Обновление, если курс не найден
            rate = currencyRateRepository.findById(currencyCode);
        }
        return rate;
    }


    @CachePut(value = "currencyRateCache", key = "#result.currencyCode")
    public CurrencyRate createCurrencyRate(CurrencyRate currencyRate) {
        return currencyRateRepository.save(currencyRate);
    }

    @Transactional
    @CacheEvict(value = "currencyRateCache", allEntries = true)
    public List<CurrencyRate> createBulkCurrencyRates(List<CurrencyRate> rates) {
        return currencyRateRepository.saveAll(rates);
    }

    @Transactional
    @CachePut(value = "currencyRateCache", key = "#currencyCode")
    public CurrencyRate updateCurrencyRate(String currencyCode, CurrencyRate currencyRateDetails) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRate.setRate(currencyRateDetails.getRate());
        currencyRate.setLastUpdated(LocalDateTime.now());
        currencyRate.setSource(currencyRateDetails.getSource());
        return currencyRateRepository.save(currencyRate);
    }

    @Transactional
    @CacheEvict(value = "currencyRateCache", key = "#currencyCode")
    public void deleteCurrencyRate(String currencyCode) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRateRepository.delete(currencyRate);
    }

    public void updateRatesFromOpenExchange() {
        String url = "https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) body.get("rates");
                List<CurrencyRate> updatedRates = rates.entrySet().stream()
                        .map(entry -> {
                            CurrencyRate rate = new CurrencyRate();
                            rate.setCurrencyCode(entry.getKey());
                            rate.setRate(entry.getValue());
                            rate.setLastUpdated(LocalDateTime.now());
                            rate.setSource("OpenExchangeRates");
                            return rate;
                        })
                        .collect(Collectors.toList());
                currencyRateRepository.saveAll(updatedRates);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch rates from Open Exchange Rates: " + e.getMessage());
        }
    }
}