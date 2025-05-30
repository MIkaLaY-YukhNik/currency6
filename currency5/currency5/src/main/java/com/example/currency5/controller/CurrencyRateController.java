package com.example.currency5.controller;

import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.service.CurrencyRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currency-rates")
public class CurrencyRateController {

    @Autowired
    private CurrencyRateService currencyRateService;

    @GetMapping
    public List<CurrencyRate> getAllCurrencyRates() {
        return currencyRateService.getAllCurrencyRates();
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyRate> getCurrencyRateByCode(@PathVariable String code) {
        return currencyRateService.getCurrencyRateByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CurrencyRate createCurrencyRate(@RequestBody CurrencyRate currencyRate) {
        return currencyRateService.createCurrencyRate(currencyRate);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CurrencyRate>> createBulkCurrencyRates(@RequestBody List<CurrencyRate> rates) {
        List<CurrencyRate> createdRates = currencyRateService.createBulkCurrencyRates(rates);
        return ResponseEntity.ok(createdRates);
    }

    @PutMapping("/{code}")
    public ResponseEntity<CurrencyRate> updateCurrencyRate(@PathVariable String code, @RequestBody CurrencyRate currencyRateDetails) {
        try {
            CurrencyRate updatedRate = currencyRateService.updateCurrencyRate(code, currencyRateDetails);
            return ResponseEntity.ok(updatedRate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrencyRate(@PathVariable String code) {
        try {
            currencyRateService.deleteCurrencyRate(code);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateRatesFromOpenExchange() {
        currencyRateService.updateRatesFromOpenExchange();
        return ResponseEntity.ok("Currency rates updated successfully");
    }
}
