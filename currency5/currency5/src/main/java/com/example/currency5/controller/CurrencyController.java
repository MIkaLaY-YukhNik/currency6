package com.example.currency5.controller;

import com.example.currency5.dto.CurrencyConversionRequest;
import com.example.currency5.model.CurrencyResponse;
import com.example.currency5.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<CurrencyResponse> convertCurrency(@Valid @RequestBody CurrencyConversionRequest request) {
        try {
            CurrencyResponse response = currencyService.convertCurrency(
                    request.getAmount(),
                    request.getFromCurrency(),
                    request.getToCurrency());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/bulk-convert")
    public ResponseEntity<List<CurrencyResponse>> bulkConvertCurrency(
            @Valid @RequestBody List<CurrencyConversionRequest> requests) {
        List<CurrencyResponse> responses = requests.stream()
                .map(request -> currencyService.convertCurrency(
                        request.getAmount(),
                        request.getFromCurrency(),
                        request.getToCurrency()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
