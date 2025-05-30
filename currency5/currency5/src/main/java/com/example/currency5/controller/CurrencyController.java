package com.example.currency5.controller;

import com.example.currency5.dto.CurrencyConversionRequest;
import com.example.currency5.model.CurrencyResponse;
import com.example.currency5.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<CurrencyResponse> convertCurrency(@Valid @RequestBody CurrencyConversionRequest request) {
        try {
            CurrencyResponse response = currencyService.convertCurrency(
                    request.getAmount(), request.getFromCurrency(), request.getToCurrency());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}